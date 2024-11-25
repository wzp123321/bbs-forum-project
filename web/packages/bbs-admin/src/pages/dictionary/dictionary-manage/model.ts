// 查询表单
export interface SearchForm {
    name: string
    type:string
}
// 字典表单
export interface RowFrom{
    name: string;
    status: boolean;
    type: string,
    describe: string,
  }
// 表格
export interface TableData {
    id: string,
    name: string;
    status: boolean;
    type: string,
    describe: string,
}