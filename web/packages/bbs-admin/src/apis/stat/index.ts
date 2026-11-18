import http from '@/utils/request';
import type { StatOverviewVO } from './index.api';
import type { IResponseVO } from '../types';

export * from './index.api';

/** 仪表盘统计 (仅管理员) */
export const getStatOverviewApi = () =>
  http.post<IResponseVO<StatOverviewVO>>('/admin/stat/overview', {}).then((res) => res.data.data);
