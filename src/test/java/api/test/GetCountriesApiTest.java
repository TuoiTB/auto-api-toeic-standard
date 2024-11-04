package api.test;

import api.data.GetCountriesData;
import api.model.country.Country;
import api.model.country.CountryVersionTwo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.Matchers.*;

public class GetCountriesApiTest {
    private static final String GET_COUNTRIES_PATH = "/api/v1/countries";
    private static final String GET_COUNTRIES_PATH_V2 = "/api/v2/countries";
    private static final String GET_COUNTRIES_BY_CODE_PATH = "/api/v1/countries/{code}";
    private static final String GET_COUNTRIES_BY_FILTER = "/api/v3/countries";
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
        assertThat((String.format("Actual: %s\n Expected: %s\n", actualResponseBody, country)),actualResponseBody, jsonEquals(country).when(IGNORING_ARRAY_ORDER));
    }
    @Test
    void verifyGetCountryApiReturnDataWithLessThanOperator(){
        String path = String.format("%s?gdp=5000&operator=<", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), lessThan(5000f));
        });
        System.out.println(actualResponse.asString());
    }
    @Test
    void verifyGetCountryApiReturnDataWithGreaterThanOperator(){
        String path = String.format("%s?gdp=5000&operator=>", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), greaterThan(5000f));
        });
        System.out.println(actualResponse.asString());
    }
    @Test
    void verifyGetCountryApiReturnDataWithEqualOperator_1(){
        //String path = String.format("%s?gdp=5000&operator===", GET_COUNTRIES_BY_FILTER);

        Response actualResponse = RestAssured.given().log().all().queryParam("gdp",5000).queryParam("operator", "==").get(GET_COUNTRIES_BY_FILTER);
        assertThat(200, equalTo(actualResponse.statusCode()));
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), equalTo(5000f));
        });
    }
    @Test
    void verifyGetCountryApiReturnDataWithEqualOperator_2(){
        String operator = URLEncoder.encode("==");
        String path = String.format("%s?gdp=5000&operator=%s", GET_COUNTRIES_BY_FILTER, operator);

        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), equalTo(5000f));
        });
    }
    @Test
    void verifyGetCountryApiReturnDataWithLessThanOrEqualOperator(){
        String path = String.format("%s?gdp=5000&operator=<=", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), lessThanOrEqualTo(5000f));
        });
        System.out.println(actualResponse.asString());
    }
    @Test
    void verifyGetCountryApiReturnDataWithGreaterThanOrEqualOperator(){
        String path = String.format("%s?gdp=5000&operator=>=", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), greaterThanOrEqualTo(5000f));
        });
        System.out.println(actualResponse.asString());
    }
    @Test
    void verifyGetCountryApiReturnDataWithNotEqualOperator(){
        String path = String.format("%s?gdp=5000&operator=!=", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country ->{
            assertThat(country.getGdp(), not(equalTo(5000f)));
        });
    }
}
