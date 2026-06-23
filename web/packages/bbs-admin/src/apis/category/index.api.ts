/** 板块 VO */
export interface CategoryVO {
  id: number;
  name: string;
  description?: string;
  sort?: number;
  status?: number;
  createTime?: string;
  updateTime?: string;
}

/** 新增/编辑入参 */
export interface CategorySaveParams {
  name: string;
  description?: string;
  sort?: number;
  status?: number;
}
