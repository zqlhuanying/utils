package com.example.utils.controller.hotdeploy;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuangqianliao
 * 测试 Spring boot的热部署
 * 1. 引入Spring-dev-tools
 * 2. IDEA 开启设置
 *  2.1. “File” -> “Settings” -> “Build,Execution,Deplyment” -> “Compiler”
 *       选中打勾 “Build project automatically”
 *  2.2. 组合键：“Shift+Ctrl+Alt+/” ，选择 “Registry” ，
 *       选中打勾 “compiler.automake.allow.when.app.running” 。
 * 3. 启动项目即可
 * 注意：IDEA上面的设置貌似无效，需要 Ctrl+Shift+F9 手动重新编译
 */
@RestController
@RequestMapping(value = "deploy")
public class HotDeployController {

    @RequestMapping(value = "test1")
    @ResponseBody
    public String test() {
        return helloDeploy() + " " + helloDeploy1();
    }

    private String helloDeploy() {
        return "Hello Deploy";
    }

    private String helloDeploy1() {
        return "Hello Deploy";
    }
}
