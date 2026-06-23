import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { tokenStore } from './storage';
import { message } from './message';
import type { R } from '@/apis/types';

const http = axios.create({
  baseURL: (import.meta.env.VITE_BASE_URL as string) ?? '',
  timeout: (import.meta.env.VITE_TIME_OUT as number) ?? 15000,
  headers: { 'Content-Type': 'application/json' },
});

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = tokenStore.get();
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`);
  }
  return config;
});

/** 处理 401 跳转登录 (抽出来避免在两个分支里重复) */
const handleUnauthorized = () => {
  tokenStore.clear();
  message.warning('登录已失效,请重新登录');
  if (window.location.pathname !== '/login') {
    window.location.href = '/login';
  }
};

http.interceptors.response.use(
  (res: AxiosResponse<R>) => {
    const body = res.data;
    if (!body || typeof body !== 'object' || !('code' in body)) {
      return res;
    }
    if (body.code === 200) {
      return res;
    }
    if (body.code === 401) {
      handleUnauthorized();
    } else {
      message.error(body.message || '请求失败');
    }
    return Promise.reject(new Error(body.message || `code=${body.code}`));
  },
  (error: AxiosError<R>) => {
    const status = error.response?.status;
    const msg = error.response?.data?.message || error.message;
    if (status === 401) {
      handleUnauthorized();
    } else {
      message.error(msg || '网络异常');
    }
    return Promise.reject(error);
  },
);

export default http;
