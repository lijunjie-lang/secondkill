
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>秒杀页面</title>
</head>
<body>
<form method="post" action="${pageContext.request.contextPath}/doSk">
    <input type="hidden" name="pid" value="1001"/>
    <input type="button" value="点击参与1元秒杀华为MATE30PRO 5G"/>
</form>
<script type="text/javascript" src="jquery/jquery-2.1.1.min.js"></script>
<script type="text/javascript">
    $("form :button").click(function () {
        $.ajax({
            "type":"POST",
            "url": $("form").prop("action"),
            "data":$("form").serialize(),
            "success": function (code) {
                if(code=="200"){
                    alert("秒杀成功");
                }else if(code=="10001"){
                    alert("请勿重复秒杀");
                }else if(code=="10002"){
                    alert("秒杀尚未开始");
                }else if(code=="10003"){
                    alert("库存不足");
                }
            }
        });
    });

</script>
</body>
</html>
