/**
 * 统一 axios 调用封装
 * - 请求拦截器自动加 Authorization: Bearer <token>
 * - 业务码 200 直接返回 data,非 200 走 reject
 * - 生产环境通过 VITE_API_BASE_URL 配置 API 基础路径 (如 /api), 由 nginx 反代到后端
 */
import axios from 'axios';
import { ElMessage } from 'element-plus';
import type { R } from '@/apis/types';
import { tokenStore } from '@/utils';

const http = axios;

// API 基础路径: 生产从环境变量读取, 默认为空 (相对当前 host)
const API_BASE = (import.meta.env.VITE_API_BASE_URL as string | undefined) || '';
if (API_BASE) {
  http.defaults.baseURL = API_BASE;
}

// 请求拦截器: 注入 token
http.interceptors.request.use((config) => {
  const token = tokenStore.get();
  if (token) {
    config.headers = config.headers || {};
    (config.headers as Record<string, string>).Authorization = `Bearer ${token}`;
  }
  return config;
});

interface RequestOptions {
  params?: Record<string, unknown>;
  headers?: Record<string, string>;
  silent?: boolean;
}

function unwrap<T>(promise: Promise<{ data: R<T> }>, silent?: boolean): Promise<T> {
  return promise
    .then((res) => {
      const body = res.data;
      if (body && typeof body === 'object' && 'code' in body) {
        if (body.code === 200) {
          return body.data as T;
        }
        if (!silent) {
          ElMessage.error(body.message || `请求失败 (code=${body.code})`);
        }
        return Promise.reject(new Error(body.message || `code=${body.code}`));
      }
      // 非标准 R 协议,直接返回
      return body as unknown as T;
    })
    .catch((err) => {
      if (!silent) {
        ElMessage.error(err?.message || '网络异常');
      }
      throw err;
    });
}

const http2 = {
  get: <T = unknown>(url: string, opts: RequestOptions = {}) =>
    unwrap<T>(http.get<R<T>>(url, { params: opts.params, headers: opts.headers }), opts.silent),
  post: <T = unknown>(url: string, data?: unknown, opts: RequestOptions = {}) =>
    unwrap<T>(http.post<R<T>>(url, data, { params: opts.params, headers: opts.headers }), opts.silent),
  put: <T = unknown>(url: string, data?: unknown, opts: RequestOptions = {}) =>
    unwrap<T>(http.put<R<T>>(url, data, { params: opts.params, headers: opts.headers }), opts.silent),
  delete: <T = unknown>(url: string, opts: RequestOptions = {}) =>
    unwrap<T>(http.delete<R<T>>(url, { params: opts.params, headers: opts.headers }), opts.silent),
};

export default http2;

/** 暴露原始 axios (供需要自定义 headers/进度/类型 的场景使用,仍会经过上面注册的 token 拦截器) */
export { http };
