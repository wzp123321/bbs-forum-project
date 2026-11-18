package com.bbs.bbsadmin.search;

import com.bbs.bbsadmin.entity.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 帖子检索（可选）。
 * 只在 SEARCH_ENGINE=elasticsearch 时注册；ES 不可用或索引为空时返回 null，调用方回退 SQL LIKE。
 */
@Component
@ConditionalOnProperty(name = "bbs.search.engine", havingValue = "elasticsearch")
public class EsPostSearchClient {

    private static final Logger log = LoggerFactory.getLogger(EsPostSearchClient.class);

    private final RestClient client;
    private final String index;
    private final ObjectMapper mapper = new ObjectMapper();

    public EsPostSearchClient(@Value("${bbs.search.es.url:http://localhost:9200}") String url,
                              @Value("${bbs.search.es.index:bbs-post}") String index) {
        this.client = RestClient.builder(HttpHost.create(url)).build();
        this.index = index;
    }

    /** 供单测使用：构造请求体，不依赖网络。 */
    static String buildSearchBody(String keyword, int from, int size) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", from);
        body.put("size", size);
        body.put("_source", List.of("id"));
        body.put("query", Map.of("multi_match", Map.of(
                "query", keyword,
                "fields", List.of("title^3", "content"))));
        try {
            return new ObjectMapper().writeValueAsString(body);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return 命中的 postId；ES 不可用/无索引时返回 null，触发 SQL 回退
     */
    public List<Long> searchIds(String keyword, int from, int size) {
        if (keyword == null || keyword.isBlank()) return null;
        try {
            Request request = new Request("POST", "/" + index + "/_search");
            request.setJsonEntity(buildSearchBody(keyword, from, size));
            Response response = client.performRequest(request);
            JsonNode root = mapper.readTree(EntityUtils.toString(response.getEntity()));
            List<Long> ids = new ArrayList<>();
            for (JsonNode hit : root.path("hits").path("hits")) {
                JsonNode id = hit.path("_source").path("id");
                if (id.isNumber()) ids.add(id.asLong());
                else if (id.isTextual()) ids.add(Long.parseLong(id.asText()));
            }
            return ids;
        } catch (Exception e) {
            log.warn("ES search failed, falling back to SQL LIKE: {}", e.getMessage());
            return null;
        }
    }

    /** 索引单篇帖子；失败只记日志，不影响主流程。 */
    public void indexPost(Post post) {
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", post.getId());
            doc.put("title", post.getTitle());
            doc.put("content", stripHtml(post.getContent()));
            doc.put("categoryId", post.getCategoryId());
            doc.put("userId", post.getUserId());
            doc.put("createTime", post.getCreateTime() == null ? null : post.getCreateTime().toString());
            Request request = new Request("PUT", "/" + index + "/_doc/" + post.getId());
            request.setJsonEntity(mapper.writeValueAsString(doc));
            client.performRequest(request);
        } catch (Exception e) {
            log.warn("ES index failed for post {}: {}", post.getId(), e.getMessage());
        }
    }

    private static String stripHtml(String html) {
        return html == null ? "" : html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
    }

    @PreDestroy
    public void close() {
        try {
            client.close();
        } catch (Exception ignored) {
            // ignore
        }
    }
}
