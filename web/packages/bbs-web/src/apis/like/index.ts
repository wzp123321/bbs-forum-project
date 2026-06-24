import http from '@/service';
import type { PostVO } from '@/apis/post';

interface PageResp<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 点赞 (1帖子 2评论) */
export const like = (targetType: 1 | 2, targetId: number) =>
  http.post<void>(`/admin/like/${targetType}/${targetId}`);

/** 取消点赞 */
export const cancelLike = (targetType: 1 | 2, targetId: number) =>
  http.delete<void>(`/admin/like/${targetType}/${targetId}`);

/** 是否已点赞 */
export const likeStatus = (targetType: 1 | 2, targetId: number) =>
  http.get<{ liked: boolean }>(`/admin/like/${targetType}/${targetId}/status`);

/** 我点赞的帖子分页 */
export const pageMyLiked = (params: { pageNum?: number; pageSize?: number } = {}) =>
  http.get<PageResp<PostVO>>('/admin/like/page', { params });

/** 命名空间导出 */
export const likeApi = {
  like,
  cancelLike,
  likeStatus,
  pageMyLiked,
};

export default likeApi;
