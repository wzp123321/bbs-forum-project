/** 登录入参 */
export interface LoginParams {
  account: string;
  password: string;
}

/** 登录出参 */
export interface LoginVO {
  token: string;
  userId: string;
  userName: string;
  nickName?: string;
  avatar?: string;
}

/** 用户信息 */
export interface UserInfo {
  userId: string;
  userName: string;
  email?: string;
  phone?: string;
  gender?: string;
  avatar?: string;
  status?: number;
  createTime?: string;
}

/** 忘记密码入参: 用户名 + 账号绑定的邮箱或手机号 */
export interface ForgotPasswordParams {
  account: string;
  contact: string;
}

/** 忘记密码出参 */
export interface ForgotPasswordVO {
  resetToken: string;
  expireSeconds: number;
}

/** 重置密码入参 */
export interface ResetPasswordParams {
  resetToken: string;
  newPassword: string;
}
