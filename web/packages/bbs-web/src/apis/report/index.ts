import http from '@/service';
import type { ReportSaveParams, ReportVO } from './index.api';

export * from './index.api';

/** 提交举报 */
export const createReportApi = (params: ReportSaveParams) => http.post<number>('/admin/report/create', params);

/** 查询举报详情 */
export const getReportApi = (id: number) => http.post<ReportVO>('/admin/report/detail', { id });
