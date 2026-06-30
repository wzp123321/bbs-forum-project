import http from '@/utils/request';
import type { IResponseVO } from '../types';

/** 关注 */
export const followApi = (followUserId: string) =>
  http.post<IResponseVO<void>>('/admin/follow/follow', { followUserId }).then((res) => res.data);

/** 取消关注 */
export const cancelFollowApi = (followUserId: string) =>
  http.post<IResponseVO<void>>('/admin/follow/cancel', { followUserId }).then((res) => res.data);

/** 是否已关注 */
export const followStatusApi = (followUserId: string) =>
  http.post<IResponseVO<{ following: boolean }>>('/admin/follow/status', { followUserId }).then((res) => res.data.data);

/** 粉丝/关注数 */
export const countFollowApi = (userId: string) =>
  http.post<IResponseVO<{ followers: number; following: number }>>('/admin/follow/count', { userId }).then((res) => res.data.data);
