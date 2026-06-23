import http from '@/utils/request';
import type { LoginParams, LoginResult, CurrentUser } from './index.api';
import type { R } from '../types';

/** 登录 */
export const login = (params: LoginParams) =>
  http.post<R<LoginResult>, R<LoginResult>>('/admin/auth/login', params).then((res) => res.data.data);

/** 获取当前用户 */
export const getCurrentUser = () =>
  http.get<R<CurrentUser>, R<CurrentUser>>('/admin/auth/info').then((res) => res.data.data);

/** 退出登录 */
export const logout = () => http.post<R<void>, R<void>>('/admin/auth/logout').then((res) => res.data);
