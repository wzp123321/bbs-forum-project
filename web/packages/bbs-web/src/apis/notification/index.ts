import http from '@/service';
import type { NotificationPageParams, NotificationVO } from './index.api';
import type { PageResult } from '../types';

export * from './index.api';

/** 未读通知数 */
export const unreadCountApi = () => http.post<{ count: number }>('/admin/notification/unreadCount', {});

/** 通知分页 */
export const pageNotificationsApi = (params: NotificationPageParams) =>
  http.post<PageResult<NotificationVO>>('/admin/notification/page', params);

/** 单条已读 */
export const readNotificationApi = (id: number) => http.post<void>('/admin/notification/read', { id });

/** 全部已读 */
export const readAllNotificationsApi = () =>
  http.post<{ updated: number }>('/admin/notification/readAll', {});
