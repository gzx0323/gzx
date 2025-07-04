package gzx.zxge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@RequestMapping("/page")
@Controller
public class PageController {

    /**
     * 主页
     */
    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/gameInfo")
    public String gameInfo(){
        return "gameInfo";
    }

    @GetMapping("/manager")
    public String manager(){
        return "manager";
    }
}
