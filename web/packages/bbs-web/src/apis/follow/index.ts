import http from '@/service';
import type { UserInfoVO } from '@/apis/user';
import type { PageResult } from '../types';

/** 关注 */
export const followApi = (followUserId: string) => http.post<void>('/admin/follow/follow', { followUserId });

/** 取消关注 */
export const cancelFollowApi = (followUserId: string) => http.post<void>('/admin/follow/cancel', { followUserId });

/** 是否已关注 */
export const followStatusApi = (followUserId: string) =>
  http.post<{ following: boolean }>('/admin/follow/status', { followUserId });

/** 我的粉丝列表 */
export const pageFollowersApi = (userId: string, pageNum = 1, pageSize = 20) =>
  http.post<PageResult<UserInfoVO>>('/admin/follow/followers', { userId, pageNum, pageSize });

/** 我的关注列表 */
export const pageFollowingApi = (userId: string, pageNum = 1, pageSize = 20) =>
  http.post<PageResult<UserInfoVO>>('/admin/follow/following', { userId, pageNum, pageSize });
