package api.test;

import api.data.GetCountriesData;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;

public class GetCountriesApiTest {
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }
    @Test
    void verifyGetCountriesApiResponseSchema(){
        String responseBody = RestAssured.get("/api/v1/countries").getBody().asString();
        System.out.println(responseBody);
        RestAssured.get("/api/v1/countries").then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-json-schema.json"));
    }
    @Test
    void verifyGetCountriesApiReturnCorrectData(){
        String expected = GetCountriesData.ALL_COUNTRIES;
        Response actualResponse = RestAssured.get("/api/v1/countries"); //then().statusCode(200);
        String actualResponseBody = actualResponse.asString();
        /*System.out.println(actualResponseBody);*/
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
        }

    @Test
    void verifyGetCountriesV2ApiResponseSchema(){
        String responseBody = RestAssured.get("/api/v2/countries").getBody().asString();
        System.out.println(responseBody);
        RestAssured.get("/api/v2/countries").then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-v2-json-schema.json"));
    }
    @Test
    void verifyGetCountriesV2ApiReturnCorrectData(){
        String expected = GetCountriesData.ALL_COUNTRIES_WITH_GDP;
        Response actualResponse = RestAssured.get("/api/v2/countries");
        String actualResponseBody = actualResponse.asString();
        /*System.out.println(actualResponseBody);*/
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
    }
}
