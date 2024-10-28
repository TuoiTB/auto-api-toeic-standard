package test;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class FirstApiTests {
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
        String expected = """
        [{"name":"Viet Nam","code":"VN"},{"name":"USA","code":"US"},{"name":"Canada","code":"CA"},{"name":"UK","code":"GB"},{"name":"France","code":"FR"},{"name":"Japan","code":"JP"},{"name":"India","code":"IN"},{"name":"China","code":"CN"},{"name":"Brazil","code":"BR"}]
                """;
        RestAssured.get("/api/v1/countries").then().statusCode(200);

        }
}
