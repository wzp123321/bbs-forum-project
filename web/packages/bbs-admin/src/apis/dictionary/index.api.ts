/** 字典类型 VO */
export interface DictionaryTypeVO {
  id: number;
  name: string;
  code: string;
  description?: string;
  status: number;
  createTime?: string;
}

/** 字典类型新增/编辑入参 */
export interface DictionaryTypeSaveParams {
  name: string;
  code: string;
  description?: string;
  status?: number;
}

/** 字典项 VO */
export interface DictionaryVO {
  id: number;
  typeId: number;
  typeName?: string;
  label: string;
  value: string;
  sort: number;
  status: number;
  createTime?: string;
}

/** 字典项新增/编辑入参 */
export interface DictionarySaveParams {
  typeId: number;
  label: string;
  value: string;
  sort?: number;
  status?: number;
}

/** 字典项分页查询参数 */
export interface DictionaryPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  typeId?: number;
  status?: number;
}
