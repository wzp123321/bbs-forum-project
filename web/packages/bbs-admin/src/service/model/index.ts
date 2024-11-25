import { Component } from 'vue';

/**
 * 菜单
 */
export interface CommonMenu {
  path: string;
  name: string;
  icon?: string;
  index: string;
  meta: { title: string };
  children?: CommonMenu[];
  component?: Component;
}
