/** 评论 VO */
export interface CommentVO {
  id: number;
  postId?: number;
  postTitle?: string;
  userId?: string;
  userName?: string;
  /** 评论者昵称 (为空时后端已回退为 userName) */
  nickname?: string;
  /** 评论者头像 URL */
  avatar?: string;
  parentId?: number;
  replyToUserId?: string;
  replyToUserName?: string;
  replyToNickname?: string;
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
