import http from '@/service';
import type { LoginParams, LoginVO, UserInfo } from './index.api';

export * from './index.api';

/** 登录 */
export const login = (params: LoginParams) =>
  http.post<LoginVO>('/admin/auth/login', params);

/** 退出登录 */
export const logout = () => http.post<void>('/admin/auth/logout');

/** 当前登录用户信息 */
export const getInfo = () => http.get<UserInfo>('/admin/auth/info');

/** 命名空间导出 */
export const authApi = {
  login,
  logout,
  getInfo,
};

export default authApi;
