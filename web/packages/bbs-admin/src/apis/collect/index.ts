import http from '@/utils/request';
import type { R } from '../types';

/** 收藏帖子 */
export const collect = (postId: number) => http.post<R<void>>(`/admin/collect/${postId}`).then((res) => res.data);

/** 取消收藏 */
export const cancelCollect = (postId: number) =>
  http.delete<R<void>>(`/admin/collect/${postId}`).then((res) => res.data);

/** 是否已收藏 */
export const collectStatus = (postId: number) =>
  http.get<R<{ collected: boolean }>>(`/admin/collect/${postId}/status`).then((res) => res.data.data);
