package com.bbs.bbsadmin.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class RequestIdFilterTest {

    @AfterEach
    void clear() {
        MDC.clear();
    }

    @Test
    void shouldPassThroughProvidedRequestId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestIdFilter.HEADER, "client-request-1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(invocation -> {
            assertEquals("client-request-1", MDC.get(RequestIdFilter.MDC_KEY));
            return null;
        }).when(chain).doFilter(request, response);

        new RequestIdFilter().doFilter(request, response, chain);

        assertEquals("client-request-1", response.getHeader(RequestIdFilter.HEADER));
        assertNull(MDC.get(RequestIdFilter.MDC_KEY));
    }

    @Test
    void shouldGenerateIdAndCleanMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestIdFilter filter = new RequestIdFilter();

        filter.doFilter(request, response, (req, res) -> {
            assertNotNull(MDC.get(RequestIdFilter.MDC_KEY));
        });

        assertNotNull(response.getHeader(RequestIdFilter.HEADER));
        assertNull(MDC.get(RequestIdFilter.MDC_KEY));
    }
}
