<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<table border="1">
    <tr>
        <th>일</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th>
    </tr>
    <tr>
        <%-- 1. 시작 요일까지 빈칸 만들기 --%>
        <c:forEach var="i" begin="1" end="${startDay - 1}">
            <td>&nbsp;</td>
        </c:forEach>

        <%-- 2. 1일부터 마지막 날까지 숫자 쓰기 --%>
        <c:forEach var="d" begin="1" end="${lastDay}">
        <td>${d}</td>

            <%-- 3. 토요일(7번째 칸)마다 줄 바꾸기 --%>
        <c:if test="${(d + startDay - 1) % 7 == 0}">
    </tr><tr>
    </c:if>
    </c:forEach>
</tr>
</table>
</body>
</html>
