// 帖子搜索表单
export interface SearchForm {
  keyword: string;
  categoryId?: number;
  tagId?: number;
  status?: number;
}

// 帖子表单
export interface RowFrom {
  id?: number;
  title: string;
  content: string;
  contentType: number;
  categoryId?: number;
  tagIds: number[];
  status: number;
  isTop: number;
  isEssence: number;
}
