import { http as rawHttp } from '@/service';
import type { AttachmentVO, UploadOptions } from './index.api';

/** 基础 URL (后端服务地址) */
const API_BASE = (import.meta.env.VITE_API_BASE_URL as string) || '';

/** 把相对路径 /upload/xxx 转成可直接展示的完整 URL */
export const resolveFileUrl = (url?: string): string => {
  if (!url) return '';
  if (/^https?:\/\//i.test(url)) return url;
  if (url.startsWith('/upload/')) {
    return API_BASE + url;
  }
  return url;
};

/** 上传附件 (走原始 axios,可自定义 onUploadProgress; 自动经过 service 注册的 token 拦截器) */
export const uploadAttachment = (file: File, opts: UploadOptions = {}): Promise<AttachmentVO> => {
  const baseURL = (import.meta.env.VITE_BASE_URL as string) || '';
  const form = new FormData();
  form.append('file', file);
  if (opts.bizType) {
    form.append('bizType', opts.bizType);
  }
  return rawHttp
    .post<{ code: number; message: string; data: AttachmentVO }>(`${baseURL}/admin/attachment/upload`, form, {
      onUploadProgress: (e) => {
        if (opts.onProgress && e.total) {
          opts.onProgress(Math.round((e.loaded * 100) / e.total));
        }
      },
    })
    .then((res) => {
      const body = res.data;
      if (body.code !== 200) {
        return Promise.reject(new Error(body.message || '上传失败'));
      }
      // 补全 url 为前端可访问的完整地址
      const att = body.data;
      if (att && att.url) {
        att.url = resolveFileUrl(att.url);
      }
      return att;
    });
};
