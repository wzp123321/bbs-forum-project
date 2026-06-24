import http from '@/utils/request';
import type { ReportVO, ReportPageParams, ReportHandleParams } from './index.api';
import type { R } from '../types';

interface ReportPageData {
  list: ReportVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pageReports = (params: ReportPageParams) =>
  http.get<R<ReportPageData>>('/admin/report/page', { params }).then((res) => res.data.data);

/** 详情 */
export const getReport = (id: number) =>
  http.get<R<ReportVO>>(`/admin/report/${id}`).then((res) => res.data.data);

/** 处理举报 */
export const handleReport = (id: number, params: ReportHandleParams) =>
  http.put<R<void>>(`/admin/report/${id}/handle`, params).then((res) => res.data);

/** 删除 */
export const deleteReport = (id: number) =>
  http.delete<R<void>>(`/admin/report/${id}`).then((res) => res.data);
