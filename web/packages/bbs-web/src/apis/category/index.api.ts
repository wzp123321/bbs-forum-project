/** 板块 */
export interface CategoryVO {
  id: number;
  name: string;
  description?: string;
  icon?: string;
  sort?: number;
  status?: number;
  postCount?: number;
  createTime?: string;
}
