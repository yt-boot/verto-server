package org.jeecg.modules.verto.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 与前端 UserSetting.api.ts 对齐的绑定接口
 */
@RestController
@RequestMapping("/verto/user")
@RequiredArgsConstructor
public class UserBindingController {

    private final IOAuthService oauthService;

    /**
     * 绑定系统用户与第三方平台（gitlab）的账户
     * POST /verto/user/bindThirdAppAccount
     * body: { userId, thirdType, thirdUserId }
     */
    @PostMapping("/bindThirdAppAccount")
    public Result<?> bindThirdAppAccount(@RequestBody Map<String, Object> body) {
        String userId = String.valueOf(body.get("userId"));
        String thirdType = String.valueOf(body.get("thirdType"));
        String thirdUserId = String.valueOf(body.get("thirdUserId"));
        oauthService.bindUserAccount(userId, thirdType, thirdUserId);
        return Result.OK("绑定成功");
    }

    /**
     * 解绑
     */
    @PostMapping("/unbindThirdAppAccount")
    public Result<?> unbindThirdAppAccount(@RequestBody Map<String, Object> body) {
        String userId = String.valueOf(body.get("userId"));
        String thirdType = String.valueOf(body.get("thirdType"));
        oauthService.unbindUserAccount(userId, thirdType);
        return Result.OK("解绑成功");
    }

    /**
     * 获取用户绑定信息
     */
    @GetMapping("/getThirdAccountByUserId")
    public Result<?> getThirdAccountByUserId(@RequestParam("userId") String userId) {
        return Result.OK(oauthService.getUserBindings(userId));
    }
}