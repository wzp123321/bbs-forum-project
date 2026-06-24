// 通用类型定义 (与 web 端 bbs-web 对齐, 便于联调)

export interface UserInfo {
  id: string;
  username: string;
  nickname: string;
  avatar: string;
  bio?: string;
  fanCount?: number;
  followCount?: number;
  postCount?: number;
  likeCount?: number;
}

export interface Tag {
  id: string;
  name: string;
}

export interface Category {
  id: string;
  name: string;
  icon?: string;
  postCount?: number;
}

export interface PostItem {
  id: string;
  title: string;
  content: string;
  author: UserInfo;
  category?: Category;
  tags?: Tag[];
  likeCount: number;
  commentCount: number;
  viewCount: number;
  collectCount: number;
  liked?: boolean;
  collected?: boolean;
  coverImage?: string;
  createTime: string;
  top?: boolean;
  essence?: boolean;
}

export interface CommentItem {
  id: string;
  postId: string;
  author: UserInfo;
  content: string;
  likeCount: number;
  liked?: boolean;
  replyTo?: string; // 被回复评论 id
  replyToUser?: string; // 被回复人昵称
  createTime: string;
}

export interface MessageItem {
  id: string;
  type: 'like' | 'comment' | 'follow' | 'system';
  fromUser?: UserInfo;
  targetTitle?: string;
  content: string;
  createTime: string;
  read: boolean;
}
