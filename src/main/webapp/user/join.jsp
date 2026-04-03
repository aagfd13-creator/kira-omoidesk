<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>회원가입</title>
</head>
<body>

<h2>회원가입</h2>

<form id="joinForm">
  <div>이름: <input name="name" required></div><br>
  <div>생년월일: <input type="date" name="birth" required></div><br>

  <div>
    아이디:
    <input name="id" id="id" required>
    <button type="button" id="idCheckBtn">중복확인</button>
  </div><br>

  <div>비밀번호: <input type="password" name="pw" required></div><br>
  <div>비밀번호 확인: <input type="password" name="pwChk" required></div><br>

  <div>
    닉네임:
    <input name="nickname" id="nickname" required>
    <button type="button" id="nicknameCheckBtn">중복확인</button>
  </div><br>

  <div>
    이메일:
    <input name="email" id="email" required>
    <button type="button" id="emailSendBtn">인증번호 받기</button>
  </div><br>

  <div>
    인증번호:
    <input name="emailCode" id="emailCode">
    <button type="button" id="emailCheckBtn">인증확인</button>
  </div><br>

  <input type="hidden" name="idChecked" id="idChecked" value="N">
  <input type="hidden" name="checkedId" id="checkedId">

  <input type="hidden" name="nicknameChecked" id="nicknameChecked" value="N">
  <input type="hidden" name="checkedNickname" id="checkedNickname">

  <input type="hidden" name="emailVerified" id="emailVerified" value="N">
  <input type="hidden" name="verifiedEmail" id="verifiedEmail">

  <button type="button" id="joinBtn">회원가입</button>
</form>

<script>
  window.appCtx = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/user/join.js"></script>
</body>
</html>