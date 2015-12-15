package com.clinkworld.pay.util;

public class MobileUtil {
	/** */
	/**
	 * 中国移动拥有号码段为:139,138,137,136,135,134,159,158,157(3G),151,150,188(3G),187(3G
	 * );13个号段 中国联通拥有号码段为:130,131,132,156(3G),186(3G),185(3G);6个号段
	 * 中国电信拥有号码段为:133,153,189(3G),180(3G);4个号码段
	 */
	private static String regMobileStr = "^1(([3][456789])|([5][012789])|([8][78]))[0-9]{8}$";
	private static String regMobile3GStr = "^((157)|(18[78]))[0-9]{8}$";
	private static String regUnicomStr = "^1(([3][012])|([5][6])|([8][56]))[0-9]{8}$";
	private static String regUnicom3GStr = "^((156)|(18[56]))[0-9]{8}$";
	private static String regTelecomStr = "^1(([3][3])|([5][3])|([8][09]))[0-9]{8}$";
	private static String regTelocom3GStr = "^(18[09])[0-9]{8}$";

	private String mobile = "";
	private String yysh = "UNKNOWN";
	private boolean isMobile = false;
	private boolean is3G = false;

	public MobileUtil(String mobile) {
		this.setMobile(mobile);
	}

	public void setMobile(String mobile) {
		if (mobile == null) {
			return;
		}
		if (mobile.length() == 11) {
			/** */
			/** 第一步判断中国移动 */
			if (mobile.matches(MobileUtil.regMobileStr)) {
				this.mobile = mobile;
				this.setYysh("中国移动");
				this.setIsMobile(true);
				if (mobile.matches(MobileUtil.regMobile3GStr)) {
					this.set3G(true);
				}
			}
			/** */
			/** 第二步判断中国联通 */
			else if (mobile.matches(MobileUtil.regUnicomStr)) {
				this.mobile = mobile;
				this.setYysh("中国联通");
				this.setIsMobile(true);
				if (mobile.matches(MobileUtil.regUnicom3GStr)) {
					this.set3G(true);
				}
			}
			/** */
			/** 第三步判断中国电信 */
			else if (mobile.matches(MobileUtil.regTelecomStr)) {
				this.mobile = mobile;
				this.setYysh("中国电信");
				this.setIsMobile(true);
				if (mobile.matches(MobileUtil.regTelocom3GStr)) {
					this.set3G(true);
				}
			}
		}
	}

	public String getMobile() {
		return mobile;
	}

	public boolean isMobile() {
		return isMobile;
	}

	public boolean is3G() {
		return is3G;
	}

	private void setIsMobile(boolean isLawful) {
		this.isMobile = isLawful;
	}

	private void set3G(boolean is3G) {
		this.is3G = is3G;
	}

	public String getYysh() {
		return yysh;
	}

	public void setYysh(String yysh) {
		this.yysh = yysh;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("mobile:").append(this.getMobile()).append(",")
				.append("yysh:").append(this.getYysh()).append(",")
				.append("isLawful:").append(this.isMobile()).append(",")
				.append("is3G:").append(this.is3G()).append(";");
		return str.toString();
	}

	public static void main(String[] args) {
		MobileUtil mobile = new MobileUtil("13088817413");
		System.out.println(mobile.toString());
	}

	/**
	 * 还原11位手机号
	 *
	 * @param num
	 * @return
	 */
	public static String formatNumber(String num) {
		if (num != null && !num.equals("")) {
			if (num.startsWith("+86")) {
				num = num.substring(3);
			} else if (num.startsWith("86")) {
				num = num.substring(2);
			}
		} else {
			num = "";
		}
		return num;
	}
}
