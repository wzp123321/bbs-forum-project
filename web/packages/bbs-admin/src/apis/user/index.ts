import http from '@/utils/request';
import type { UserVO, RegisterParams, UserPageParams, UserUpdateParams, PasswordUpdateParams } from './index.api';
import { PageResult, IResponseVO } from '../types';

export * from './index.api';

/** 分页 */
export const pageUsersApi = (params: UserPageParams) =>
  http.post<IResponseVO<PageResult<UserVO>>>('/admin/user/page', params).then((res) => res.data.data);

/** 详情 */
export const getUserApi = (userId: string) =>
  http.post<IResponseVO<UserVO>>('/admin/user/detail', { userId }).then((res) => res.data.data);

/** 注册 */
export const registerApi = (params: RegisterParams) =>
  http.post<IResponseVO<string>>('/admin/user/register', params).then((res) => res.data.data);

/** 修改资料 */
export const updateProfileApi = (userId: string, params: UserUpdateParams) =>
  http.post<IResponseVO<void>>(`/admin/user/${userId}/update`, params).then((res) => res.data);

/** 修改密码 */
export const updatePasswordApi = (userId: string, params: PasswordUpdateParams) =>
  http.post<IResponseVO<void>>(`/admin/user/${userId}/password`, params).then((res) => res.data);

/** 删除 */
export const deleteUserApi = (userId: string) =>
  http.post<IResponseVO<void>>('/admin/user/delete', { userId }).then((res) => res.data);
