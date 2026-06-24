import http from '@/service';
import type { RegisterParams } from './index.api';

/** 注册 */
export const register = (params: RegisterParams) =>
  http.post<string>('/admin/user/register', params);
