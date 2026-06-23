import http from '@/utils/request';
import type { CategoryVO, CategorySaveParams } from './index.api';
import type { PageResult, R } from '../types';

/** 分页 */
export const pageCategories = (params: { pageNum?: number; pageSize?: number; keyword?: string }) =>
  http
    .get<R<PageResult<CategoryVO>>, R<PageResult<CategoryVO>>>('/admin/category/page', { params })
    .then((res) => res.data.data);

/** 全部 (启用中,下拉用) */
export const listCategories = () =>
  http.get<R<CategoryVO[]>, R<CategoryVO[]>>('/admin/category/list').then((res) => res.data.data);

/** 详情 */
export const getCategory = (id: number) =>
  http.get<R<CategoryVO>, R<CategoryVO>>(`/admin/category/${id}`).then((res) => res.data.data);

/** 新增 */
export const createCategory = (params: CategorySaveParams) =>
  http.post<R<number>, R<number>>('/admin/category', params).then((res) => res.data.data);

/** 编辑 */
export const updateCategory = (id: number, params: CategorySaveParams) =>
  http.put<R<void>, R<void>>(`/admin/category/${id}`, params).then((res) => res.data);

/** 删除 */
export const deleteCategory = (id: number) =>
  http.delete<R<void>, R<void>>(`/admin/category/${id}`).then((res) => res.data);
