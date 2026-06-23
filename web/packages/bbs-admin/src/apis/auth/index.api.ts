/** 登录入参 */
export interface LoginParams {
  account: string;
  password: string;
}

/** 登录返回 */
export interface LoginResult {
  token: string;
  userId: string;
  userName: string;
  nickName: string;
  avatar: string;
}

/** 当前登录用户信息 */
export interface CurrentUser {
  userId: string;
  userName: string;
}
