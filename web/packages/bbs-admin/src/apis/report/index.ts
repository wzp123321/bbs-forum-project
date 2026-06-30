import http from '@/utils/request';
import type { ReportVO, ReportPageParams, ReportHandleParams } from './index.api';
import type { IResponseVO } from '../types';

export * from './index.api';

interface ReportPageData {
  list: ReportVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pageReportsApi = (params: ReportPageParams) =>
  http.post<IResponseVO<ReportPageData>>('/admin/report/page', params).then((res) => res.data.data);

/** 详情 */
export const getReportApi = (id: number) =>
  http.post<IResponseVO<ReportVO>>('/admin/report/detail', { id }).then((res) => res.data.data);

/** 处理举报 */
export const handleReportApi = (id: number, params: ReportHandleParams) =>
  http.post<IResponseVO<void>>(`/admin/report/${id}/handle`, params).then((res) => res.data);

/** 删除 */
export const deleteReportApi = (id: number) =>
  http.post<IResponseVO<void>>('/admin/report/delete', { id }).then((res) => res.data);
