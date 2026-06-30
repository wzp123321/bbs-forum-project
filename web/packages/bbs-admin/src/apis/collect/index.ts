import http from '@/utils/request';
import { IResponseVO } from '../types';

/** 收藏帖子 */
export const collectApi = (postId: number) =>
  http.post<IResponseVO<void>>('/admin/collect/collect', { postId }).then((res) => res.data);

/** 取消收藏 */
export const cancelCollectApi = (postId: number) =>
  http.post<IResponseVO<void>>('/admin/collect/cancel', { postId }).then((res) => res.data);

/** 是否已收藏 */
export const collectStatusApi = (postId: number) =>
  http.post<IResponseVO<{ collected: boolean }>>('/admin/collect/status', { postId }).then((res) => res.data.data);
