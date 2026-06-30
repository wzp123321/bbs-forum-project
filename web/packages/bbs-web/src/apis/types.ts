/**
 * 统一响应类型 (对齐后端 R<T>)
 *
 * 与后端全局 R<T> 包装类一一对应，所有 http 请求的响应体都遵循该结构。
 * - 业务码约定：200 = 成功；非 200 = 失败
 * - 失败时 message 面向用户可直接提示，data 字段无业务含义（通常为 null）
 * - 成功时 data 为泛型 T 的实际负载
 */
export interface IResponseVO<T = unknown> {
  /** 业务状态码：200 表示成功，其它值表示失败 */
  code: number;
  /** 业务提示信息：成功时为 'ok'，失败时为可面向用户展示的错误描述 */
  message: string;
  /** 业务负载：仅在 code === 200 时有实际意义，类型由泛型 T 决定 */
  data: T;
  /** 时间戳 (可选) */
  timestamp?: number;
}

/** 分页结果 */
export interface PageResult<T> {
  list: T[];
  total: number;
  pageSize: number;
  pageNum: number;
}

/** 兼容旧 R 类型 */
export type R<T = unknown> = IResponseVO<T>;
