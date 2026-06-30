import http from '@/utils/request';
import { IResponseVO } from '../types';

/** 点赞 (1帖子 2评论) */
export const likeApi = (targetType: 1 | 2, targetId: number) =>
  http.post<IResponseVO<void>>('/admin/like/like', { targetType, targetId }).then((res) => res.data);

/** 取消点赞 */
export const cancelLikeApi = (targetType: 1 | 2, targetId: number) =>
  http.post<IResponseVO<void>>('/admin/like/cancel', { targetType, targetId }).then((res) => res.data);

/** 是否已点赞 */
export const likeStatusApi = (targetType: 1 | 2, targetId: number) =>
  http.post<IResponseVO<{ liked: boolean }>>('/admin/like/status', { targetType, targetId }).then((res) => res.data.data);
