package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Post;
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
}
