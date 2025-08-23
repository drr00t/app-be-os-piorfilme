package com.github.drr00t.gra;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class AwardsResourceTest {
    @Test
    void testAwardsGetProducersJustMinAndMaxEndpoint() {
        given()
          .when().get("/awards/producers/intervals/max-and-min")
          .then()
             .statusCode(200)
             .body(is("{\"min\":[" +
                 "{\"producer\":\"Producer 1\",\"interval\":1,\"previousWin\":2008,\"followingWin\":2009}," +
                 "{\"producer\":\"Producer 2\",\"interval\":1,\"previousWin\":2018,\"followingWin\":2019}" +
                 "],\"max\":[" +
                 "{\"producer\":\"Producer 1\",\"interval\":99,\"previousWin\":1900,\"followingWin\":1999}," +
                 "{\"producer\":\"Producer 2\",\"interval\":99,\"previousWin\":2000,\"followingWin\":2099}" +
                 "]}"));
    }

}