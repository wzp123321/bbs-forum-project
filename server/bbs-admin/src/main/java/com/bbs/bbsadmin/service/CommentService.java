package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Comment;
import com.bbs.bbsadmin.entity.dto.CommentPageQuery;
import com.bbs.bbsadmin.entity.dto.CommentSaveDTO;
import com.bbs.bbsadmin.entity.vo.CommentVO;

public interface CommentService extends IService<Comment> {

    /**
     * 分页查询(返回 VO)
     */
    IPage<CommentVO> pageQueryVO(CommentPageQuery query);

    /**
     * 详情
     */
    CommentVO detail(Long id);

    /**
     * 新增 (同步更新帖子 comment_count)
     */
    Long create(CommentSaveDTO dto);

    /**
     * 编辑
     */
    boolean update(Long id, CommentSaveDTO dto);

    /**
     * 删除 (同步更新帖子 comment_count)
     */
    boolean delete(Long id);

    /**
     * 修改状态
     */
    boolean changeStatus(Long id, Integer status);
}
