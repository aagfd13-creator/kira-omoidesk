<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>비밀번호 재설정</title>
</head>
<body>

<h2>새 비밀번호 설정</h2>

<form id="resetPwForm">
  <input type="hidden" name="id" id="resetId">
  <input type="hidden" name="name" id="resetName">
  <input type="hidden" name="email" id="resetEmail">

  <div>새 비밀번호: <input type="password" name="newPw" required></div><br>
  <div>새 비밀번호 확인: <input type="password" name="newPwChk" required></div><br>

  <button type="button" id="resetPwBtn">비밀번호 변경</button>
</form>

<script>
  window.appCtx = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/user/reset-pw.js"></script>
</body>
</html>