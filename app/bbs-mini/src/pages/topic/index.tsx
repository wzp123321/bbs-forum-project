/**
 * 话题 - 分类网格 + 热门标签
 */
import React, { useEffect, useState } from 'react';
import { View, Text } from '@tarojs/components';
import Taro from '@tarojs/taro';
import { getCategories, getHotTags } from '@/services/api';
import type { Category, Tag } from '@/types/model';
import styles from './index.module.scss';

const TopicPage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);

  useEffect(() => {
    (async () => {
      try {
        const [c, t] = await Promise.all([getCategories(), getHotTags()]);
        setCategories(c);
        setTags(t);
      } catch (e) {
        console.error('[Topic] 加载分类/标签失败', e);
        Taro.showToast({ title: '加载失败', icon: 'none' });
      }
    })();
  }, []);

  const goCategory = (c: Category) => {
    Taro.navigateTo({ url: `/pages/topic-list/index?categoryId=${c.id}&name=${encodeURIComponent(c.name)}` });
  };

  const goTag = (t: Tag) => {
    Taro.navigateTo({ url: `/pages/topic-list/index?tagId=${t.id}&name=${encodeURIComponent(t.name)}` });
  };

  return (
    <View className={styles.page}>
      <View className={styles.section}>
        <View className={styles.sectionTitle}>
          <Text>分类</Text>
          <Text className={styles.sectionMore}>共 {categories.length} 个</Text>
        </View>
        <View className={styles.grid}>
          {categories.map((c) => (
            <View key={c.id} className={styles.gridItem} onClick={() => goCategory(c)}>
              <View className={styles.gridIcon}>{c.icon}</View>
              <Text className={styles.gridName}>{c.name}</Text>
              <Text className={styles.gridCount}>{c.postCount} 帖</Text>
            </View>
          ))}
        </View>
      </View>

      <View className={styles.section}>
        <View className={styles.sectionTitle}>
          <Text>热门标签</Text>
          <Text className={styles.sectionMore}>实时更新</Text>
        </View>
        <View className={styles.tagCloud}>
          {tags.map((t) => (
            <Text key={t.id} className={styles.tagItem} onClick={() => goTag(t)}>#{t.name}</Text>
          ))}
        </View>
      </View>
    </View>
  );
};

export default TopicPage;
