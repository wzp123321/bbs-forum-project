/**
 * 我的 - 个人信息 + 统计 + 菜单
 */
import React, { useEffect } from 'react';
import { View, Text, Image } from '@tarojs/components';
import Taro from '@tarojs/taro';
import { useUserStore } from '@/store/user';
import styles from './index.module.scss';

const MENU = [
  { id: 'post', label: '我的帖子', icon: '📝', color: '#165dff' },
  { id: 'collect', label: '我的收藏', icon: '⭐', color: '#ff7d00' },
  { id: 'like', label: '我的点赞', icon: '❤', color: '#f53f3f' },
  { id: 'follow', label: '我的关注', icon: '👥', color: '#00b42a' },
  { id: 'feedback', label: '意见反馈', icon: '💌', color: '#722ed1' },
  { id: 'about', label: '关于', icon: 'ℹ', color: '#86909c' },
];

const MinePage: React.FC = () => {
  const { user, loadFromStorage, logout } = useUserStore();

  useEffect(() => {
    loadFromStorage();
  }, [loadFromStorage]);

  const goLogin = () => Taro.navigateTo({ url: '/pages/login/index' });

  const handleMenu = (id: string) => {
    if (id === 'about') {
      Taro.showModal({ title: 'BBS 论坛', content: 'v1.0.0\n跨端论坛小程序', showCancel: false });
      return;
    }
    Taro.showToast({ title: '功能开发中', icon: 'none' });
  };

  const handleLogout = () => {
    Taro.showModal({
      title: '提示',
      content: '确定要退出登录吗?',
      success: (res) => {
        if (res.confirm) {
          logout();
          Taro.showToast({ title: '已退出', icon: 'success' });
        }
      },
    });
  };

  return (
    <View className={styles.page}>
      <View className={styles.header}>
        <View className={styles.userRow}>
          {user ? (
            <>
              <Image className={styles.avatar} src={user.avatar} mode="aspectFill" />
              <View className={styles.userInfo}>
                <Text className={styles.nickname}>{user.nickname}</Text>
                <Text className={styles.bio}>{user.bio || '这个人很懒,什么都没写~'}</Text>
              </View>
            </>
          ) : (
            <>
              <View className={styles.avatar} onClick={goLogin} />
              <View className={styles.userInfo} onClick={goLogin}>
                <Text className={styles.nickname}>未登录</Text>
                <Text className={styles.bio}>点击登录后体验完整功能</Text>
              </View>
            </>
          )}
        </View>

        {user && (
          <View className={styles.stats}>
            <View className={styles.statItem}>
              <Text className={styles.statValue}>{user.postCount ?? 0}</Text>
              <Text className={styles.statLabel}>帖子</Text>
            </View>
            <View className={styles.statItem}>
              <Text className={styles.statValue}>{user.followCount ?? 0}</Text>
              <Text className={styles.statLabel}>关注</Text>
            </View>
            <View className={styles.statItem}>
              <Text className={styles.statValue}>{user.fanCount ?? 0}</Text>
              <Text className={styles.statLabel}>粉丝</Text>
            </View>
            <View className={styles.statItem}>
              <Text className={styles.statValue}>{user.likeCount ?? 0}</Text>
              <Text className={styles.statLabel}>获赞</Text>
            </View>
          </View>
        )}
      </View>

      <View className={styles.menu}>
        {MENU.map((m) => (
          <View key={m.id} className={styles.menuItem} onClick={() => handleMenu(m.id)}>
            <View className={styles.menuIcon} style={{ background: m.color }}>
              {m.icon}
            </View>
            <Text className={styles.menuLabel}>{m.label}</Text>
            <Text className={styles.menuArrow}>›</Text>
          </View>
        ))}
      </View>

      {user ? (
        <View className={styles.logoutBtn} onClick={handleLogout}>退出登录</View>
      ) : (
        <View className={styles.loginPrompt} onClick={goLogin}>立即登录</View>
      )}
    </View>
  );
};

export default MinePage;
