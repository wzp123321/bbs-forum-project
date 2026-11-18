import http from '@/utils/request';
import type {
  DictionaryVO,
  DictionarySaveParams,
  DictionaryPageParams,
  DictionaryTypeVO,
  DictionaryTypeSaveParams,
} from './index.api';
import type { PageResult, IResponseVO } from '../types';

export * from './index.api';

/* ---------------- 字典类型 ---------------- */

/** 类型分页 */
export const pageDictionaryTypesApi = (params: { pageNum?: number; pageSize?: number; keyword?: string }) =>
  http.post<IResponseVO<PageResult<DictionaryTypeVO>>>('/admin/dictionaryType/page', params).then((res) => res.data.data);

/** 启用中的类型 (下拉用) */
export const listDictionaryTypesApi = () =>
  http.post<IResponseVO<DictionaryTypeVO[]>>('/admin/dictionaryType/list', {}).then((res) => res.data.data);

/** 新增类型 */
export const createDictionaryTypeApi = (params: DictionaryTypeSaveParams) =>
  http.post<IResponseVO<number>>('/admin/dictionaryType/create', params).then((res) => res.data.data);

/** 编辑类型 */
export const updateDictionaryTypeApi = (id: number, params: DictionaryTypeSaveParams) =>
  http.post<IResponseVO<void>>(`/admin/dictionaryType/${id}/update`, params).then((res) => res.data);

/** 删除类型 */
export const deleteDictionaryTypeApi = (id: number) =>
  http.post<IResponseVO<void>>('/admin/dictionaryType/delete', { id }).then((res) => res.data);

/* ---------------- 字典项 ---------------- */

/** 字典项分页 */
export const pageDictionariesApi = (params: DictionaryPageParams) =>
  http.post<IResponseVO<PageResult<DictionaryVO>>>('/admin/dictionary/page', params).then((res) => res.data.data);

/** 新增字典项 */
export const createDictionaryApi = (params: DictionarySaveParams) =>
  http.post<IResponseVO<number>>('/admin/dictionary/create', params).then((res) => res.data.data);

/** 编辑字典项 */
export const updateDictionaryApi = (id: number, params: DictionarySaveParams) =>
  http.post<IResponseVO<void>>(`/admin/dictionary/${id}/update`, params).then((res) => res.data);

/** 删除字典项 */
export const deleteDictionaryApi = (id: number) =>
  http.post<IResponseVO<void>>('/admin/dictionary/delete', { id }).then((res) => res.data);

/** 启用/停用字典项 */
export const changeDictionaryStatusApi = (id: number, status: number) =>
  http.post<IResponseVO<void>>(`/admin/dictionary/${id}/changeStatus`, { status }).then((res) => res.data);
