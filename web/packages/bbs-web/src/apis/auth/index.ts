import http from '@/service';
import type { LoginParams, LoginVO, UserInfo } from './index.api';

export * from './index.api';

/** 登录 */
export const loginApi = (params: LoginParams) => http.post<LoginVO>('/admin/auth/login', params);

/** 退出登录 */
export const logoutApi = () => http.post<void>('/admin/auth/logout');

/** 当前登录用户信息 */
export const getInfoApi = () => http.post<UserInfo>('/admin/auth/info');
