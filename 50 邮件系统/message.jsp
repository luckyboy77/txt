<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>邮箱</title>
<style type="text/css">
	.tim{
		display:none;
	}
</style>
</head>
<body>
	<form action="message.do">
		收件人:<input type="text" name="to" class="to"><br>
		抄送:<input type="text" name="cc" class="cc"><br>
		密送:<input type="text" name="bcc" class="bcc"><br>
		标题:<input type="text" name="title" class="title"><br>
		正文:<textarea rows="50" cols="50" name="area" class="area"></textarea><br>
		<input type="submit" value="发送">
		<input type="button" value="定时发送" class="timer">
	</form>
	<div class="tim">
		<input type="text" class="year">年
		<input type="text" class="month">月
		<input type="text" class="day">日
		<input type="text" class="hour">时
		<input type="text" class="minunt">分
		<input type="text" class="second">秒<br>
		<input type="button" value="确定" class="yes">
	</div>
</body>
<script type="text/javascript" src="js/jquery-1.9.0.min.js"></script>
<script type="text/javascript">
	$('.timer').click(function(){
		$('.tim').css("display","block");
		var date = new Date();
		$('.year').val(date.getFullYear());
		$('.month').val(date.getMonth()+1);
		$('.day').val(date.getDate());
		$('.hour').val(date.getHours());
		$('.minunt').val(date.getMinutes());
		$('.second').val('00');
	});
	$('.yes').click(function(){
		alert("45689");
		$.ajax({
			url:"timeMessage.do",
			data:{
				to : $('.to').val(),
				cc : $('.cc').val(),
				bcc : $('.bcc').val(),
				title : $('.title').val(),
				area : $('.area').val(),
				hour : $('.hour').val(),
				minunt : $('.minunt').val(),
				second : $('.second').val(),
			},
			type:"post",
			dataType:"text",
			success:function(data){
				alert("成功");
			},
			error:function(){
				alert("error");
			}
		});
	});
</script>
</html>