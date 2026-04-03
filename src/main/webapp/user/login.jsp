<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
</head>
<body>

<form id="loginForm">
    <div>아이디: <input name="id"></div><br>
    <div>비밀번호: <input type="password" name="pw"></div><br>
    <button type="button" id="loginBtn">로그인</button>
</form>

<br>

<a href="${pageContext.request.contextPath}/find-id">아이디 찾기</a> |
<a href="${pageContext.request.contextPath}/find-pw">비밀번호 찾기</a> |
<a href="${pageContext.request.contextPath}/join">회원가입</a>

<script>
    window.appCtx = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/user/login.js"></script>
</body>
</html>