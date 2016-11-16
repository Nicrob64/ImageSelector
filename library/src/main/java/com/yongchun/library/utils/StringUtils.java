package com.yongchun.library.utils;

/**
 * Created by Nic on 16/11/16.
 */

public class StringUtils {
	public static String getStringForMsec(long msec){
		long totalSecs = msec/1000;
		long minutes = totalSecs / 60;
		long secs = totalSecs % 60;
		return String.format("%1$02d:%2$02d", minutes, secs);
	}
}
