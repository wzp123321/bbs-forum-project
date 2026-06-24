import http from '@/service';

/** 收藏 */
export const collect = (postId: number) =>
  http.post<void>(`/admin/collect/${postId}`);

/** 取消收藏 */
export const cancelCollect = (postId: number) =>
  http.delete<void>(`/admin/collect/${postId}`);

/** 是否已收藏 */
export const collectStatus = (postId: number) =>
  http.get<{ collected: boolean }>(`/admin/collect/${postId}/status`);
