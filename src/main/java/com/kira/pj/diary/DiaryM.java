package com.kira.pj.diary;


import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

public class DiaryM {

    public static void getCalendar(HttpServletRequest request) {

        Calendar cal = Calendar.getInstance();

        // 만약 파라미터로 년, 월이 들어온다면 처리하는 로직 (나중에 확장 가능)
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        // 이번 달 1일로 설정해서 시작 요일 구하기
        cal.set(year, month, 1);
        int startDay = cal.get(Calendar.DAY_OF_WEEK);

        // 이번 달 마지막 날짜 구하기
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 계산한 결과를 바로 request에 심어버리기! (수업 방식)
        request.setAttribute("startDay", startDay);
        request.setAttribute("lastDay", lastDay);
        request.setAttribute("curYear", year);
        request.setAttribute("curMonth", month + 1);
    }


}

