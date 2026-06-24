package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.OperationLog;

/**
 * 操作审计日志
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 异步写入审计日志 (失败不抛异常, 不影响主业务)
     */
    void asyncLog(OperationLog log);
}
