package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.OperationLog;
import com.bbs.bbsadmin.mapper.OperationLogMapper;
import com.bbs.bbsadmin.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    /**
     * 异步写入审计日志
     * - 失败仅打印日志, 不影响主业务
     */
    @Async
    public void asyncLog(OperationLog log) {
        try {
            if (log.getCreateTime() == null) {
                log.setCreateTime(java.time.LocalDateTime.now());
            }
            if (log.getSuccess() == null) {
                log.setSuccess(1);
            }
            save(log);
        } catch (Exception e) {
            // 审计失败不能影响业务
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[AuditLog] 写入审计日志失败: {}", e.getMessage());
        }
    }
}
