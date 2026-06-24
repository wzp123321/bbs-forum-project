import http from '@/utils/request';
import type { TagVO, TagSaveParams, TagPageParams } from './index.api';
import type { PageResult, R } from '../types';

/** 分页查询 */
export const pageTags = (params: TagPageParams) =>
  http.get<R<PageResult<TagVO>>>('/admin/tag/page', { params }).then((res) => res.data.data);

/** 全部 (启用中,下拉用) */
export const listTags = () => http.get<R<TagVO[]>>('/admin/tag/list').then((res) => res.data.data);

/** 详情 */
export const getTag = (id: number) => http.get<R<TagVO>>(`/admin/tag/${id}`).then((res) => res.data.data);

/** 新增 */
export const createTag = (params: TagSaveParams) =>
  http.post<R<number>>('/admin/tag', params).then((res) => res.data.data);

/** 编辑 */
export const updateTag = (id: number, params: TagSaveParams) =>
  http.put<R<void>>(`/admin/tag/${id}`, params).then((res) => res.data);

/** 删除 */
export const deleteTag = (id: number) => http.delete<R<void>>(`/admin/tag/${id}`).then((res) => res.data);
