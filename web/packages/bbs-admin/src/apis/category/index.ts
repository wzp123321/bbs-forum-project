import http from '@/utils/request';
import type { CategoryVO, CategorySaveParams } from './index.api';
import type { PageResult, IResponseVO } from '../types';

export * from './index.api';

/** 分页 */
export const pageCategoriesApi = (params: { pageNum?: number; pageSize?: number; keyword?: string }) =>
  http.post<IResponseVO<PageResult<CategoryVO>>>('/admin/category/page', params).then((res) => res.data.data);

/** 全部 (启用中,下拉用) */
export const listCategoriesApi = () =>
  http.post<IResponseVO<CategoryVO[]>>('/admin/category/list').then((res) => res.data.data);

/** 详情 */
export const getCategoryApi = (id: number) =>
  http.post<IResponseVO<CategoryVO>>('/admin/category/detail', { id }).then((res) => res.data.data);

/** 新增 */
export const createCategoryApi = (params: CategorySaveParams) =>
  http.post<IResponseVO<number>>('/admin/category/create', params).then((res) => res.data.data);

/** 编辑 */
export const updateCategoryApi = (id: number, params: CategorySaveParams) =>
  http.post<IResponseVO<void>>(`/admin/category/${id}/update`, params).then((res) => res.data);

/** 删除 */
export const deleteCategoryApi = (id: number) =>
  http.post<IResponseVO<void>>('/admin/category/delete', { id }).then((res) => res.data);
