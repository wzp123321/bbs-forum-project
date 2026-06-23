/**
 * 统一响应类型 (对齐后端 R<T>)
 */
export interface R<T = unknown> {
  code: number;
  message: string;
  data: T;
}

/** 分页结果 */
export interface PageResult<T> {
  list: T[];
  total: number;
  pageSize: number;
  pageNum: number;
}
