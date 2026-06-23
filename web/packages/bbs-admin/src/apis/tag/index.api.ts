/** 标签 VO */
export interface TagVO {
  id: number;
  name: string;
  description?: string;
  postCount?: number;
  status?: number;
  createTime?: string;
  updateTime?: string;
}

/** 标签新增/编辑入参 */
export interface TagSaveParams {
  name: string;
  description?: string;
  status?: number;
}

/** 标签分页查询参数 */
export interface TagPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
}
