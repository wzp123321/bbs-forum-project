# AI 评测脚手架

通用 LLM / RAG 评测骨架，放在本仓作为样板；可整体复制到 `ai-agent-cooking-sse` 等含模型调用的项目。无第三方依赖，Node.js 18+ 即可运行。

## 运行

```bash
cd eval
npm run eval                       # 离线 mock 适配器，验证评测链路
npm run eval:http                  # 调真实模型（需 EVAL_TARGET_URL）
node runner.mjs --threshold 0.8    # 自定义通过阈值
```

退出码：`Recall@K >= threshold` 时为 0，否则为 1，可直接作为 CI 门禁。

## 目录

| 文件 | 作用 |
| --- | --- |
| `datasets/*.jsonl` | 评测集，每行 `{id, input, expected, meta}` |
| `adapters/mock.mjs` | 确定性离线适配器，用于自测 |
| `adapters/http.mjs` | HTTP 适配器，POST `{input, expected}` 到 `EVAL_TARGET_URL` |
| `metrics.mjs` | `exactSetMatch` / `containsAll` / `recallAtK` / `similarity` |
| `runner.mjs` | 跑批 → 汇总 → 输出 `reports/latest.{json,md}` |
| `reports/` | 最近一次报告（可忽略或纳入趋势追踪） |

## 接入真实模型

1. 在 `datasets/` 增加 JSONL 用例，`expected` 为期望标签/答案列表。
2. 实现 `adapters/your-model.mjs`，导出 `async run(item) => { output }`。
3. `node runner.mjs --adapter adapters/your-model.mjs --dataset datasets/xxx.jsonl --threshold 0.8`。

HTTP 适配器约定：请求 `{input, expected}`，响应 `{output: string[] | string}`，也可用 `answer` 字段。

## 迁移到 AI 烹饪项目

复制整个 `eval/` 目录；把数据集换成菜谱问答/食材替换用例，适配器改为调用 Agent 的 SSE 或 HTTP 接口。指标层无需改动。建议同时保留 `mock` 适配器，保证 CI 在无 API Key 时仍能跑通。

## 指标说明

- `Recall@K`：期望项中有多少出现在前 K 个输出里，主门禁指标。
- `exactSetMatch`：集合完全一致，严格但易受同义词影响。
- `containsAll`：期望项是否全部命中。
- `similarity`：编辑距离相似度，用于开放式回答的粗判。
