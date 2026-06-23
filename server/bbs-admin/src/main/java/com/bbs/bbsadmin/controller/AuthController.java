package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.LoginDTO;
import com.bbs.bbsadmin.entity.vo.LoginVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.JwtUtil;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "AuthController", description = "登录鉴权")
@RestController
@RequestMapping("admin/auth")
public class AuthController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        UserInfo user = userInfoService.findByAccount(dto.getAccount());
        if (user == null) {
            throw new BizException(ResponseCode.USER_NOT_FOUND);
        }
        // 兼容老数据: 明文密码 (没有 BCrypt 前缀) 也允许登录一次,登录后自动升级为 BCrypt
        String stored = user.getPassword();
        boolean matched;
        if (stored != null && stored.startsWith("$2a$")) {
            matched = BCrypt.checkpw(dto.getPassword(), stored);
        } else {
            matched = stored != null && stored.equals(dto.getPassword());
            if (matched) {
                userInfoService.updatePassword(user.getUserId(), BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
            }
        }
        if (!matched) {
            throw new BizException(ResponseCode.BAD_CREDENTIALS);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getUserName());
        String token = jwtUtil.generate(user.getUserId(), claims);

        return R.data(LoginVO.builder()
                .token(token)
                .userId(user.getUserId())
                .userName(user.getUserName())
                .nickName(user.getUserName())
                .avatar("")
                .build());
    }

    @Operation(summary = "当前登录用户信息")
    @RequireAuth
    @GetMapping("/info")
    public R<UserInfo> info() {
        String userId = AuthContext.userId();
        UserInfo user = userInfoService.findByUserId(userId);
        if (user == null) {
            throw new BizException(ResponseCode.USER_NOT_FOUND);
        }
        // 密码不回传
        user.setPassword(null);
        return R.data(user);
    }

    @Operation(summary = "退出登录")
    @RequireAuth
    @PostMapping("/logout")
    public R<Void> logout() {
        // 纯 JWT 无需服务端失效,如需黑名单可在 Redis 存 token
        return R.success();
    }
}
