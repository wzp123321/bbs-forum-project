/** 统一响应 */
export interface R<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp?: number;
}
