package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.search.EsPostSearchClient;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** ES 索引维护；仅在 SEARCH_ENGINE=elasticsearch 时可用。 */
@RestController
@RequestMapping("admin/search")
public class SearchAdminController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ObjectProvider<EsPostSearchClient> esPostSearchClient;

    @Operation(summary = "重建帖子搜索索引（管理员）")
    @RequireAuth(adminOnly = true)
    @PostMapping("/reindex")
    public R<Map<String, Object>> reindex() {
        EsPostSearchClient client = esPostSearchClient.getIfAvailable();
        if (client == null) {
            return R.fail(503, "搜索索引未启用，请设置 SEARCH_ENGINE=elasticsearch");
        }
        List<Post> posts = postMapper.selectList(null);
        int indexed = 0;
        for (Post post : posts) {
            client.indexPost(post);
            indexed++;
        }
        return R.data(Map.of("indexed", indexed));
    }
}
