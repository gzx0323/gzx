<%--
  Created by IntelliJ IDEA.
  User: pn
  Date: 2023/3/25
  Time: 16:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>后台管理</title>
    <style>

        button {
            display: inline-block;
            margin: 0 10px;
            background-color: #137f44;
            color: #fff;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s ease-in-out;
        }

        button:hover {
            background-color: #00cc0a;
        }

        button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        button:first-of-type {
            margin-right: 10px;
        }

        .container {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            flex-direction: column;
        }

        .button-container {
            display: flex;
            justify-content: center;
            align-items: center;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="button-container">
        <button id="startGameButton" onclick="buttonReq('startGameButton')">开始游戏</button>
        <button id="restartGameButton" onclick="buttonReq('restartGameButton')">重启游戏</button>
        <button id="queryPlayerButton" onclick="buttonReq('queryPlayerButton')">查询玩家</button>
    </div>
</div>

</body>

<script type="text/javascript">

    function buttonReq(type){
        if (type === 'restartGameButton'){
            let confirmed = confirm('确定要重启游戏吗，这将会清除所有玩家的信息');
            if (!confirmed) {
                return;
            }
        }
        const reqParam = {};
        reqParam['buttonType'] = type;
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var responseData = JSON.parse(xhr.responseText);
                if (responseData.success === true) {
                    if (responseData.data){
                        alert(responseData.data);
                    }
                } else {
                    alert(responseData.message);
                }
            }
        };
        xhr.open("POST", "/manager/buttonReq", true);
        xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        xhr.send(JSON.stringify(reqParam));
    }


</script>

</html>
