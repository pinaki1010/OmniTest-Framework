package com.omnitest.platform.contract.pact;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

@Feature("contract")
@Story("user-service")
public class UserContractTest {

    @Test(groups = {"regression", "contract"})
    public void getUserByIdContract() {
        PactContractHelper.runGetUserByIdContract();
    }
}
