import http from '@/service';
import type { PostVO } from '@/apis/post';
import type { PageResult } from '../types';

/** 点赞 (1帖子 2评论) */
export const likeApi = (targetType: 1 | 2, targetId: number) =>
  http.post<void>('/admin/like/like', { targetType, targetId });

/** 取消点赞 */
export const cancelLikeApi = (targetType: 1 | 2, targetId: number) =>
  http.post<void>('/admin/like/cancel', { targetType, targetId });

/** 是否已点赞 */
export const likeStatusApi = (targetType: 1 | 2, targetId: number) =>
  http.post<{ liked: boolean }>('/admin/like/status', { targetType, targetId });

/** 我点赞的帖子分页 */
export const pageMyLikedApi = (params: { pageNum?: number; pageSize?: number } = {}) =>
  http.post<PageResult<PostVO>>('/admin/like/page', params);
