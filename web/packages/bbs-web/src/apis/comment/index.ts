import http from '@/service';
import type { CommentVO, CommentPageParams, CommentSaveParams, PageData } from './index.api';

/** 帖子下的评论分页 */
export const pageComments = (params: CommentPageParams) =>
  http.get<PageData<CommentVO>>('/admin/comment/page', { params });

/** 提交评论 */
export const createComment = (params: CommentSaveParams) =>
  http.post<number>('/admin/comment', params);

/** 删除 */
export const deleteComment = (id: number) => http.delete<void>(`/admin/comment/${id}`);
