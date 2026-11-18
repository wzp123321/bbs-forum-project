package com.bbs.bbsadmin.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EsPostSearchClientTest {

    @Test
    void buildSearchBody_shouldUseMultiMatchAndLimitSize() throws Exception {
        String json = EsPostSearchClient.buildSearchBody("Spring Boot", 20, 50);
        JsonNode root = new ObjectMapper().readTree(json);
        assertEquals(20, root.path("from").asInt());
        assertEquals(50, root.path("size").asInt());
        assertEquals("Spring Boot", root.path("query").path("multi_match").path("query").asText());
        assertEquals("title^3", root.path("query").path("multi_match").path("fields").get(0).asText());
    }
}
