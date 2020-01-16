package com.mingdotyang.dtssubscribeclient.util;


public class CamelUtil {

	public static final char UNDERLINE = '_';

	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		Boolean flag = false;
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == UNDERLINE) {
				flag = true;
				continue;
			} else {
				if (flag == true) {
					sb.append(Character.toUpperCase(param.charAt(i)));
					flag = false;
				} else {
					sb.append(Character.toLowerCase(param.charAt(i)));
				}
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(underlineToCamel("studio_name"));
		System.out.println(underlineToCamel("activity_id"));
	}
}
