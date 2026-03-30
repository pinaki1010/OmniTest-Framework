package com.omnitest.platform.tests;

import com.omnitest.platform.core.BaseTest;
import com.omnitest.platform.db.QueryExecutor;
import com.omnitest.platform.reporting.AllureManager;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Map;

@Feature("database")
@Story("users-table")
public class DbValidationTest extends BaseTest {

    @Test(groups = {"regression"})
    public void insertAndVerifyUser() throws SQLException {
        QueryExecutor q = new QueryExecutor();
        long id = 2002L;
        q.executeUpdate("DELETE FROM users WHERE id = ?", id);
        q.executeUpdate(
                "INSERT INTO users (id, name, email, role) VALUES (?,?,?,?)",
                id, "DB Test", "db.test@example.com", "user");

        Map<String, String> row = q.queryForMap("SELECT id, name, email, role FROM users WHERE id = ?", id);
        AllureManager.attachText("db-row", row.toString());
        Assert.assertEquals(row.get("email"), "db.test@example.com");
        Assert.assertEquals(row.get("name"), "DB Test");
    }
}
