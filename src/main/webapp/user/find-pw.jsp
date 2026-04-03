<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>비밀번호 찾기</title>
</head>
<body>

<h2>비밀번호 찾기</h2>

<form id="findPwForm">
    <div>아이디: <input name="id" required></div><br>
    <div>이름: <input name="name" required></div><br>

    <div>
        이메일:
        <input name="email" id="findPwEmail" required>
        <button type="button" id="findPwEmailSendBtn">인증번호 받기</button>
    </div><br>

    <div>
        인증번호:
        <input name="emailCode" id="findPwEmailCode">
        <button type="button" id="findPwEmailCheckBtn">인증확인</button>
    </div><br>

    <input type="hidden" name="emailVerified" id="findPwEmailVerified" value="N">
    <input type="hidden" name="verifiedEmail" id="findPwVerifiedEmail">

    <button type="button" id="goResetPwBtn">다음</button>
</form>

<br>
<a href="${pageContext.request.contextPath}/login">로그인으로 돌아가기</a>

<script>
    window.appCtx = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/user/find-pw.js"></script>
</body>
</html>