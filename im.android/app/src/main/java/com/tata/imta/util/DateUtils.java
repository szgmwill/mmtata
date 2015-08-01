package com.tata.imta.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 日期工具类
 * 
 * @author zhangweirui
 * 
 */
public class DateUtils {
	/** 日期格式 */
	public static final String DATE_DAY_FORMAT = "yyyy-MM-dd";
	/** 时间格式 */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** 时间格式 */
	public static final String TIME_FORMAT = "HH:mm:ss";

	public static final String DATE_DAY_FORMAT2 = "yyyyMMdd";

	/**
	 * 解析日期
	 *
	 * @param dateStr
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateStr(String dateStr, String dateFormat)
			throws ParseException {
		SimpleDateFormat ft = new SimpleDateFormat(dateFormat);
		Date date = ft.parse(dateStr);
		return date;
	}

	/**
	 * 计算两个日期的时间差
	 *
	 * @param dateStr1
	 * @param dateStr2
	 * @return
	 * @throws ParseException
	 */
	public static int computeDateDifference(String dateStr1, String dateStr2)
			throws ParseException {
		Date date1 = parseDateStr(dateStr1, DATE_DAY_FORMAT);
		Date date2 = parseDateStr(dateStr2, DATE_DAY_FORMAT);
		int days1 = (int) TimeUnit.MILLISECONDS.toDays(date1.getTime());
		int days2 = (int) TimeUnit.MILLISECONDS.toDays(date2.getTime());
		return days2 - days1;

	}

	/**
	 * 计算两个日期的日期差
	 *
	 */
	public static int computeDateDifference(String dateStr1, Date date2)
			throws ParseException {
		Date date1 = parseDateStr(dateStr1, DATE_DAY_FORMAT);
		int days1 = (int) TimeUnit.MILLISECONDS.toDays(date1.getTime());
		int days2 = (int) TimeUnit.MILLISECONDS.toDays(date2.getTime());
		return days2 - days1;

	}

	/**
	 * 计算两个日期的小时差
	 *
	 */
	public static int computeHoursDifference(String dateStr1, Date date2)
			throws ParseException {
		Date date1 = parseDateStr(dateStr1, DATE_TIME_FORMAT);
		int hours1 = (int) TimeUnit.MILLISECONDS.toHours(date1.getTime());
		int hours2 = (int) TimeUnit.MILLISECONDS.toHours(date2.getTime());
		return hours2 - hours1;
	}

	/**
	 * 计算两个日期的分钟差
	 *
	 */
	public static long computeMinutesDifference(String dateStr1, Date date2)
			throws ParseException {
		Date date1 = parseDateStr(dateStr1, DATE_TIME_FORMAT);
		long minutes1 = (long) TimeUnit.MILLISECONDS.toMinutes(date1.getTime());
		long minutes2 = (long) TimeUnit.MILLISECONDS.toMinutes(date2.getTime());
		return minutes2 - minutes1;
	}

	/**
	 * 计算两个日期的秒差
	 *
	 */
	public static long computeSecondsDifference(String dateStr1, Date date2)
			throws ParseException {
		Date date1 = parseDateStr(dateStr1, DATE_TIME_FORMAT);
		long minutes1 = (long) TimeUnit.MILLISECONDS.toSeconds(date1.getTime());
		long minutes2 = (long) TimeUnit.MILLISECONDS.toSeconds(date2.getTime());
		return minutes2 - minutes1;
	}

	/**
	 * 获取当前时间字符串
	 *
	 * @return
	 */
	public static String getCurrentTimeStr() {
		SimpleDateFormat ft = new SimpleDateFormat(DATE_TIME_FORMAT);
		return ft.format(new Date());
	}

	/**
	 * 获取当前日期字符串
	 *
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat ft = new SimpleDateFormat(DATE_DAY_FORMAT);
		return ft.format(new Date());
	}

	/**
	 * 获取当前时间前面一段时间。参数单位是分钟
	 *
	 * @param minute
	 * @return
	 */
	public static String getTimeStrBeforeNow(long minute) {
		long time = System.currentTimeMillis() - minute * 60 * 1000;
		Date date = new Date(time);

		SimpleDateFormat ft = new SimpleDateFormat(DATE_TIME_FORMAT);
		return ft.format(date);
	}

	/**
	 * 比较两个时间字符串的先后。
	 *
	 * @param timeStr1
	 * @param timeStr2
	 * @return
	 * @throws ParseException
	 */
	public static boolean isBefore(String timeStr1, String timeStr2)
			throws ParseException {
		Date date1 = parseDateStr(timeStr1, DATE_TIME_FORMAT);
		Date date2 = parseDateStr(timeStr2, DATE_TIME_FORMAT);
		return date1.before(date2);
	}

	/**
	 * 判断当前时间时间是否在两个时间之中
	 *
	 * @param timeStr1
	 * @param timeStr2
	 * @return
	 * @throws ParseException
	 */
	public static boolean isInTimeRange(String timeStr1, String timeStr2)
			throws ParseException {
		Date date1 = parseDateStr(timeStr1, TIME_FORMAT);
		Date date2 = parseDateStr(timeStr2, TIME_FORMAT);

		SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT);
		Date curr = ft.parse(ft.format(new Date()));
		return (date1.before(curr) && curr.before(date2));
	}


	/**
	 * 获取指定日期: YYYYMMDD
	 *
	 * @return
	 */
	public static int getDayFromDate(Date date) {
		int fday = 0;

		if (date == null) {
			// 默认当天
			date = new Date();
		}

		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_DAY_FORMAT2);
			String dateStr = sdf.format(date);
			if (dateStr != null) {
				fday = Integer.parseInt(dateStr);
			}
		}
		return fday;
	}


	/**
	 * 获取当天日期 yyyy-mm-dd
	 */
	public static String getCurrentDayStr() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_DAY_FORMAT);
		return sdf.format(new Date());
	}

	/**
	 * 获取指定日期字符串 yyyy-mm-dd
	 */
	public static String getDayStrFromDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_DAY_FORMAT);
		return sdf.format(date);
	}

	/**
	 * 格式转换日期:yyyy-mm-dd
	 *
	 */
	public static Date getDateFromDayStr(String date) {
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_DAY_FORMAT);
				return sdf.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block

			}
		}
		return null;
	}

	/**
	 * 格式转换日期:yyyy-mm-dd HH:MM:SS
	 *
	 */
	public static Date getDateFromTimeStr(String time) {
		if (time != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
				return sdf.parse(time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block

			}
		}
		return null;
	}

	/**
	 * 获取指定时间字符串
	 *
	 * @return
	 */
	public static String getTimeStrFromDate(Date date) {
		SimpleDateFormat ft = new SimpleDateFormat(DATE_TIME_FORMAT);
		return ft.format(date);
	}

	/**
	 * 校验合法的时间格式
	 */
	public static Date getValidDayOrTime(String date) {

		Date validDate = getDateFromDayStr(date);
		if (validDate == null) {
			validDate = getDateFromTimeStr(date);
		}
		return validDate;
	}

	public static boolean isValidDayOrTime(String date) {
		Date validDate = getValidDayOrTime(date);
		return (validDate == null ? false : true);
	}

	/**
	 * 根据生日得到年龄
	 */
	public static int getAgeFromDate(Date birth) {
		int age = 0;
		if(birth != null) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(birth);
			Calendar c2 = Calendar.getInstance();
			int birthYear = c1.get(Calendar.YEAR);
			int curYear = c2.get(Calendar.YEAR);

			age = curYear-birthYear;
		}

		return age;
	}

	/**
	 * 获得对应星座
	 */
	public static String star(Date birth) {
		String star = "";
		if (birth == null) {
			return star;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(birth);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		if (month == 1 && day >= 20 || month == 2 && day <= 18) {
			star = "水瓶座";
		}
		if (month == 2 && day >= 19 || month == 3 && day <= 20) {
			star = "双鱼座";
		}
		if (month == 3 && day >= 21 || month == 4 && day <= 19) {
			star = "白羊座";
		}
		if (month == 4 && day >= 20 || month == 5 && day <= 20) {
			star = "金牛座";
		}
		if (month == 5 && day >= 21 || month == 6 && day <= 21) {
			star = "双子座";
		}
		if (month == 6 && day >= 22 || month == 7 && day <= 22) {
			star = "巨蟹座";
		}
		if (month == 7 && day >= 23 || month == 8 && day <= 22) {
			star = "狮子座";
		}
		if (month == 8 && day >= 23 || month == 9 && day <= 22) {
			star = "处女座";
		}
		if (month == 9 && day >= 23 || month == 10 && day <= 22) {
			star = "天秤座";
		}
		if (month == 10 && day >= 23 || month == 11 && day <= 21) {
			star = "天蝎座";
		}
		if (month == 11 && day >= 22 || month == 12 && day <= 21) {
			star = "射手座";
		}
		if (month == 12 && day >= 22 || month == 1 && day <= 19) {
			star = "摩羯座";
		}
		return star;
	}

	public static void main(String[] args) {
		String testDate = "1989-01-28";
		Date birth = getDateFromDayStr(testDate);
		System.out.println("age:"+getAgeFromDate(birth)+",xz:"+star(birth));
	}
}
