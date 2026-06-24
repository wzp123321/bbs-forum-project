import http from '@/utils/request';
import type { CommentVO, CommentPageParams, CommentSaveParams } from './index.api';
import type { R } from '../types';

interface CommentPageData {
  list: CommentVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pageComments = (params: CommentPageParams) =>
  http.get<R<CommentPageData>>('/admin/comment/page', { params }).then((res) => res.data.data);

/** 详情 */
export const getComment = (id: number) => http.get<R<CommentVO>>(`/admin/comment/${id}`).then((res) => res.data.data);

/** 新增 */
export const createComment = (params: CommentSaveParams) =>
  http.post<R<number>>('/admin/comment', params).then((res) => res.data.data);

/** 编辑 */
export const updateComment = (id: number, params: CommentSaveParams) =>
  http.put<R<void>>(`/admin/comment/${id}`, params).then((res) => res.data);

/** 删除 */
export const deleteComment = (id: number) => http.delete<R<void>>(`/admin/comment/${id}`).then((res) => res.data);
