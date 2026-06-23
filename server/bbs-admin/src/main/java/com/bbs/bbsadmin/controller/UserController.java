package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.PasswordUpdateDTO;
import com.bbs.bbsadmin.entity.dto.RegisterDTO;
import com.bbs.bbsadmin.entity.dto.UserUpdateDTO;
import com.bbs.bbsadmin.entity.vo.UserVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "UserController", description = "用户管理")
@RestController
@RequestMapping("admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "注册 (公开)")
    @PostMapping("/register")
    public R<String> register(@Valid @RequestBody RegisterDTO dto) {
        String userId = userInfoService.register(dto);
        return R.data(userId);
    }

    @Operation(summary = "分页查询用户")
    @RequireAuth
    @GetMapping("/page")
    public R<Map<String, Object>> page(PageQuery query) {
        IPage<UserInfo> page = userInfoService.pageQuery(query);
        List<UserVO> rows = page.getRecords().stream().map(UserVO::from).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("list", rows);
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "用户详情")
    @RequireAuth
    @GetMapping("/{userId}")
    public R<UserVO> detail(@PathVariable String userId) {
        UserInfo user = userInfoService.findByUserId(userId);
        if (user == null) {
            throw new BizException(ResponseCode.USER_NOT_FOUND);
        }
        return R.data(UserVO.from(user));
    }

    @Operation(summary = "修改资料")
    @RequireAuth
    @PutMapping("/{userId}")
    public R<Void> updateProfile(@PathVariable String userId, @RequestBody UserUpdateDTO dto) {
        // 只允许改自己 或 超级管理员(此处仅判断自己)
        String current = AuthContext.userId();
        if (current == null || !current.equals(userId)) {
            throw new BizException(ResponseCode.FORBIDDEN, "只能修改自己的资料");
        }
        userInfoService.updateProfile(userId, dto);
        return R.success();
    }

    @Operation(summary = "修改密码")
    @RequireAuth
    @PutMapping("/{userId}/password")
    public R<Void> updatePassword(@PathVariable String userId, @Valid @RequestBody PasswordUpdateDTO dto) {
        String current = AuthContext.userId();
        if (current == null || !current.equals(userId)) {
            throw new BizException(ResponseCode.FORBIDDEN, "只能修改自己的密码");
        }
        UserInfo user = userInfoService.findByUserId(userId);
        if (user == null) {
            throw new BizException(ResponseCode.USER_NOT_FOUND);
        }
        // 校验旧密码
        String stored = user.getPassword();
        boolean matched = stored != null && stored.startsWith("$2a$")
                ? BCrypt.checkpw(dto.getOldPassword(), stored)
                : stored != null && stored.equals(dto.getOldPassword());
        if (!matched) {
            throw new BizException(ResponseCode.BAD_CREDENTIALS, "原密码错误");
        }
        userInfoService.updatePassword(userId, BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        return R.success();
    }

    @Operation(summary = "删除用户")
    @RequireAuth
    @DeleteMapping("/{userId}")
    public R<Void> delete(@PathVariable String userId) {
        userInfoService.removeById(userId);
        return R.success();
    }
}
