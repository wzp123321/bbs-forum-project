import http from '@/service';
import type { RegisterParams, UserInfoVO, UserUpdateParams, PasswordUpdateParams, FollowCountVO } from './index.api';
import type { PageResult } from '../types';

export * from './index.api';

/** 注册 */
export const registerApi = (params: RegisterParams) => http.post<string>('/admin/user/register', params);

/** 获取用户公开信息 */
export const getUserApi = (userId: string) => http.post<UserInfoVO>('/admin/user/detail', { userId });

/** 修改自己的资料 */
export const updateProfileApi = (userId: string, params: UserUpdateParams) =>
  http.post<void>(`/admin/user/${userId}/update`, params);

/** 修改自己的密码 */
export const updatePasswordApi = (userId: string, params: PasswordUpdateParams) =>
  http.post<void>(`/admin/user/${userId}/password`, params);

/** 关注/粉丝 计数 */
export const followCountApi = (userId: string) => http.post<FollowCountVO>('/admin/follow/count', { userId });

/** 我的帖子分页 */
export const myPostsApi = (params: { pageNum?: number; pageSize?: number }) =>
  http.post<PageResult<import('@/apis/post').PostVO>>('/admin/post/page', { ...params, status: 1 });

/** 我的收藏分页 */
export const myCollectedApi = (params: { pageNum?: number; pageSize?: number }) =>
  http.post<PageResult<import('@/apis/post').PostVO>>('/admin/collect/page', params);

/** 我点赞的帖子分页 */
export const myLikedApi = (params: { pageNum?: number; pageSize?: number }) =>
  http.post<PageResult<import('@/apis/post').PostVO>>('/admin/like/page', params);
