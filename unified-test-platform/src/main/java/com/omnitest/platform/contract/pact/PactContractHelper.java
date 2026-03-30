package com.omnitest.platform.contract.pact;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.ConsumerPactRunnerKt;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.PactTestExecutionContext;
import au.com.dius.pact.consumer.PactTestRun;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.model.MockProviderConfig;
import au.com.dius.pact.core.model.RequestResponsePact;
import com.omnitest.platform.reporting.AllureManager;
import com.omnitest.platform.utils.ConfigManager;
import io.restassured.RestAssured;
import kotlin.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.hamcrest.Matchers.equalTo;

/**
 * Pact consumer verification using {@link ConsumerPactRunnerKt#runConsumerTest} and embedded {@link MockServer}.
 * Writes pact JSON under {@code target/pacts} (or {@code pact.rootDir}).
 */
public final class PactContractHelper {

    private static final Logger LOG = LogManager.getLogger(PactContractHelper.class);

    private PactContractHelper() {
    }

    public static void runGetUserByIdContract() {
        ConfigManager cfg = ConfigManager.getInstance();
        RequestResponsePact pact = ConsumerPactBuilder.consumer(cfg.getPactConsumerName())
                .hasPactWith(cfg.getPactProviderName())
                .uponReceiving("GET user by id")
                .path("/api/users/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body("{\"id\":1,\"name\":\"Contract User\",\"email\":\"contract@example.com\",\"role\":\"user\"}")
                .toPact();

        PactVerificationResult result = ConsumerPactRunnerKt.runConsumerTest(
                pact,
                MockProviderConfig.createDefault(),
                new PactTestRun() {
                    @Override
                    public Object run(MockServer mockServer, PactTestExecutionContext context) {
                        RestAssured.given()
                                .baseUri(mockServer.getUrl())
                                .when()
                                .get("/api/users/1")
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(1))
                                .body("email", equalTo("contract@example.com"));
                        AllureManager.attachText("pact-consumer", "Verified GET /api/users/1 against MockServer");
                        LOG.info("Pact consumer verification succeeded for GET /api/users/1");
                        return Unit.INSTANCE;
                    }
                });

        if (result instanceof PactVerificationResult.Error) {
            Throwable t = ((PactVerificationResult.Error) result).getError();
            throw new AssertionError("Pact verification failed", t);
        }
    }
}
