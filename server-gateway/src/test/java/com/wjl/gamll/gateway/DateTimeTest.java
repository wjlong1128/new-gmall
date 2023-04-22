package com.wjl.gamll.gateway;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public class DateTimeTest {

    public static void main(String[] args) {
        LocalDate now = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.MAX;
        LocalDateTime dateTime = LocalDateTime.of(now, time);
        System.out.println(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")));
    }

}
