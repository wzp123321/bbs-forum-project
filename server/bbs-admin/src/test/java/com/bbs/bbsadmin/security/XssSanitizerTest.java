package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class XssSanitizerTest {

    private XssSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new XssSanitizer();
        ReflectionTestUtils.setField(sanitizer, "sensitiveWordFilter", new SensitiveWordFilter());
    }

    @Test
    void clean_shouldRemoveScriptAndEventAttributes() {
        String html = "<p onclick=\"alert(1)\">hello</p><script>alert(2)</script>";
        String clean = sanitizer.clean(html);
        assertFalse(clean.contains("<script"), clean);
        assertFalse(clean.contains("onclick"), clean);
        assertTrue(clean.contains("hello"), clean);
    }

    @Test
    void clean_shouldStripDangerousHref() {
        String clean = sanitizer.clean("<a href=\"javascript:alert(1)\" title=\"x\">link</a>");
        assertFalse(clean.toLowerCase().contains("javascript:"), clean);
        assertTrue(clean.contains("title=\"x\""), clean);
    }

    @Test
    void cleanAndCheck_shouldThrowOnSensitiveWord() {
        assertThrows(BizException.class,
                () -> sanitizer.cleanAndCheck("<p>包含敏感词的内容</p>", true));
    }

    @Test
    void cleanAndCheck_shouldReturnSafeHtmlWhenNoHit() {
        String result = sanitizer.cleanAndCheck("<p>正常内容</p>", true);
        assertNotNull(result);
        assertTrue(result.contains("正常内容"));
    }
}
