package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Report;
import com.bbs.bbsadmin.entity.dto.ReportHandleDTO;
import com.bbs.bbsadmin.entity.dto.ReportPageQuery;
import com.bbs.bbsadmin.entity.dto.ReportSaveDTO;
import com.bbs.bbsadmin.entity.vo.ReportVO;

public interface ReportService extends IService<Report> {

    /** 分页查询(返回 VO) */
    IPage<ReportVO> pageQueryVO(ReportPageQuery query);

    /** 详情 */
    ReportVO detail(Long id);

    /** 用户提交举报 */
    Long create(ReportSaveDTO dto);

    /** 管理员处理 (0驳回 1已处理) */
    boolean handle(Long id, ReportHandleDTO dto);

    /** 删除 */
    boolean delete(Long id);
}
