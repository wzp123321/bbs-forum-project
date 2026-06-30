import http from '@/utils/request';
import type { CommentVO, CommentPageParams, CommentSaveParams } from './index.api';
import { IResponseVO } from '../types';

export * from './index.api';

interface CommentPageData {
  list: CommentVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pageCommentsApi = (params: CommentPageParams) =>
  http.post<IResponseVO<CommentPageData>>('/admin/comment/page', params).then((res) => res.data.data);

/** 详情 */
export const getCommentApi = (id: number) =>
  http.post<IResponseVO<CommentVO>>('/admin/comment/detail', { id }).then((res) => res.data.data);

/** 新增 */
export const createCommentApi = (params: CommentSaveParams) =>
  http.post<IResponseVO<number>>('/admin/comment/create', params).then((res) => res.data.data);

/** 编辑 */
export const updateCommentApi = (id: number, params: CommentSaveParams) =>
  http.post<IResponseVO<void>>(`/admin/comment/${id}/update`, params).then((res) => res.data);

/** 删除 */
export const deleteCommentApi = (id: number) =>
  http.post<IResponseVO<void>>('/admin/comment/delete', { id }).then((res) => res.data);

/** 切换状态 */
export const changeStatusApi = (id: number, params: { status: number }) =>
  http.post<IResponseVO<void>>(`/admin/comment/${id}/changeStatus`, params).then((res) => res.data);
