import http from '@/service';
import type { UserInfoVO } from '@/apis/user';

/** 关注 */
export const follow = (followUserId: string) =>
  http.post<void>(`/admin/follow/${followUserId}`);

/** 取消关注 */
export const cancelFollow = (followUserId: string) =>
  http.delete<void>(`/admin/follow/${followUserId}`);

/** 是否已关注 */
export const followStatus = (followUserId: string) =>
  http.get<{ following: boolean }>(`/admin/follow/${followUserId}/status`);

/** 我的粉丝列表 */
export const pageFollowers = (userId: string, pageNum = 1, pageSize = 20) =>
  http.get<{ list: UserInfoVO[]; total: number; pageNum: number; pageSize: number }>(
    '/admin/follow/followers',
    { params: { userId, pageNum, pageSize } },
  );

/** 我的关注列表 */
export const pageFollowing = (userId: string, pageNum = 1, pageSize = 20) =>
  http.get<{ list: UserInfoVO[]; total: number; pageNum: number; pageSize: number }>(
    '/admin/follow/following',
    { params: { userId, pageNum, pageSize } },
  );

/** 命名空间导出 */
export const followApi = {
  follow,
  cancelFollow,
  followStatus,
  pageFollowers,
  pageFollowing,
};

export default followApi;
