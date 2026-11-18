/** 查询表单 */
export interface SearchForm {
  keyword: string;
  typeId?: number;
}

/** 字典项新增/编辑表单 */
export interface DictForm {
  id?: number;
  typeId?: number;
  label: string;
  value: string;
  sort: number;
  status: number;
}
