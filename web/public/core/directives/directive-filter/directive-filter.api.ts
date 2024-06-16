/*
 * @Author: wanzp
 * @Date: 2023-04-18 20:47:57
 * @LastEditors: wanzp
 * @LastEditTime: 2023-05-22 22:07:27
 * @Description:
 */
export enum EDirectiveType {
  文本 = 'text',
  数字 = 'number',
}

/**
 * @param allowChinese 是否允许中文
 * @param allowSpace 是否允许空格
 * @param regExp 正则
 */
export interface IDirectiveTextBindingVO {
  allowChinese: boolean;
  allowSpace: boolean;
  regExp: RegExp | null;
}

/**
 * @param integral 整数位
 * @param decimal 小数位
 * @param negativeFlag 是否支持负数
 * @param zeroFlag 是否支持0
 * @param min 最小
 * @param max 最大
 */
export interface IDirectiveNumberBindingVO {
  integral: number;
  decimal: number;
  negativeFlag: boolean;
  zeroFlag: boolean;
  min: number | null;
  max: number | null;
}
