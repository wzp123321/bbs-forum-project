import http from '@/service';

/** 关注 */
export const follow = (followUserId: string) =>
  http.post<void>(`/admin/follow/${followUserId}`);

/** 取消关注 */
export const cancelFollow = (followUserId: string) =>
  http.delete<void>(`/admin/follow/${followUserId}`);

/** 是否已关注 */
export const followStatus = (followUserId: string) =>
  http.get<{ following: boolean }>(`/admin/follow/${followUserId}/status`);
