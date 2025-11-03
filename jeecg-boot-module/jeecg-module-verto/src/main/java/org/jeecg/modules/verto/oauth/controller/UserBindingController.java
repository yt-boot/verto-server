package org.jeecg.modules.verto.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysThirdAccount;
import org.jeecg.modules.system.service.ISysThirdAccountService;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 与前端 UserSetting.api.ts 对齐的绑定接口
 */
@RestController
@RequestMapping("/verto/user")
@RequiredArgsConstructor
public class UserBindingController {

    private final IOAuthService oauthService;
    private final ISysThirdAccountService sysThirdAccountService;

    /**
     * 绑定系统用户与第三方平台（gitlab）的账户
     * POST /verto/user/bindThirdAppAccount
     * body: { userId, thirdType, thirdUserId }
     */
    @PostMapping("/bindThirdAppAccount")
    public Result<?> bindThirdAppAccount(@RequestBody Map<String, Object> body) {
        // 兼容前端只传 thirdType 和 thirdUserUuid 的场景
        String thirdType = String.valueOf(body.get("thirdType"));
        String thirdUserUuid = String.valueOf(body.getOrDefault("thirdUserUuid", body.get("thirdUserId")));

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = body.get("userId") != null ? String.valueOf(body.get("userId")) : (sysUser != null ? sysUser.getId() : null);
        if (!StringUtils.hasText(userId)) {
            return Result.error("未获取到系统用户ID，绑定失败");
        }
        if (!StringUtils.hasText(thirdType) || !StringUtils.hasText(thirdUserUuid)) {
            return Result.error("参数不足：第三方类型或UUID缺失");
        }

        // 1) 先绑定系统内置的 sys_third_account（以兼容现有前端展示与逻辑）
        try {
            SysThirdAccount req = new SysThirdAccount();
            req.setThirdType(thirdType);
            req.setThirdUserUuid(thirdUserUuid);
            sysThirdAccountService.bindThirdAppAccountByUserId(req);
        } catch (Exception e) {
            // 忽略冲突等异常，由后续返回统一提示
        }

        // 2) 再写入 oauth_binding，并清理该第三方用户历史 token
        try {
            boolean bound = oauthService.bindUserAccount(userId, thirdType, thirdUserUuid);
            if (bound) {
                oauthService.cleanupTokensForOauthUser(thirdType, thirdUserUuid);
            }
        } catch (Exception e) {
            return Result.error("OAuth绑定失败: " + e.getMessage());
        }
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
     * 删除指定系统用户在指定平台的 access_token（通过绑定关系定位到第三方用户）。
     * 这满足“根据 sysUser.getId() 可以删除对应的 access_token”的需求。
     */
    @PostMapping("/deleteAccessToken")
    public Result<?> deleteAccessToken(@RequestBody Map<String, Object> body) {
        String userId = String.valueOf(body.get("userId"));
        String thirdType = String.valueOf(body.get("thirdType"));
        boolean ok = oauthService.deleteAccessTokensBySystemUser(userId, thirdType);
        return ok ? Result.OK("删除成功") : Result.error("未删除任何记录（可能未绑定或无 token）");
    }

    /**
     * 获取第三方账号（与 /sys/thirdApp/getThirdAccountByUserId 等价，增加了 Verto 扩展）
     * @param thirdType 第三方类型（如 github、gitlab）
     */
    @GetMapping("/getThirdAccountByUserId")
    public Result<List<SysThirdAccount>> getThirdAccountByUserId(@RequestParam(name = "thirdType") String thirdType) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
        // 根据当前登录用户ID查询
        query.eq(SysThirdAccount::getSysUserId, sysUser.getId());
        // 扫码登录仅租户为0
        query.eq(SysThirdAccount::getTenantId, CommonConstant.TENANT_ID_DEFAULT_VALUE);
        // 根据第三方类别查询
        if (oConvertUtils.isNotEmpty(thirdType)) {
            query.in(SysThirdAccount::getThirdType, Arrays.asList(thirdType.split(",")));
        }
        List<SysThirdAccount> list = sysThirdAccountService.list(query);
        return Result.ok(list);
    }

    /**
     * 删除第三方用户信息（与 /sys/thirdApp/deleteThirdAccount 等价，额外同步清理 oauth_binding 与 token）
     */
    @DeleteMapping("/deleteThirdAccount")
    public Result<String> deleteThirdAccountById(@RequestBody SysThirdAccount sysThirdAccount) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (!sysUser.getId().equals(sysThirdAccount.getSysUserId())) {
            return Result.error("无权修改他人信息");
        }
        SysThirdAccount thirdAccount = sysThirdAccountService.getById(sysThirdAccount.getId());
        if (thirdAccount == null) {
            return Result.error("未找到该第三方账户信息");
        }
        // 先删除系统第三方账号记录
        sysThirdAccountService.removeById(thirdAccount.getId());
        // 再删除 verto oauth 绑定关系与 token
        try {
            oauthService.unbindUserAccount(sysUser.getId(), thirdAccount.getThirdType());
            oauthService.deleteAccessTokensBySystemUser(sysUser.getId(), thirdAccount.getThirdType());
        } catch (Exception e) {
            // 不影响主流程，记录异常即可
        }
        return Result.ok("解绑成功");
    }
}