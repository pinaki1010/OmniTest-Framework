package com.omnitest.platform.tests;

import com.omnitest.platform.api.clients.UserApiClient;
import com.omnitest.platform.api.models.User;
import com.omnitest.platform.core.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Feature("api")
@Story("user-api")
public class ApiTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    public void getUserByIdMatchesSchema() {
        UserApiClient client = new UserApiClient();
        client.getUserByIdRaw(1001L)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/user.json"));
    }

    @Test(groups = {"regression"})
    public void getUserByIdReturnsTypedModel() {
        UserApiClient client = new UserApiClient();
        User user = client.getUserById(1001L);
        Assert.assertEquals(user.getId(), Long.valueOf(1001L));
        Assert.assertNotNull(user.getEmail());
    }
}
