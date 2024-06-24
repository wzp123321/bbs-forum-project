/**
 * 菜单
 */
export interface CommonMenu {
  path: string;
  name: string;
  hasIcon?: boolean;
  meta: { title: string };
  children?: CommonMenu[];
}
