/**
 * 用户信息 (对齐后端 UserVO)
 */
export interface UserInfo {
  userId: string;
  userName: string;
  gender?: string;
  email?: string;
  phone?: string;
  createTime?: string;
}

/**
 * 搜索表单
 */
export interface UserForm {
  keyword: string;
}
