import http from '@/utils/request';
import type { TagVO, TagSaveParams, TagPageParams } from './index.api';
import type { PageResult, IResponseVO } from '../types';

export * from './index.api';

/** 分页查询 */
export const pageTagsApi = (params: TagPageParams) =>
  http.post<IResponseVO<PageResult<TagVO>>>('/admin/tag/page', params).then((res) => res.data.data);

/** 全部 (启用中,下拉用) */
export const listTagsApi = () => http.post<IResponseVO<TagVO[]>>('/admin/tag/list').then((res) => res.data.data);

/** 详情 */
export const getTagApi = (id: number) => http.post<IResponseVO<TagVO>>('/admin/tag/detail', { id }).then((res) => res.data.data);

/** 新增 */
export const createTagApi = (params: TagSaveParams) =>
  http.post<IResponseVO<number>>('/admin/tag/create', params).then((res) => res.data.data);

/** 编辑 */
export const updateTagApi = (id: number, params: TagSaveParams) =>
  http.post<IResponseVO<void>>(`/admin/tag/${id}/update`, params).then((res) => res.data);

/** 删除 */
export const deleteTagApi = (id: number) => http.post<IResponseVO<void>>('/admin/tag/delete', { id }).then((res) => res.data);
