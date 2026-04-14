package com.example.hospitalsystem.utils;

import java.util.Random;

// 生成6位随机验证码
public class CodeUtil {
    public static String randomVerify() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }
}