/**
 * 发布帖子
 * - 标题 + 正文 + 分类 + 标签
 * - 草稿 / 提交
 */
import React, { useState, useEffect } from 'react';
import { View, Text, Input, Textarea } from '@tarojs/components';
import Taro from '@tarojs/taro';
import { publishPost, getCategories, getHotTags } from '@/services/api';
import { useUserStore } from '@/store/user';
import type { Category, Tag } from '@/types/model';
import styles from './index.module.scss';

const PostPublishPage: React.FC = () => {
  const { user } = useUserStore();
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState<string>('');
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const [c, t] = await Promise.all([getCategories(), getHotTags()]);
        setCategories(c);
        setTags(t);
      } catch (e) {
        console.error('[Publish] 加载分类/标签失败', e);
      }
    })();
  }, []);

  const toggleTag = (id: string) => {
    setSelectedTags((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
  };

  const handleSubmit = async () => {
    if (!user) {
      Taro.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    if (!title.trim()) {
      Taro.showToast({ title: '请输入标题', icon: 'none' });
      return;
    }
    if (!content.trim()) {
      Taro.showToast({ title: '请输入内容', icon: 'none' });
      return;
    }
    setSubmitting(true);
    try {
      await publishPost({ title, content, categoryId: category, tags: selectedTags });
      Taro.showToast({ title: '发布成功', icon: 'success' });
      setTimeout(() => Taro.navigateBack(), 800);
    } catch (e) {
      console.error('[Publish] 发布失败', e);
      Taro.showToast({ title: '发布失败', icon: 'none' });
    } finally {
      setSubmitting(false);
    }
  };

  const saveDraft = () => {
    Taro.setStorageSync('post_draft', { title, content, category, selectedTags });
    Taro.showToast({ title: '草稿已保存', icon: 'success' });
  };

  return (
    <View className={styles.page}>
      <View className={styles.form}>
        <Input
          className={styles.titleInput}
          value={title}
          onInput={(e) => setTitle(e.detail.value)}
          placeholder="请输入标题 (最多 30 字)"
          maxlength={30}
        />
        <Textarea
          className={styles.contentArea}
          value={content}
          onInput={(e) => setContent(e.detail.value)}
          placeholder="分享你的想法..."
          maxlength={2000}
          autoHeight
        />

        <Text className={styles.label}>选择分类</Text>
        <View className={styles.tagBox}>
          {categories.map((c) => (
            <Text
              key={c.id}
              className={`${styles.tagItem} ${category === c.id ? styles.tagItemActive : ''}`}
              onClick={() => setCategory(c.id)}
            >
              {c.icon} {c.name}
            </Text>
          ))}
        </View>

        <Text className={styles.label}>添加标签 (最多 3 个)</Text>
        <View className={styles.tagBox}>
          {tags.map((t) => (
            <Text
              key={t.id}
              className={`${styles.tagItem} ${selectedTags.includes(t.id) ? styles.tagItemActive : ''}`}
              onClick={() => selectedTags.length < 3 || selectedTags.includes(t.id) ? toggleTag(t.id) : Taro.showToast({ title: '最多选 3 个', icon: 'none' })}
            >
              #{t.name}
            </Text>
          ))}
        </View>
      </View>

      <View className={styles.actions}>
        <View className={styles.draftBtn} onClick={saveDraft}>存草稿</View>
        <View className={styles.submitBtn} onClick={handleSubmit}>
          {submitting ? '发布中...' : '立即发布'}
        </View>
      </View>
    </View>
  );
};

export default PostPublishPage;
