/** 后端服务基础地址 (用于拼接 /upload/ 静态资源) */
export const API_BASE_URL: string =
  (import.meta.env.VITE_API_BASE_URL as string) || '';

/** 上传资源 URL 前缀 (来自后端 application.yml) */
export const UPLOAD_URL_PREFIX: string =
  (import.meta.env.VITE_UPLOAD_URL_PREFIX as string) || '/upload/';

/** 把后端返回的相对路径 (/upload/xxx) 拼接为可直接访问的完整 URL */
export function toAbsoluteUrl(url?: string): string {
  if (!url) return '';
  if (/^https?:\/\//i.test(url)) return url;
  return API_BASE_URL + url;
}
