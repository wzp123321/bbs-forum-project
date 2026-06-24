/**
 * API Service 层
 * 优先用 mock 数据, 通过开关切换真实接口
 * 真实接口时, axios baseURL 为 /api (web 同源) 或独立接口地址
 */
import Taro from '@tarojs/taro';
import { useUserStore } from '@/store/user';
import { mockPosts } from '@/data/posts';
import { mockCategories, mockTags } from '@/data/categories';
import { mockComments, mockMessages } from '@/data/messages';
import type { PostItem, CommentItem, MessageItem, Category, Tag, UserInfo } from '@/types/model';

const USE_MOCK = true;
const API_BASE = (process.env.NODE_ENV === 'production' ? '/api' : 'http://localhost:8888');

/** 通用请求封装 */
async function request<T>(url: string, options: Taro.request.Option = {}): Promise<T> {
  const token = useUserStore.getState().token;
  const res = await Taro.request({
    url: API_BASE + url,
    header: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...(options.header || {}) },
    ...options,
  });
  // 约定后端统一响应: { code, data, msg }
  const body = res.data as { code: number; data: T; msg?: string };
  if (body?.code !== 200) {
    throw new Error(body?.msg || `请求失败: ${res.statusCode}`);
  }
  return body.data;
}

/** 模拟网络延迟 */
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

// ============== 帖子 ==============
export interface PostListParams { page?: number; pageSize?: number; categoryId?: string; keyword?: string; }
export interface PageResult<T> { list: T[]; total: number; hasMore: boolean; }

export async function getPostList(params: PostListParams = {}): Promise<PageResult<PostItem>> {
  if (USE_MOCK) {
    await delay();
    const { page = 1, pageSize = 10, categoryId, keyword } = params;
    let list = [...mockPosts];
    if (categoryId) list = list.filter((p) => p.category?.id === categoryId);
    if (keyword) list = list.filter((p) => p.title.includes(keyword) || p.content.includes(keyword));
    const total = list.length;
    const start = (page - 1) * pageSize;
    const slice = list.slice(start, start + pageSize);
    return { list: slice, total, hasMore: start + pageSize < total };
  }
  return request(`/admin/post/page?page=${params.page ?? 1}&size=${params.pageSize ?? 10}`);
}

export async function getPostDetail(id: string): Promise<PostItem> {
  if (USE_MOCK) {
    await delay();
    const p = mockPosts.find((x) => x.id === id);
    if (!p) throw new Error('帖子不存在');
    return p;
  }
  return request(`/admin/post/${id}`);
}

export async function publishPost(payload: { title: string; content: string; categoryId?: string; tags?: string[] }): Promise<{ id: string }> {
  if (USE_MOCK) {
    await delay();
    console.info('[PostService] 发布成功(mock)', payload);
    return { id: 'p' + Date.now() };
  }
  return request('/admin/post', { method: 'POST', data: payload });
}

export async function likePost(id: string, liked: boolean): Promise<void> {
  if (USE_MOCK) {
    await delay(120);
    const p = mockPosts.find((x) => x.id === id);
    if (p) { p.liked = liked; p.likeCount += liked ? 1 : -1; }
    return;
  }
  return request(`/admin/post/like/${id}?liked=${liked}`, { method: 'POST' });
}

// ============== 评论 ==============
export async function getComments(postId: string): Promise<CommentItem[]> {
  if (USE_MOCK) {
    await delay();
    return mockComments.filter((c) => c.postId === postId);
  }
  return request(`/admin/comment/post/${postId}`);
}

export async function addComment(payload: { postId: string; content: string; replyTo?: string }): Promise<CommentItem> {
  if (USE_MOCK) {
    await delay();
    return {
      id: 'cm' + Date.now(),
      postId: payload.postId,
      author: { id: 'me', username: 'me', nickname: '我', avatar: 'https://picsum.photos/id/64/200/200' },
      content: payload.content,
      likeCount: 0,
      createTime: '刚刚',
      replyTo: payload.replyTo,
    };
  }
  return request('/admin/comment', { method: 'POST', data: payload });
}

// ============== 分类 / 话题 ==============
export async function getCategories(): Promise<Category[]> {
  if (USE_MOCK) {
    await delay(150);
    return mockCategories;
  }
  return request('/admin/category/list');
}

export async function getHotTags(): Promise<Tag[]> {
  if (USE_MOCK) {
    await delay(150);
    return mockTags.slice(0, 12);
  }
  return request('/admin/tag/hot');
}

// ============== 消息 ==============
export async function getMessages(): Promise<MessageItem[]> {
  if (USE_MOCK) {
    await delay();
    return mockMessages;
  }
  return request('/admin/message/list');
}

// ============== 登录 ==============
export async function login(payload: { username: string; password: string }): Promise<{ token: string; user: UserInfo }> {
  if (USE_MOCK) {
    await delay();
    return {
      token: 'mock-token-' + Date.now(),
      user: {
        id: 'me',
        username: payload.username || 'demo',
        nickname: payload.username || '体验用户',
        avatar: 'https://picsum.photos/id/64/200/200',
        bio: '这个人很懒,什么都没写~',
        fanCount: 12, followCount: 23, postCount: 5, likeCount: 88,
      },
    };
  }
  return request('/admin/auth/login', { method: 'POST', data: payload });
}

export async function getCurrentUser(): Promise<UserInfo> {
  if (USE_MOCK) {
    await delay(100);
    return useUserStore.getState().user as UserInfo;
  }
  return request('/admin/auth/info');
}
