import dayjs from 'dayjs';
/**
 * 格式化时间
 * @param {number} stamp
 * @param {string} format
 * @returns {string}
 */
export const formatTimeStamp = (stamp: number, format: string): string => {
	return dayjs(new Date(stamp)).format(format);
};
