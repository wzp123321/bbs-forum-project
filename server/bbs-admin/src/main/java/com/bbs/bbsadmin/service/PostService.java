package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.dto.DraftSaveDTO;
import com.bbs.bbsadmin.entity.dto.PostPageQuery;
import com.bbs.bbsadmin.entity.dto.PostSaveDTO;
import com.bbs.bbsadmin.entity.vo.PostVO;

public interface PostService extends IService<Post> {

    /**
     * 分页查询(返回 VO 列表,含用户/板块/标签信息)
     */
    IPage<PostVO> pageQueryVO(PostPageQuery query);

    /**
     * 详情(含标签)
     */
    PostVO detail(Long id);

    /**
     * 新增
     */
    Long create(PostSaveDTO dto);

    /**
     * 编辑
     */
    boolean update(Long id, PostSaveDTO dto);

    /**
     * 切换置顶
     */
    boolean toggleTop(Long id, Integer isTop);

    /**
     * 切换精华
     */
    boolean toggleEssence(Long id, Integer isEssence);

    /**
     * 修改状态
     */
    boolean changeStatus(Long id, Integer status);

    /**
     * 公开给其他 Service 复用的关联信息填充方法
     */
    void fillAssociationsPublic(java.util.List<PostVO> vos);

    /**
     * 关注动态流: 当前用户关注的人发布的正常帖子, 按发布时间倒序
     */
    IPage<PostVO> followFeed(int pageNum, int pageSize);

    /**
     * 保存草稿(新建或更新), 仅作者本人可操作
     *
     * @return 草稿 ID
     */
    Long saveDraft(DraftSaveDTO dto);

    /**
     * 我的草稿分页, 按更新时间倒序
     */
    IPage<PostVO> myDrafts(int pageNum, int pageSize);

    /**
     * 草稿详情, 仅作者本人可读
     */
    PostVO draftDetail(Long id);

    /**
     * 发布草稿
     *
     * @return 发布后的状态: 1 正常 / 2 审核中
     */
    Integer publishDraft(Long id);
}