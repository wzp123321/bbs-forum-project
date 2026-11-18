import http from '@/service';
import type {
  LoginParams,
  LoginVO,
  UserInfo,
  ForgotPasswordParams,
  ForgotPasswordVO,
  ResetPasswordParams,
} from './index.api';

export * from './index.api';

/** 登录 */
export const loginApi = (params: LoginParams) => http.post<LoginVO>('/admin/auth/login', params);

/** 退出登录 */
export const logoutApi = () => http.post<void>('/admin/auth/logout');

/** 当前登录用户信息 */
export const getInfoApi = () => http.post<UserInfo>('/admin/auth/info');

/** 忘记密码: 校验用户名与绑定联系方式, 换取一次性重置令牌 */
export const forgotPasswordApi = (params: ForgotPasswordParams) =>
  http.post<ForgotPasswordVO>('/admin/auth/forgotPassword', params);

/** 重置密码 */
export const resetPasswordApi = (params: ResetPasswordParams) =>
  http.post<void>('/admin/auth/resetPassword', params);
