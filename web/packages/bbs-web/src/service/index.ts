/**
 * 统一 axios 调用封装
 * 复用 public 包注册的拦截器 (token/前缀/401)
 * 业务码 200 直接返回 data,非 200 走 reject (与 bbs-admin 保持一致)
 */
import axios from 'axios';
import { ElMessage } from 'element-plus';
import type { R } from '@/apis/types';

const http = axios;

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
      return (body as unknown) as T;
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
