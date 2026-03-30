package com.omnitest.platform.api.clients;

import com.omnitest.platform.api.models.User;
import com.omnitest.platform.utils.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * REST Assured abstraction for user APIs with Allure and logging filters.
 */
public class UserApiClient {

    private final RequestSpecification spec;

    public UserApiClient() {
        String base = ConfigManager.getInstance().getApiBaseUri();
        this.spec = given()
                .baseUri(base)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured())
                .filter(new ApiLoggingFilter());
    }

    public User getUserById(long id) {
        return spec.get("/users/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
    }

    public ValidatableResponse getUserByIdRaw(long id) {
        return spec.get("/users/" + id).then();
    }

    public User createUser(User user) {
        return spec.body(user)
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .as(User.class);
    }

    public static void resetRestAssured() {
        RestAssured.reset();
    }
}
