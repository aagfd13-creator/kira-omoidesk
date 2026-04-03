<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>아이디 찾기</title>
</head>
<body>

<h2>아이디 찾기</h2>

<form id="findIdForm">
    <div>이름: <input name="name" required></div><br>

    <div>
        이메일:
        <input name="email" id="findIdEmail" required>
        <button type="button" id="findIdEmailSendBtn">인증번호 받기</button>
    </div><br>

    <div>
        인증번호:
        <input name="emailCode" id="findIdEmailCode">
        <button type="button" id="findIdEmailCheckBtn">인증확인</button>
    </div><br>

    <input type="hidden" name="emailVerified" id="findIdEmailVerified" value="N">
    <input type="hidden" name="verifiedEmail" id="findIdVerifiedEmail">

    <button type="button" id="findIdBtn">아이디 찾기</button>
</form>

<hr>
<div id="foundIdArea"></div>

<br>
<a href="${pageContext.request.contextPath}/login">로그인으로 돌아가기</a>

<script>
    window.appCtx = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/user/find-id.js"></script>
</body>
</html>