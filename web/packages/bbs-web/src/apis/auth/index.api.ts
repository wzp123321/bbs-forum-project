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
