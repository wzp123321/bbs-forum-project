/** 查询表单 */
export interface SearchForm {
  keyword: string;
}

/** 类型新增/编辑表单 */
export interface TypeForm {
  id?: number;
  name: string;
  code: string;
  description: string;
  status: number;
}
