import http from '@/service';
import type {
  RegisterParams,
  UserInfoVO,
  UserUpdateParams,
  PasswordUpdateParams,
  FollowCountVO,
} from './index.api';

export * from './index.api';

/** 注册 */
export const register = (params: RegisterParams) =>
  http.post<string>('/admin/user/register', params);

/** 获取用户公开信息 */
export const getUser = (userId: string) =>
  http.get<UserInfoVO>(`/admin/user/${userId}`);

/** 修改自己的资料 */
export const updateProfile = (userId: string, params: UserUpdateParams) =>
  http.put<void>(`/admin/user/${userId}`, params);

/** 修改自己的密码 */
export const updatePassword = (userId: string, params: PasswordUpdateParams) =>
  http.put<void>(`/admin/user/${userId}/password`, params);

/** 关注/粉丝 计数 */
export const followCount = (userId: string) =>
  http.get<FollowCountVO>('/admin/follow/count', { params: { userId } });

/** 我的帖子 (userId 来自 token) */
export const myPosts = (params: { pageNum?: number; pageSize?: number }) =>
  http.get<{ list: import('@/apis/post').PostVO[]; total: number; pageNum: number; pageSize: number }>(
    '/admin/post/page',
    { params: { ...params, status: 1 } },
  );

/** 我的收藏 */
export const myCollected = (params: { pageNum?: number; pageSize?: number }) =>
  http.get<{ list: import('@/apis/post').PostVO[]; total: number; pageNum: number; pageSize: number }>(
    '/admin/collect/page',
    { params },
  );

/** 我点赞的帖子 */
export const myLiked = (params: { pageNum?: number; pageSize?: number }) =>
  http.get<{ list: import('@/apis/post').PostVO[]; total: number; pageNum: number; pageSize: number }>(
    '/admin/like/page',
    { params },
  );

/** 命名空间导出 (兼容老 import { userApi } from '@/apis/user') */
export const userApi = {
  register,
  getUser,
  updateProfile,
  updatePassword,
  followCount,
  myPosts,
  myCollected,
  myLiked,
};

export default userApi;
