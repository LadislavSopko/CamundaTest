package com.example.workflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    public CorsFilter() {
        super();
    }

    @Value("${cors.allow.origin:#{'*'}}")
    private String allowOrigin;

    @Value("${cors.allow.credentials:#{false}}")
    private boolean allowCredentials;

    @Override
    public final void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse resp = (HttpServletResponse) res;

        setCorsHeaders(resp);

        final HttpServletRequest request = (HttpServletRequest) req;
        if(HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            // do not continue with filter chain for options requests
        }
        else {
            chain.doFilter(req, res);
        }
    }

    public void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", allowOrigin);
        if(allowCredentials) {// BUG 946 - posielat len ak je true
            resp.setHeader("Access-Control-Allow-Credentials", Boolean.toString(allowCredentials));
        }

        resp.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Authorization, Origin, Credentials, Content-Type, Version");
        resp.setHeader("Access-Control-Expose-Headers", "X-Requested-With, Authorization, Origin, Credentials, Content-Type");
    }
}

