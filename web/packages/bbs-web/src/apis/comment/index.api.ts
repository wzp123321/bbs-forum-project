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
}

/** 评论分页参数 */
export interface CommentPageParams {
  pageNum?: number;
  pageSize?: number;
  postId?: number;
  parentId?: number;
  status?: number;
}

/** 评论提交入参 */
export interface CommentSaveParams {
  postId: number;
  parentId?: number;
  replyToUserId?: string;
  content: string;
}
