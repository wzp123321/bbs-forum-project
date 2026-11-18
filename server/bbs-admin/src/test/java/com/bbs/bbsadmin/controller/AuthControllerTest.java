package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.LoginDTO;
import com.bbs.bbsadmin.entity.vo.LoginVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.JwtUtil;
import com.bbs.bbsadmin.security.LoginLock;
import com.bbs.bbsadmin.security.PasswordResetStore;
import com.bbs.bbsadmin.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController controller;
    private UserInfoService userInfoService;
    private JwtUtil jwtUtil;
    private LoginLock loginLock;
    private PasswordResetStore passwordResetStore;

    @BeforeEach
    void setUp() {
        controller = new AuthController();
        userInfoService = mock(UserInfoService.class);
        jwtUtil = mock(JwtUtil.class);
        loginLock = mock(LoginLock.class);
        passwordResetStore = mock(PasswordResetStore.class);
        ReflectionTestUtils.setField(controller, "userInfoService", userInfoService);
        ReflectionTestUtils.setField(controller, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(controller, "loginLock", loginLock);
        ReflectionTestUtils.setField(controller, "passwordResetStore", passwordResetStore);
    }

    private LoginDTO loginDto(String account, String password) {
        LoginDTO dto = new LoginDTO();
        dto.setAccount(account);
        dto.setPassword(password);
        return dto;
    }

    private UserInfo user(String hash) {
        UserInfo user = new UserInfo();
        user.setUserId("u-1");
        user.setUserName("tester");
        user.setPassword(hash);
        return user;
    }

    @Test
    void login_withBcryptPassword_shouldReturnTokenAndClearLock() {
        when(userInfoService.findByAccount("tester")).thenReturn(user(BCrypt.hashpw("123456", BCrypt.gensalt())));
        when(jwtUtil.generate(eq("u-1"), anyMap())).thenReturn("signed-token");

        R<LoginVO> result = controller.login(loginDto("tester", "123456"));

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("signed-token", result.getData().getToken());
        verify(loginLock).clear("tester");
        verify(userInfoService, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void login_withWrongPassword_shouldCountFailureAndThrow() {
        when(userInfoService.findByAccount("tester")).thenReturn(user(BCrypt.hashpw("correct", BCrypt.gensalt())));

        assertThrows(BizException.class, () -> controller.login(loginDto("tester", "wrong")));

        verify(loginLock).onFailure("tester");
        verify(jwtUtil, never()).generate(anyString(), anyMap());
    }

    @Test
    void login_withUnknownAccount_shouldNotLeakExistence() {
        when(userInfoService.findByAccount("nobody")).thenReturn(null);

        BizException ex = assertThrows(BizException.class,
                () -> controller.login(loginDto("nobody", "123456")));
        assertEquals(1002, ex.getCode());
        verify(loginLock).onFailure("nobody");
    }
}
