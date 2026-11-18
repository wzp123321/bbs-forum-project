/**
 * 真实模型适配器：POST EVAL_TARGET_URL
 * 请求体 { input, expected }；响应体需为 { output: string[] | string } 或 { answer: string[] | string }。
 */
const endpoint = process.env.EVAL_TARGET_URL;
if (!endpoint) throw new Error('EVAL_TARGET_URL is required for http adapter');

export const run = async (item) => {
  const response = await fetch(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ input: item.input, expected: item.expected }),
  });
  if (!response.ok) throw new Error(`HTTP ${response.status} from ${endpoint}`);
  const data = await response.json();
  return { output: data.output ?? data.answer ?? '', raw: data };
};
