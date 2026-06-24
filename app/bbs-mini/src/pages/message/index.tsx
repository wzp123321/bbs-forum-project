/**
 * 消息 - 快捷入口 + 互动消息 + 系统通知
 */
import React, { useEffect, useState } from 'react';
import { View, Text, Image } from '@tarojs/components';
import Taro from '@tarojs/taro';
import dayjs from 'dayjs';
import { getMessages } from '@/services/api';
import type { MessageItem } from '@/types/model';
import Empty from '@/components/Empty';
import styles from './index.module.scss';

const QUICK = [
  { id: 'like', label: '赞和收藏', icon: '❤', cls: styles.quickIconLike },
  { id: 'comment', label: '评论', icon: '💬', cls: styles.quickIconComment },
  { id: 'follow', label: '新增关注', icon: '➕', cls: styles.quickIconFollow },
];

const MessagePage: React.FC = () => {
  const [list, setList] = useState<MessageItem[]>([]);
  const unread = list.filter((m) => !m.read).length;

  useEffect(() => {
    (async () => {
      try {
        const data = await getMessages();
        setList(data);
      } catch (e) {
        console.error('[Message] 加载消息失败', e);
        Taro.showToast({ title: '加载失败', icon: 'none' });
      }
    })();
  }, []);

  const renderMessage = (m: MessageItem) => {
    if (m.type === 'system') {
      return (
        <View key={m.id} className={styles.messageItem}>
          <View className={`${styles.messageIcon} ${styles.quickIconSystem}`}>📢</View>
          <View className={styles.messageBody}>
            <View className={styles.messageLine1}>
              <Text className={styles.messageFrom}>系统通知</Text>
              <Text className={styles.messageTime}>{dayjs(m.createTime).format('MM-DD HH:mm')}</Text>
            </View>
            <Text className={styles.messageContent}>{m.content}</Text>
            {m.targetTitle && <Text className={styles.messageContent}>《{m.targetTitle}》</Text>}
          </View>
        </View>
      );
    }
    return (
      <View key={m.id} className={styles.messageItem}>
        {m.fromUser?.avatar ? (
          <Image className={styles.messageAvatar} src={m.fromUser.avatar} mode="aspectFill" />
        ) : (
          <View className={`${styles.messageIcon} ${styles.quickIconLike}`}>❤</View>
        )}
        <View className={styles.messageBody}>
          <View className={styles.messageLine1}>
            <Text className={styles.messageFrom}>{m.fromUser?.nickname || '匿名'}</Text>
            <Text className={styles.messageTime}>{dayjs(m.createTime).format('MM-DD HH:mm')}</Text>
          </View>
          <Text className={styles.messageContent}>{m.content}</Text>
          {m.targetTitle && <Text className={styles.messageContent}>《{m.targetTitle}》</Text>}
        </View>
        {!m.read && <View className={styles.unread} />}
      </View>
    );
  };

  return (
    <View className={styles.page}>
      <View className={styles.quickBar}>
        {QUICK.map((q) => (
          <View key={q.id} className={styles.quickItem} onClick={() => Taro.showToast({ title: '功能开发中', icon: 'none' })}>
            <View className={`${styles.quickIcon} ${q.cls}`}>
              {q.icon}
              {q.id === 'like' && unread > 0 && <View className={styles.badge}>{unread}</View>}
            </View>
            <Text className={styles.quickLabel}>{q.label}</Text>
          </View>
        ))}
      </View>

      <View className={styles.section}>
        <View className={styles.sectionTitle}>
          <Text>互动消息</Text>
        </View>
        <View className={styles.messageList}>
          {list.length === 0 ? (
            <Empty text="还没有消息" />
          ) : (
            list.map(renderMessage)
          )}
        </View>
      </View>
    </View>
  );
};

export default MessagePage;
