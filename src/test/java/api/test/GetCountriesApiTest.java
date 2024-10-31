package api.test;

import api.data.GetCountriesData;
import api.model.country.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.Matchers.equalTo;

public class GetCountriesApiTest {
    private static final String GET_COUNTRIES_PATH = "/api/v1/countries";
    private static final String GET_COUNTRIES_PATH_V2 = "/api/v2/countries";
    private static final String GET_COUNTRIES_BY_CODE_PATH = "/api/v1/countries/{code}";
    //private static final String GET_COUNTRIES_PATH_BY_CODE = "/api/v1/countries/{code}";
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
        Response actualResponse = RestAssured.get(GET_COUNTRIES_PATH);
        String actualResponseBody = actualResponse.asString();
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
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
    }
    static Stream<Country> countriesProvider() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = objectMapper.readValue(GetCountriesData.ALL_COUNTRIES, new TypeReference<List<Country>>(){});
        return countries.stream();
    }
    @Test
    void verifyGetCountriesByCodeApiResponseSchema(){
        String expected = GetCountriesData.COUNTRY_BY_CODE;
        Map<String, String> params = new HashMap<>();
        params.put("code", "VN");
        Response actualResponse = RestAssured.given().log().all().get(GET_COUNTRIES_BY_CODE_PATH, params);
        assertThat(200, equalTo(actualResponse.statusCode()));
        String actualResponseBody = actualResponse.asString();
        assertThat(actualResponseBody, jsonEquals(expected));
    }
    @ParameterizedTest
    @MethodSource("countriesProvider")
    void verifyGetCountriesByCodeApiReturnCorrectData(Country country){
        Map<String, String> params = new HashMap<>();
        params.put("code", country.getCode());
        Response actualResponse = RestAssured.given().log().all().get(GET_COUNTRIES_BY_CODE_PATH, params);
        assertThat(200, equalTo(actualResponse.statusCode()));
        String actualResponseBody = actualResponse.asString();
        assertThat(actualResponseBody, jsonEquals(country).when(IGNORING_ARRAY_ORDER));
    }

}
