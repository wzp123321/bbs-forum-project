import http from '@/utils/request';
import type { R } from '../types';

/** 关注 */
export const follow = (followUserId: string) =>
  http.post<R<void>, R<void>>(`/admin/follow/${followUserId}`).then((res) => res.data);

/** 取消关注 */
export const cancelFollow = (followUserId: string) =>
  http.delete<R<void>, R<void>>(`/admin/follow/${followUserId}`).then((res) => res.data);

/** 是否已关注 */
export const followStatus = (followUserId: string) =>
  http.get<R<{ following: boolean }>, R<{ following: boolean }>>(`/admin/follow/${followUserId}/status`).then((res) => res.data.data);

/** 粉丝/关注数 */
export const countFollow = (userId: string) =>
  http.get<R<{ followers: number; following: number }>, R<{ followers: number; following: number }>>('/admin/follow/count', { params: { userId } }).then((res) => res.data.data);
