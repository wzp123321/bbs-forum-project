import fs from 'node:fs/promises';
import path from 'node:path';
import { pathToFileURL } from 'node:url';
import { exactSetMatch, containsAll, recallAtK, similarity } from './metrics.mjs';

const args = Object.fromEntries(
  process.argv.slice(2).map((raw, i, arr) => {
    if (!raw.startsWith('--')) return null;
    const next = arr[i + 1];
    return [raw.slice(2), next && !next.startsWith('--') ? next : 'true'];
  }).filter(Boolean),
);

const here = path.dirname(new URL(import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, '$1'));
const datasetPath = path.resolve(here, args.dataset || 'datasets/bbs-tag-smoke.jsonl');
const adapterPath = path.resolve(here, args.adapter || 'adapters/mock.mjs');
const outDir = path.resolve(here, args.out || 'reports');
const k = Number(args.k || 3);
const threshold = Number(args.threshold || 0.7);

const loadDataset = async (file) => {
  const text = await fs.readFile(file, 'utf8');
  return text.split(/\r?\n/).filter((line) => line.trim()).map((line, i) => {
    try {
      return JSON.parse(line);
    } catch (e) {
      throw new Error(`invalid JSONL at line ${i + 1}: ${e.message}`);
    }
  });
};

const dataset = await loadDataset(datasetPath);
const adapter = await import(pathToFileURL(adapterPath).href);
if (typeof adapter.run !== 'function') throw new Error(`adapter ${adapterPath} must export run(item)`);

const results = [];
for (const item of dataset) {
  const started = Date.now();
  const { output, raw } = await adapter.run(item);
  results.push({
    id: item.id,
    input: item.input,
    expected: item.expected,
    output,
    exact: exactSetMatch(output, item.expected),
    containsAll: containsAll(output, item.expected),
    recallAtK: recallAtK(output, item.expected, k),
    similarity: similarity(JSON.stringify(output), JSON.stringify(item.expected)),
    latencyMs: Date.now() - started,
    raw,
  });
}

const avg = (key) => results.reduce((sum, r) => sum + r[key], 0) / (results.length || 1);
const summary = {
  generatedAt: new Date().toISOString(),
  dataset: path.basename(datasetPath),
  adapter: path.basename(adapterPath),
  cases: results.length,
  exactMatchRate: Number(avg('exact').toFixed(3)),
  containsAllRate: Number(avg('containsAll').toFixed(3)),
  meanRecallAtK: Number(avg('recallAtK').toFixed(3)),
  meanSimilarity: Number(avg('similarity').toFixed(3)),
  avgLatencyMs: Math.round(avg('latencyMs')),
  threshold,
  passed: avg('recallAtK') >= threshold,
};

await fs.mkdir(outDir, { recursive: true });
await fs.writeFile(path.join(outDir, 'latest.json'), JSON.stringify({ summary, results }, null, 2), 'utf8');
const md = [
  '# AI 评测报告',
  '',
  `- 数据集: ${summary.dataset}`,
  `- 适配器: ${summary.adapter}`,
  `- 用例数: ${summary.cases}`,
  `- Recall@${k}: ${summary.meanRecallAtK}（阈值 ${threshold}）`,
  `- 完全匹配率: ${summary.exactMatchRate}`,
  `- 平均延迟: ${summary.avgLatencyMs}ms`,
  `- 结论: ${summary.passed ? 'PASS' : 'FAIL'}`,
  '',
  '| id | expected | output | recall | latency |',
  '| --- | --- | --- | --- | --- |',
  ...results.map((r) => `| ${r.id} | ${JSON.stringify(r.expected)} | ${JSON.stringify(r.output)} | ${r.recallAtK.toFixed(2)} | ${r.latencyMs}ms |`),
  '',
].join('\n');
await fs.writeFile(path.join(outDir, 'latest.md'), md, 'utf8');

console.log(md);
if (!summary.passed) process.exitCode = 1;
