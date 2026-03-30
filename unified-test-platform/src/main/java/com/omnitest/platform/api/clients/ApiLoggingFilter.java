package com.omnitest.platform.api.clients;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Routes REST Assured traffic through Log4j2. */
public class ApiLoggingFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger("com.omnitest.platform.api");

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        LOG.info("{} {} body={}", requestSpec.getMethod(), requestSpec.getURI(),
                requestSpec.getBody() != null ? requestSpec.getBody().toString() : "");
        Response response = ctx.next(requestSpec, responseSpec);
        LOG.info("Response status={} body={}", response.getStatusCode(),
                response.getBody() != null ? response.getBody().asString() : "");
        return response;
    }
}
