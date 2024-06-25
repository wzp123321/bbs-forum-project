/*************************************类型*****************************************/
/**
 * 响应结果
 */
export interface CommonHttpRes<T> {
  code: number;
  data: T;
  message: string;
  success: boolean;
}
/**
 * 分页响应结果
 */
export interface CommonHttpListRes<T> {
  list: T;
  pageNum: number;
  pageSize: number;
  pages: number;
  total: number;
}
/**
 * 通用value、label
 */
export interface CommonValueLabel<T = string> {
  value: T;
  label: string;
}
/**
 * code、name
 */
export interface CommonCodeName<T = string> {
  code: T;
  name: string;
}
/**
 * id、name
 */
export interface CommonIdName<T = string> {
  id: T;
  name: string;
}
/**
 * 通用对象
 */
export interface Common_IObject {
  [key: string]: any;
}
/******************************************常量****************************************************/
// 默认表格表头高度
export const COMMON_TABLE_HEADER_HEIGHT = 52;
// 默认表格单元格高度
export const COMMON_TABLE_CELL_HEIGHT = 48;
// 条数
export const COMMON_PAGE_SIZES = [10, 20, 30, 40, 50];
