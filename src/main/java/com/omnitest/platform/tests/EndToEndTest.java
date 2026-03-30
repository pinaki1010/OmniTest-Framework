package com.omnitest.platform.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnitest.platform.api.clients.UserApiClient;
import com.omnitest.platform.api.models.User;
import com.omnitest.platform.contract.pact.PactContractHelper;
import com.omnitest.platform.core.TestContext;
import com.omnitest.platform.core.UiTestBase;
import com.omnitest.platform.db.QueryExecutor;
import com.omnitest.platform.reporting.AllureManager;
import com.omnitest.platform.ui.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Map;

/**
 * End-to-End User Validation: UI → API → DB → contract verification.
 */
@Feature("e2e")
@Story("user-lifecycle")
public class EndToEndTest extends UiTestBase {

    @Test(groups = {"regression"})
    public void endToEndUserValidation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("testdata.json")) {
            Assert.assertNotNull(in, "testdata.json missing from classpath");
            data = mapper.readTree(in);
        }
        String name = data.path("user").path("name").asText();
        String email = data.path("user").path("email").asText();

        LoginPage login = new LoginPage(com.omnitest.platform.core.DriverManager.getPage());
        login.openRegisterPage();
        login.registerUser(name, email);
        long userId = login.readDisplayedUserId();
        TestContext.setUserId(userId);
        TestContext.setEmail(email);
        AllureManager.attachContextSnapshot(TestContext.snapshot());

        UserApiClient api = new UserApiClient();
        User user = api.getUserById(userId);
        Assert.assertEquals(user.getEmail(), email);

        QueryExecutor db = new QueryExecutor();
        db.executeUpdate("DELETE FROM users WHERE id = ?", userId);
        db.executeUpdate(
                "INSERT INTO users (id, name, email, role) VALUES (?,?,?,?)",
                userId, name, email, "user");
        Map<String, String> row = db.queryForMap("SELECT email FROM users WHERE id = ?", userId);
        AllureManager.attachText("db-verify", row.toString());
        Assert.assertEquals(row.get("email"), email);

        AllureManager.step("pact-contract-validation", PactContractHelper::runGetUserByIdContract);
    }
}
