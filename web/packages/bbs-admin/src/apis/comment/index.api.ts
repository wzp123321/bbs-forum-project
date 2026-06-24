/** 评论 VO */
export interface CommentVO {
  id: number;
  postId?: number;
  postTitle?: string;
  userId?: string;
  userName?: string;
  parentId?: number;
  replyToUserId?: string;
  replyToUserName?: string;
  content?: string;
  likeCount?: number;
  status?: number;
  createTime?: string;
  updateTime?: string;
}

/** 评论分页查询参数 */
export interface CommentPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  postId?: number;
  userId?: string;
  parentId?: number;
  status?: number;
}

/** 评论新增/编辑入参 */
export interface CommentSaveParams {
  postId: number;
  parentId?: number;
  replyToUserId?: string;
  content: string;
  status?: number;
}
