/**
 * 拼接 WebSocket URL
 * - 优先用 VITE_API_BASE_URL (后端服务完整地址) 切协议为 ws/wss
 * - 兜底: 用当前页面的 host
 */
export function resolveWsUrl(path: string): string {
  const base = (import.meta.env.VITE_API_BASE_URL as string) || `${location.protocol}//${location.host}`;
  const u = new URL(base);
  const scheme = u.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${scheme}//${u.host}${path}`;
}
