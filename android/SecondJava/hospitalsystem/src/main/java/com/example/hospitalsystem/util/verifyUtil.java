package com.example.hospitalsystem.util;

public class verifyUtil {

    // 判断是否是邮箱
    public static boolean isEmail(String input) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return input != null && input.matches(emailRegex);
    }

    // 判断是否是11位手机号
    public static boolean isPhoneNumber(String input) {
        String phoneRegex = "^1[3-9]\\d{9}$"; // 中国大陆手机号正则
        return input != null && input.matches(phoneRegex);
    }

}
