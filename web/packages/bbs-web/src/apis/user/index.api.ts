/** 注册入参 */
export interface RegisterParams {
  userName: string;
  password: string;
  email?: string;
  phone?: string;
  gender?: string;
}

/** 用户详情 (公开) */
export interface UserInfoVO {
  userId: string;
  userName: string;
  /** 展示名: 昵称, 为空时后端已回退为用户名 */
  nickname?: string;
  /** 头像 URL */
  avatar?: string;
  /** 个性签名 */
  signature?: string;
  gender?: string;
  email?: string;
  phone?: string;
  createTime?: string;
}

/** 资料更新入参 */
export interface UserUpdateParams {
  nickname?: string;
  avatar?: string;
  signature?: string;
  gender?: string;
  email?: string;
  phone?: string;
}

/** 修改密码入参 */
export interface PasswordUpdateParams {
  oldPassword: string;
  newPassword: string;
}

/** 关注/粉丝计数 */
export interface FollowCountVO {
  followers: number;
  following: number;
}
