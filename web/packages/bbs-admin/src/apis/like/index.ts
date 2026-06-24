import http from '@/utils/request';
import type { R } from '../types';

/** 点赞 (1帖子 2评论) */
export const like = (targetType: 1 | 2, targetId: number) =>
  http.post<R<void>>(`/admin/like/${targetType}/${targetId}`).then((res) => res.data);

/** 取消点赞 */
export const cancelLike = (targetType: 1 | 2, targetId: number) =>
  http.delete<R<void>>(`/admin/like/${targetType}/${targetId}`).then((res) => res.data);

/** 是否已点赞 */
export const likeStatus = (targetType: 1 | 2, targetId: number) =>
  http.get<R<{ liked: boolean }>>(`/admin/like/${targetType}/${targetId}/status`).then((res) => res.data.data);
