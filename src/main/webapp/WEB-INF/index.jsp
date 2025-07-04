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
    <title>加入</title>
    <style>
        input[type="text"] {
            width: 30%;
            height: 35px;
            padding: 5px;
            font-size: 16px;
            color: #333;
            border: 2px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 0 5px #ccc;
            text-align: center;
            margin: 10px auto;
        }

        input[type="text"]:focus {
            outline: none;
            border: 2px solid #0099ff;
            box-shadow: 0 0 5px #0099ff;
        }

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
    <input type="text" id="userName" placeholder="用户名"/>
    <input type="text" id="password" placeholder="密码"/>
    <div class="button-container">
        <button id="submitButton" onclick="register()">新玩家加入</button>
        <button id="loginButton" onclick="login()">登录加入</button>
    </div>
</div>

</body>

<script type="text/javascript">
    // JavaScript 代码
    const userName = document.getElementById("userName");
    const password = document.getElementById("password");
    const submitButton = document.getElementById("submitButton");
    const loginButton = document.getElementById("submitButton");

    function register(){
        const reqParam = {};
        reqParam['userName'] = userName.value;
        reqParam['password'] = password.value;
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                var responseData = JSON.parse(xhr.responseText);
                if (responseData.success == true) {
                    // 将返回数据存储到本地存储中
                    localStorage.setItem("userToken", responseData.data);
                    console.log("数据已存储到本地存储中" + responseData.data);
                    //跳转到实时信息页面
                    window.location.href = "gameInfo";
                } else {
                    alert(responseData.message);
                }
            }
        };
        xhr.open("POST", "/manager/player/register", true);
        xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        xhr.send(JSON.stringify(reqParam));
    }

    function login(){
        const reqParam = {};
        reqParam['userName'] = userName.value;
        reqParam['password'] = password.value;
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                var responseData = JSON.parse(xhr.responseText);
                if (responseData.success == true) {
                    // 将返回数据存储到本地存储中
                    localStorage.setItem("userToken", responseData.data);
                    console.log("数据已存储到本地存储中");
                    //跳转到实时信息页面
                    window.location.href = "gameInfo";
                } else {
                    alert(responseData.message);
                }
            }
        };
        xhr.open("POST", "/manager/player/login", true);
        xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        xhr.send(JSON.stringify(reqParam));
    }

</script>

</html>
