package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子 VO
 */
@Data
public class PostVO {

    private Long id;
    private String userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
    private Integer contentType;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer status;
    private Integer isTop;
    private Integer isEssence;
    private LocalDateTime topTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> tagIds;
    private List<String> tagNames;

    public static PostVO from(Post p) {
        if (p == null) return null;
        PostVO v = new PostVO();
        BeanUtils.copyProperties(p, v);
        return v;
    }
}
