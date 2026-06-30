import { Component } from 'vue';

/**
 * 统一响应类型 (对齐后端 R<T>)
 *
 * 与后端全局 R<T> 包装类一一对应，所有 http 请求的响应体都遵循该结构。
 * - 业务码约定：0 = 成功；非 0 = 失败（具体错误码参见 server/bbs-common 的 ResultCode）
 * - 失败时 message 面向用户可直接提示，data 字段无业务含义（通常为 null）
 * - 成功时 data 为泛型 T 的实际负载
 *
 * @example
 * ```ts
 * // 后端返回: { code: 0, message: 'ok', data: { id: 1, name: 'foo' } }
 * http.get<IResponseVO<{ id: number; name: string }>>('/xxx').then(res => res.data.data)
 * ```
 */
export interface IResponseVO<T = unknown> {
  /** 业务状态码：0 表示成功，其它值参见 ResultCode */
  code: number;
  /** 业务提示信息：成功时为 'ok'，失败时为可面向用户展示的错误描述 */
  message: string;
  /** 业务负载：仅在 code === 0 时有实际意义，类型由泛型 T 决定 */
  data: T;
}

/** 分页结果 */
export interface PageResult<T> {
  list: T[];
  total: number;
  pageSize: number;
  pageNum: number;
}
/**
 * 菜单
 */
export interface CommonMenu {
  path: string;
  name: string;
  icon?: string;
  index: string;
  meta: { title: string };
  children?: CommonMenu[];
  component?: Component;
}
