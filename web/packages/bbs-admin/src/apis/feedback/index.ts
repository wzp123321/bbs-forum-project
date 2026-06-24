import http from '@/utils/request';
import type {
  FeedbackVO,
  FeedbackPageParams,
  FeedbackSaveParams,
  FeedbackReplyParams,
} from './index.api';
import type { R } from '../types';

/** 分页查询 */
export const pageFeedbacks = (params: FeedbackPageParams) =>
  http
    .get<R<{ list: FeedbackVO[]; total: number; pageNum: number; pageSize: number }>, R<{ list: FeedbackVO[]; total: number; pageNum: number; pageSize: number }>>('/admin/feedback/page', { params })
    .then((res) => res.data.data);

/** 详情 */
export const getFeedback = (id: number) =>
  http.get<R<FeedbackVO>, R<FeedbackVO>>(`/admin/feedback/${id}`).then((res) => res.data.data);

/** 提交反馈 */
export const createFeedback = (params: FeedbackSaveParams) =>
  http.post<R<number>, R<number>>('/admin/feedback', params).then((res) => res.data.data);

/** 回复反馈 */
export const replyFeedback = (id: number, params: FeedbackReplyParams) =>
  http.put<R<void>, R<void>>(`/admin/feedback/${id}/reply`, params).then((res) => res.data);

/** 删除 */
export const deleteFeedback = (id: number) =>
  http.delete<R<void>, R<void>>(`/admin/feedback/${id}`).then((res) => res.data);
