import http from '@/service';
import { CommentVO, CommentPageParams, CommentSaveParams } from './index.api';
import { PageResult } from '../types';

export * from './index.api';

/** 帖子下的评论分页 */
export const pageCommentsApi = (params: CommentPageParams) =>
  http.post<PageResult<CommentVO>>('/admin/comment/page', params);

/** 提交评论 */
export const createCommentApi = (params: CommentSaveParams) => http.post<number>('/admin/comment/create', params);

/** 删除 */
export const deleteCommentApi = (id: number) => http.post<void>('/admin/comment/delete', { id });
