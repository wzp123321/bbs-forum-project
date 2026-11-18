package com.bbs.bbsadmin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/** 需要本地 MySQL 的上下文冒烟测试；CI 默认跳过，本地设置 RUN_DB_TESTS=true 后运行。 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "true")
class BbsAdminApplicationTests {

    @Test
    void contextLoads() {
    }

}
