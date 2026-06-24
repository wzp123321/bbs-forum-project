import http from '@/utils/request';
import type { UserVO, RegisterParams, UserPageParams, UserUpdateParams, PasswordUpdateParams } from './index.api';
import type { PageResult, R } from '../types';

/** 分页 */
export const pageUsers = (params: UserPageParams) =>
  http.get<R<PageResult<UserVO>>>('/admin/user/page', { params }).then((res) => res.data.data);

/** 详情 */
export const getUser = (userId: string) => http.get<R<UserVO>>(`/admin/user/${userId}`).then((res) => res.data.data);

/** 注册 */
export const register = (params: RegisterParams) =>
  http.post<R<string>>('/admin/user/register', params).then((res) => res.data.data);

/** 修改资料 */
export const updateProfile = (userId: string, params: UserUpdateParams) =>
  http.put<R<void>>(`/admin/user/${userId}`, params).then((res) => res.data);

/** 修改密码 */
export const updatePassword = (userId: string, params: PasswordUpdateParams) =>
  http.put<R<void>>(`/admin/user/${userId}/password`, params).then((res) => res.data);

/** 删除 */
export const deleteUser = (userId: string) => http.delete<R<void>>(`/admin/user/${userId}`).then((res) => res.data);
