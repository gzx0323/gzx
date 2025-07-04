package gzx.zxge.controller;

import gzx.zxge.pojo.ApiResult;
import gzx.zxge.service.GameEngine;
import gzx.zxge.service.PlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@CrossOrigin
@RequestMapping("/manager")
@Controller
public class GameManagerController extends GlobalExceptionHandler {

    @Autowired
    private GameEngine engine;

    @Resource
    private PlayerManager playerManager;

    @ResponseBody
    @GetMapping("/changeGameStatus")
    public Boolean changeGameStatus(@RequestParam("status") String status){
        return engine.changeGameStatus(status);
    }

    @ResponseBody
    @PostMapping("/buttonReq")
    public ApiResult buttonReq(@RequestBody Map<String, String> param){
        return engine.managerButtonReq(param);
    }

    /**
     * 查询游戏状态
     */
    @ResponseBody
    @GetMapping("/queryGameInfo")
    public ApiResult queryGameInfo(){
        return engine.queryGameInfo();
    }

    /**
     * 玩家注册
     */
    @ResponseBody
    @PostMapping("/player/register")
    public ApiResult playerRegister(@RequestBody Map<String, String> param){
        return playerManager.playerRegister(param);
    }

    /**
     * 玩家登录
     */
    @ResponseBody
    @PostMapping("/player/login")
    public ApiResult playerLogin(@RequestBody Map<String, String> param){
        return playerManager.playerLogin(param);
    }

}
