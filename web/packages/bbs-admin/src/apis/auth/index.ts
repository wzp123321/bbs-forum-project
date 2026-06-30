import http from '@/utils/request';
import type { LoginParams, LoginResult, CurrentUser } from './index.api';
import { IResponseVO } from '../types';

export * from './index.api';

/** 登录 */
export const loginApi = (params: LoginParams) =>
  http.post<IResponseVO<LoginResult>>('/admin/auth/login', params).then((res) => res.data.data);

/** 获取当前用户 */
export const getCurrentUserApi = () =>
  http.post<IResponseVO<CurrentUser>>('/admin/auth/info').then((res) => res.data.data);

/** 退出登录 */
export const logoutApi = () => http.post<IResponseVO<void>>('/admin/auth/logout').then((res) => res.data);
