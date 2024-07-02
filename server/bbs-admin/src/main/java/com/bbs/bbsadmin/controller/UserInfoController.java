package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhcao
 * @since 2024-07-01
 */
@Tag(name = "UserInfoController", description = "用户信息")
@RestController
@RequestMapping("admin/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "查询用户信息")
    @GetMapping("/selectUserInfo/{userId}")
    public R selectUserInfo(@PathVariable("userId") String userId) {
        UserInfo userInfo = userInfoService.findByUserId(userId);
        if(userInfo != null) {
            return R.data(userInfo);
        }else {
            return R.fail("未查询到用户！");
        }
    }

}
