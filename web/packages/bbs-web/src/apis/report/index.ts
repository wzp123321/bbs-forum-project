import http from '@/service';
import type { ReportSaveParams, ReportVO } from './index.api';

export * from './index.api';

/** 提交举报 */
export const createReport = (params: ReportSaveParams) =>
  http.post<number>('/admin/report', params);

/** 查询举报详情 (一般管理后台用,前端保留可用) */
export const getReport = (id: number) => http.get<ReportVO>(`/admin/report/${id}`);

export const reportApi = {
  create: createReport,
  detail: getReport,
};

export default reportApi;
