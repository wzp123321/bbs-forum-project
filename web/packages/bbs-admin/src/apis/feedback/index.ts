import http from '@/utils/request';
import type { FeedbackVO, FeedbackPageParams, FeedbackSaveParams, FeedbackReplyParams } from './index.api';
import { IResponseVO } from '../types';

export * from './index.api';

interface FeedbackPageData {
  list: FeedbackVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pageFeedbacksApi = (params: FeedbackPageParams) =>
  http.post<IResponseVO<FeedbackPageData>>('/admin/feedback/page', params).then((res) => res.data.data);

/** 详情 */
export const getFeedbackApi = (id: number) =>
  http.post<IResponseVO<FeedbackVO>>('/admin/feedback/detail', { id }).then((res) => res.data.data);

/** 提交反馈 */
export const createFeedbackApi = (params: FeedbackSaveParams) =>
  http.post<IResponseVO<number>>('/admin/feedback/create', params).then((res) => res.data.data);

/** 回复反馈 */
export const replyFeedbackApi = (id: number, params: FeedbackReplyParams) =>
  http.post<IResponseVO<void>>(`/admin/feedback/${id}/reply`, params).then((res) => res.data);

/** 删除 */
export const deleteFeedbackApi = (id: number) =>
  http.post<IResponseVO<void>>('/admin/feedback/delete', { id }).then((res) => res.data);
