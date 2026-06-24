import http from '@/utils/request';
import type { CommentVO, CommentPageParams, CommentSaveParams } from './index.api';
import type { R } from '../types';

/** 分页查询 */
export const pageComments = (params: CommentPageParams) =>
  http
    .get<R<{ list: CommentVO[]; total: number; pageNum: number; pageSize: number }>, R<{ list: CommentVO[]; total: number; pageNum: number; pageSize: number }>>('/admin/comment/page', { params })
    .then((res) => res.data.data);

/** 详情 */
export const getComment = (id: number) =>
  http.get<R<CommentVO>, R<CommentVO>>(`/admin/comment/${id}`).then((res) => res.data.data);

/** 新增 */
export const createComment = (params: CommentSaveParams) =>
  http.post<R<number>, R<number>>('/admin/comment', params).then((res) => res.data.data);

/** 编辑 */
export const updateComment = (id: number, params: CommentSaveParams) =>
  http.put<R<void>, R<void>>(`/admin/comment/${id}`, params).then((res) => res.data);

/** 删除 */
export const deleteComment = (id: number) =>
  http.delete<R<void>, R<void>>(`/admin/comment/${id}`).then((res) => res.data);

/** 修改状态 */
export const changeStatus = (id: number, params: { status?: number }) =>
  http.put<R<void>, R<void>>(`/admin/comment/${id}/status`, params).then((res) => res.data);
