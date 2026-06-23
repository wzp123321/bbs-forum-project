// 板块搜索表单
export interface SearchForm {
  keyword: string;
}

// 板块表单
export interface RowFrom {
  id?: number;
  name: string;
  description: string;
  sort: number;
  status: number;
}
