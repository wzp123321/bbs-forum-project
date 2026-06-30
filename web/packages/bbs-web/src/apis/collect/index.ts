import http from '@/service';
import type { PostVO } from '@/apis/post';
import type { PageResult } from '../types';

/** 收藏 */
export const collectApi = (postId: number) => http.post<void>('/admin/collect/collect', { postId });

/** 取消收藏 */
export const cancelCollectApi = (postId: number) => http.post<void>('/admin/collect/cancel', { postId });

/** 是否已收藏 */
export const collectStatusApi = (postId: number) =>
  http.post<{ collected: boolean }>('/admin/collect/status', { postId });

/** 我收藏的帖子分页 */
export const pageMyCollectedApi = (params: { pageNum?: number; pageSize?: number } = {}) =>
  http.post<PageResult<PostVO>>('/admin/collect/page', params);
