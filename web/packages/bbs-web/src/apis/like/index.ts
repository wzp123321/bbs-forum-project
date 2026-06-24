import http from '@/service';

/** 点赞 (1帖子 2评论) */
export const like = (targetType: 1 | 2, targetId: number) =>
  http.post<void>(`/admin/like/${targetType}/${targetId}`);

/** 取消点赞 */
export const cancelLike = (targetType: 1 | 2, targetId: number) =>
  http.delete<void>(`/admin/like/${targetType}/${targetId}`);

/** 是否已点赞 */
export const likeStatus = (targetType: 1 | 2, targetId: number) =>
  http.get<{ liked: boolean }>(`/admin/like/${targetType}/${targetId}/status`);
