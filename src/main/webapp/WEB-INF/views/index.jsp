<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>系統入口</title>
	<style>
		body {
			font-family: Arial, sans-serif;
			background: #f3f3f3;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			height: 100vh;
			margin: 0;
		}
		h1 {
			color: #333;
			margin-bottom: 30px;
		}
		.entry-buttons {
			display: flex;
			gap: 20px;
		}
		.entry-buttons a {
			text-decoration: none;
			padding: 12px 24px;
			background-color: #007bff;
			color: white;
			border-radius: 5px;
			font-size: 16px;
			transition: background-color 0.3s ease;
		}
		.entry-buttons a:hover {
			background-color: #0056b3;
		}
		.message {
			margin-top: 20px;
			color: #555;
			font-size: 18px;
		}
	</style>
</head>
<body>
<h1>請選擇入口系統</h1>
<div class="entry-buttons">
	<a href="${pageContext.request.contextPath}/Clinic/login.html">診所端 登入</a>
	<a href="${pageContext.request.contextPath}/Patient/login.html">病人端 登入</a>
</div>

<c:if test="${not empty param.name}">
	<div class="message">歡迎，<strong>${param.name}</strong>！</div>
</c:if>
</body>
</html>