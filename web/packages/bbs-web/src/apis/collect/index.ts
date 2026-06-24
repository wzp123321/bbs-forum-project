import http from '@/service';
import type { PostVO } from '@/apis/post';

interface PageResp<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 收藏 */
export const collect = (postId: number) =>
  http.post<void>(`/admin/collect/${postId}`);

/** 取消收藏 */
export const cancelCollect = (postId: number) =>
  http.delete<void>(`/admin/collect/${postId}`);

/** 是否已收藏 */
export const collectStatus = (postId: number) =>
  http.get<{ collected: boolean }>(`/admin/collect/${postId}/status`);

/** 我收藏的帖子分页 */
export const pageMyCollected = (params: { pageNum?: number; pageSize?: number } = {}) =>
  http.get<PageResp<PostVO>>('/admin/collect/page', { params });

/** 命名空间导出 */
export const collectApi = {
  collect,
  cancelCollect,
  collectStatus,
  pageMyCollected,
};

export default collectApi;
