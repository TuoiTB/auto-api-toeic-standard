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
    private static final String GET_COUNTRIES_PATH = "/api/v1/countries";
    private static final String GET_COUNTRIES_PATH_V2 = "/api/v2/countries";
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }
    @Test
    void verifyGetCountriesApiResponseSchema(){
        String responseBody = RestAssured.get(GET_COUNTRIES_PATH).getBody().asString();
        System.out.println(responseBody);
        RestAssured.get(GET_COUNTRIES_PATH).then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-json-schema.json"));
    }
    @Test
    void verifyGetCountriesApiReturnCorrectData(){
        String expected = GetCountriesData.ALL_COUNTRIES;
        Response actualResponse = RestAssured.get(GET_COUNTRIES_PATH); //then().statusCode(200);
        String actualResponseBody = actualResponse.asString();
        /*System.out.println(actualResponseBody);*/
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
        }

    @Test
    void verifyGetCountriesV2ApiResponseSchema(){
        String responseBody = RestAssured.get(GET_COUNTRIES_PATH_V2).getBody().asString();
        System.out.println(responseBody);
        RestAssured.get(GET_COUNTRIES_PATH_V2).then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-v2-json-schema.json"));
    }
    @Test
    void verifyGetCountriesV2ApiReturnCorrectData(){
        String expected = GetCountriesData.ALL_COUNTRIES_WITH_GDP;
        Response actualResponse = RestAssured.get(GET_COUNTRIES_PATH_V2);
        String actualResponseBody = actualResponse.asString();
        /*System.out.println(actualResponseBody);*/
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
    }
}
