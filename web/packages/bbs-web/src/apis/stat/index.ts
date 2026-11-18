import http from '@/service';
import type { StatSummaryVO } from './index.api';

export * from './index.api';

/** 站点公开统计 (首页统计条) */
export const getStatSummaryApi = () => http.post<StatSummaryVO>('/admin/stat/summary', {});
