/*
 * @Author: wanzp
 * @Date: 2023-04-26 21:15:54
 * @LastEditors: wzp123321 wanzhipengx@163.com
 * @LastEditTime: 2024-02-22 21:10:48
 * @Description:
 */
import { DirectiveBinding } from 'vue';
import { IDirectiveNumberBindingVO, IDirectiveTextBindingVO } from './directive-filter.api';

/**
 * "123-foo" will be parsed to 123
 * This is used for the .number modifier in v-model
 */
export const looseToNumber = (val: any): any => {
	const n = parseFloat(val);
	return isNaN(n) ? val : n;
};

export const addEventListener = (
	el: Element,
	event: string,
	handler: EventListener,
	options?: EventListenerOptions
) => {
	el.addEventListener(event, handler, options);
};

export function removeEventListener(
	el: Element,
	event: string,
	handler: EventListener,
	options?: EventListenerOptions
) {
	el.removeEventListener(event, handler, options);
}

export const deduplicate = (target: string, symbol: string): string => {
	if (target.includes(symbol)) {
		const temp = target.split(symbol);
		let str = `${temp.shift() ?? ''}${symbol}`;
		temp.filter((v) => v).forEach((v) => (str += v));
		return str;
	}
	return target;
};

/**
 * 过滤文本
 * @param domValue 文本内容
 * @param binding 指令传参
 * @returns
 */
export const handleTextFilter = (domValue: string, binding: DirectiveBinding<IDirectiveTextBindingVO>) => {
	const regExp = binding?.value?.regExp;
	const allowSpace =
		Object.prototype.toString.call(binding?.value?.allowSpace) === '[object Boolean]'
			? binding?.value?.allowSpace
			: true;
	const allowChinese =
		Object.prototype.toString.call(binding?.value?.allowChinese) === '[object Boolean]'
			? binding?.value?.allowChinese
			: true;

	const characters: string = '';
	const defaultStr = String.raw`\`\\;\'\"<>`;
	const reg = new RegExp(String.raw`[${defaultStr}${characters}]`, 'g');
	domValue = domValue?.replace(regExp && regExp instanceof RegExp ? regExp : reg, '');
	// 过滤空格
	if (!allowSpace) {
		domValue = domValue?.replace(/\s+/g, '');
	}
	if (!allowChinese) {
		domValue = domValue?.replace(/[^\x00-\xff]/g, '');
	}
	return domValue;
};

/**
 * 数字过滤
 * @param domValue 文本内容
 * @param binding 指令传参
 * @returns
 */
export const handleNumberFilter = (domValue: string, binding: DirectiveBinding<IDirectiveNumberBindingVO>) => {
	// 小数位
	const decimal = binding?.value?.decimal ?? 4;
	// 是否支持负数
	const negativeFlag =
		Object.prototype.toString.call(binding?.value?.negativeFlag) === '[object Boolean]'
			? binding?.value?.negativeFlag
			: false;
	// 是否支持0
	const zeroFlag =
		Object.prototype.toString.call(binding?.value?.zeroFlag) === '[object Boolean]' ? binding?.value?.zeroFlag : false;
	// 正数
	const integral = binding?.value?.integral ?? 10;
	// 最小值
	const min = binding?.value?.min;
	// 最大值
	const max = binding?.value?.max;
	// 正则
	const reg = new RegExp(String.raw`[^0-9${Math.ceil(decimal ?? 0) > 0 ? '.' : ''}${negativeFlag ? '-' : ''}]`, 'g');
	domValue = domValue.replace(reg, '');
	let symbol = '';
	// 处理符号
	if (domValue.substring(0, 1) === '-') {
		symbol = '-';
		domValue = domValue.substring(1);
	}
	domValue = domValue.replace(/[^0-9\.]/g, '');
	console.log(domValue);

	// 处理首位小数点
	if (domValue.substring(0, 1) === '.') {
		domValue = `0${domValue}`;
	}
	console.log(domValue);

	// 禁止头部连续输入0
	if (domValue.length > 1 && domValue.substring(0, 1) === '0' && domValue.substring(1, 2) !== '.') {
		domValue = domValue.substring(1);
	}
	console.log(domValue);

	// 是否支持0
	if (!zeroFlag && domValue.substring(0, 1) === '0') {
		domValue = '';
	}
	console.log(domValue);

	// 处理头部多余的0
	if (domValue.length > 1) {
		domValue = domValue.replace(/^0+(?!\.)/, '');
	}
	console.log(domValue);

	// 处理小数点及小数位数
	if (domValue.includes('.')) {
		domValue = deduplicate(domValue, '.');
		const temp = domValue.split('.');
		domValue = `${temp[0]}.${
			temp[1]?.substring(0, (Math.ceil(decimal ?? 0) > 0 ? Math.ceil(decimal) : null) as number) ?? ''
		}`;
	}
	console.log(domValue);

	// 限制整数长度
	const temp = domValue.split('.');
	temp[0] = temp[0].substring(0, Math.ceil(integral ?? 10));
	domValue = temp.length === 2 ? `${symbol}${temp[0]}.${temp[1]}` : `${symbol}${temp[0]}`;
	console.log(domValue);

	// 限制最大最小
	if (
		Object.prototype.toString.call(min) !== '[object Undefined]' &&
		Object.prototype.toString.call(min) !== '[object Null]' &&
		min &&
		domValue !== '' &&
		looseToNumber(domValue) < min
	) {
		domValue = min + '';
	}
	console.log(domValue);
	if (
		Object.prototype.toString.call(max) !== '[object Undefined]' &&
		Object.prototype.toString.call(max) !== '[object Null]' &&
		max &&
		domValue !== '' &&
		looseToNumber(domValue) > max
	) {
		domValue = max + '';
	}
	console.log(domValue);

	return domValue;
};
