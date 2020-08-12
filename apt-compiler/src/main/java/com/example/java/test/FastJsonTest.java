package com.example.java.test;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:無忌
 * @date:2020/8/7
 * @description:
 */
public class FastJsonTest {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("name","wuji");
        map.put("address","beijing");
        String jsonString = JSON.toJSONString(map);
        System.out.println(jsonString);
    }
}
