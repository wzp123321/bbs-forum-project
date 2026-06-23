/** 用户 VO (对齐后端) */
export interface UserVO {
  userId: string;
  userName: string;
  gender?: string;
  email?: string;
  phone?: string;
  createTime?: string;
}

/** 注册入参 */
export interface RegisterParams {
  userName: string;
  password: string;
  email?: string;
  phone?: string;
  gender?: string;
}

/** 分页查询参数 */
export interface UserPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
}

/** 修改资料入参 */
export interface UserUpdateParams {
  email?: string;
  phone?: string;
  gender?: string;
}

/** 修改密码入参 */
export interface PasswordUpdateParams {
  oldPassword: string;
  newPassword: string;
}
