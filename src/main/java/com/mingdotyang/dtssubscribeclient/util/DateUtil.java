package com.mingdotyang.dtssubscribeclient.util;

import java.sql.Timestamp;

/**
 * @author ming yang
 */
public class DateUtil {


	public static Timestamp toTimestamp(Long timestamp) {
		return new Timestamp(timestamp);
	}
}
