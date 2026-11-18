/**
 * 轻量前端监控：JS 错误、未处理 Promise 拒绝、页面性能。
 * 仅在配置 VITE_MONITOR_ENDPOINT 时上报；未配置时静默，不影响业务。
 */

interface MonitorEvent {
  type: 'js_error' | 'unhandled_rejection' | 'page_perf';
  message?: string;
  stack?: string;
  url: string;
  requestId?: string;
  [key: string]: unknown;
}

let initialized = false;

export const initMonitoring = (endpoint?: string): void => {
  if (initialized || typeof window === 'undefined') return;
  initialized = true;

  const target = endpoint || import.meta.env.VITE_MONITOR_ENDPOINT;
  if (!target) return;

  const send = (event: MonitorEvent): void => {
    const body = JSON.stringify({ ...event, occurredAt: new Date().toISOString() });
    if (navigator.sendBeacon) {
      navigator.sendBeacon(target, new Blob([body], { type: 'application/json' }));
    } else {
      void fetch(target, { method: 'POST', body, headers: { 'Content-Type': 'application/json' }, keepalive: true });
    }
  };

  window.addEventListener('error', (event) => {
    send({
      type: 'js_error',
      message: event.message,
      stack: event.error?.stack,
      url: location.href,
    });
  });

  window.addEventListener('unhandledrejection', (event) => {
    const reason = event.reason;
    send({
      type: 'unhandled_rejection',
      message: reason instanceof Error ? reason.message : String(reason),
      stack: reason instanceof Error ? reason.stack : undefined,
      url: location.href,
    });
  });

  window.addEventListener('load', () => {
    const nav = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming | undefined;
    if (!nav) return;
    send({
      type: 'page_perf',
      url: location.href,
      dns: Math.round(nav.domainLookupEnd - nav.domainLookupStart),
      tcp: Math.round(nav.connectEnd - nav.connectStart),
      ttfb: Math.round(nav.responseStart - nav.requestStart),
      domContentLoaded: Math.round(nav.domContentLoadedEventEnd - nav.startTime),
      load: Math.round(nav.loadEventEnd - nav.startTime),
    });
  });
};
