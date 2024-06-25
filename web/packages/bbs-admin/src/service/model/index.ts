import { Component } from 'vue';

/**
 * 菜单
 */
export interface CommonMenu {
  path: string;
  name: string;
  hasIcon?: boolean;
  index: string;
  meta: { title: string };
  children?: CommonMenu[];
  component?: Component;
}
