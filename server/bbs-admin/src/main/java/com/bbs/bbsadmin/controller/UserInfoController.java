package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.vo.UserVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 前端控制器
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
    @PostMapping("/selectUserInfo")
    public R<UserVO> selectUserInfo(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        UserInfo userInfo = userInfoService.findByUserId(userId);
        if (userInfo != null) {
            // 返回 VO 而非实体: 实体带 password 字段, 会把密码哈希暴露给任意登录用户
            return R.data(UserVO.from(userInfo));
        } else {
            return R.fail("未查询到用户！");
        }
    }

}
