package api.test;

import api.common.RestAssuredSetUp;
import api.data.GetCountriesData;
import api.model.country.*;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static api.data.GetCountriesData.COUNTRY_WITH_PRIVATE_KEY;
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
    private static final String GET_COUNTRIES_PAGINATION = "/api/v4/countries";
    private static final String GET_COUNTRIES_PRIVATE_KEY = "/api/v5/countries";

    @BeforeAll
    static void setUp() {
        RestAssuredSetUp.setUp();
    }

    @Test
    void verifyGetCountriesApiResponseSchema() {
        String responseBody = RestAssured.get(GET_COUNTRIES_PATH).getBody().asString();
        System.out.println(responseBody);
        RestAssured.get(GET_COUNTRIES_PATH).then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-json-schema.json"));
    }

    @Test
    void verifyGetCountriesApiReturnCorrectData() {
        String expected = GetCountriesData.ALL_COUNTRIES;
        Response actualResponse = RestAssured.get(GET_COUNTRIES_PATH);
        String actualResponseBody = actualResponse.asString();
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
    }

    @Test
    void verifyGetCountriesV2ApiResponseSchema() {
        String responseBody = RestAssured.get(GET_COUNTRIES_PATH_V2).getBody().asString();
        System.out.println(responseBody);
        RestAssured.get(GET_COUNTRIES_PATH_V2).then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-v2-json-schema.json"));
    }

    @Test
    void verifyGetCountriesV2ApiReturnCorrectData() {
        String expected = GetCountriesData.ALL_COUNTRIES_WITH_GDP;
        Response actualResponse = RestAssured.get(GET_COUNTRIES_PATH_V2);
        String actualResponseBody = actualResponse.asString();
        assertThat(actualResponseBody, jsonEquals(expected).when(IGNORING_ARRAY_ORDER));
    }

    static Stream<Country> countriesProvider() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = objectMapper.readValue(GetCountriesData.ALL_COUNTRIES, new TypeReference<List<Country>>() {
        });
        return countries.stream();
    }

    @Test
    void verifyGetCountriesByCodeApiResponseSchema() {
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
    void verifyGetCountriesByCodeApiReturnCorrectData(Country country) {
        Map<String, String> params = new HashMap<>();
        params.put("code", country.getCode());
        Response actualResponse = RestAssured.given().log().all().get(GET_COUNTRIES_BY_CODE_PATH, params);
        assertThat(200, equalTo(actualResponse.statusCode()));
        String actualResponseBody = actualResponse.asString();
        //CountryCustomizedModel actualResponseBody = actualResponse.as(CountryCustomizedModel.class);
        assertThat((String.format("Actual: %s\n Expected: %s\n", actualResponseBody, country)), actualResponseBody, jsonEquals(country).when(IGNORING_ARRAY_ORDER));
    }

    @Test
    void verifyGetCountryApiReturnDataWithLessThanOperator() {
        String path = String.format("%s?gdp=5000&operator=<", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), lessThan(5000f));
        });
        System.out.println(actualResponse.asString());
    }

    @Test
    void verifyGetCountryApiReturnDataWithGreaterThanOperator() {
        String path = String.format("%s?gdp=5000&operator=>", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), greaterThan(5000f));
        });
        System.out.println(actualResponse.asString());
    }

    @Test
    void verifyGetCountryApiReturnDataWithEqualOperator_1() {
        //String path = String.format("%s?gdp=5000&operator===", GET_COUNTRIES_BY_FILTER);

        Response actualResponse = RestAssured.given().log().all().queryParam("gdp", 5000).queryParam("operator", "==").get(GET_COUNTRIES_BY_FILTER);
        assertThat(200, equalTo(actualResponse.statusCode()));
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), equalTo(5000f));
        });
    }

    /*@Test
    void verifyGetCountryApiReturnDataWithEqualOperator_2() {
        String operator = URLEncoder.encode("==");
        String path = String.format("%s?gdp=5000&operator=%s", GET_COUNTRIES_BY_FILTER, operator);

        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), equalTo(5000f));
        });
    }*/

    @Test
    void verifyGetCountryApiReturnDataWithLessThanOrEqualOperator() {
        String path = String.format("%s?gdp=5000&operator=<=", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), lessThanOrEqualTo(5000f));
        });
        System.out.println(actualResponse.asString());
    }

    @Test
    void verifyGetCountryApiReturnDataWithGreaterThanOrEqualOperator() {
        String path = String.format("%s?gdp=5000&operator=>=", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), greaterThanOrEqualTo(5000f));
        });
        System.out.println(actualResponse.asString());
    }

    @Test
    void verifyGetCountryApiReturnDataWithNotEqualOperator() {
        String path = String.format("%s?gdp=5000&operator=!=", GET_COUNTRIES_BY_FILTER);
        Response actualResponse = RestAssured.given().log().all().get(path);
        assertThat(200, equalTo(actualResponse.statusCode()));
        //String actualResponseBody = actualResponse.asString();
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach(country -> {
            assertThat(country.getGdp(), not(equalTo(5000f)));
        });
    }

    //-------------------Refactor bằng chat gpt
    /*private void verifyGetCountryApiReturnDataWithOperator(String operator, float gdp, Matcher<Float> condition) {
        // Prepare the query parameter for operator
        String operatorParam = operator.equals("==") ? URLEncoder.encode(operator) : operator;
        String path = String.format("%s?gdp=%f&operator=%s", GET_COUNTRIES_BY_FILTER, gdp, operatorParam);

        // Make the request
        Response actualResponse = RestAssured.given().log().all().get(path);

        // Assert status code
        assertThat(actualResponse.statusCode(), equalTo(200));

        // Parse the response into a list of CountryVersionTwo objects
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });

        // Assert GDP based on the provided condition
        countries.forEach(country -> assertThat(country.getGdp(), condition));
    }

    *//*@ParameterizedTest
    @ValueSource(strings = {"<", ">", "<=", ">=", "==", "!="})
    void verifyGetCountryApiReturnDataWithVariousOperators(String operator) {
        float gdp = 5000f;

        // Determine the matching condition for each operator
        Matcher<Float> condition;
        switch (operator) {
            case "<":
                condition = lessThan(gdp);
                break;
            case ">":
                condition = greaterThan(gdp);
                break;
            case "<=":
                condition = lessThanOrEqualTo(gdp);
                break;
            case ">=":
                condition = greaterThanOrEqualTo(gdp);
                break;
            case "==":
                condition = equalTo(gdp);
                break;
            case "!=":
                condition = not(equalTo(gdp));
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }

        // Call the helper method
        verifyGetCountryApiReturnDataWithOperator(operator, gdp, condition);
    }
*/
    //------------------------end--------------------------------------
    //Dùng Stream để tạo bộ dữ liệu, dùng Provider để lấy dữ liệu trong bộ dữ liệu đã tạo
    static Stream<Map<String, String>> getCountriesByFilterProvider() throws JsonProcessingException {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(Map.of("gdp", "5000", "operator", ">"));
        inputs.add(Map.of("gdp", "300", "operator", "<"));
        inputs.add(Map.of("gdp", "400", "operator", ">="));
        inputs.add(Map.of("gdp", "500", "operator", "<="));
        inputs.add(Map.of("gdp", "600", "operator", "=="));
        inputs.add(Map.of("gdp", "700", "operator", "!="));
        return inputs.stream();
    }

    @ParameterizedTest
    @MethodSource("getCountriesByFilterProvider")
    void verifyGetCountryApiReturnCorrectDataWithCorrespondingFilter(Map<String, String> queryParams) {
        Response actualResponse = RestAssured.given().log().all().queryParams(queryParams).get(GET_COUNTRIES_BY_FILTER);
        System.out.println(actualResponse);
        assertThat(200, equalTo(actualResponse.statusCode()));
        List<CountryVersionTwo> countries = actualResponse.as(new TypeRef<List<CountryVersionTwo>>() {
        });
        countries.forEach
                (country -> {
                            float expectedGdp = Float.parseFloat(queryParams.get("gdp"));
                            Matcher<Float> matcher = switch (queryParams.get("operator")) {
                                case ">" -> greaterThan(expectedGdp);
                                case "<" -> lessThan(expectedGdp);
                                case "<=" -> lessThanOrEqualTo(expectedGdp);
                                case ">=" -> greaterThanOrEqualTo(expectedGdp);
                                case "==" -> equalTo(expectedGdp);
                                case "!=" -> not(equalTo(expectedGdp));
                                default -> equalTo(expectedGdp);
                            };
                            assertThat(country.getGdp(), matcher);
                        }
                );

    }
    //---------------------------------

    private CountryPagination getCountryPagination(int page, int size) {
        Response actualResponsePage = RestAssured.given().log().all()
                .queryParam("page", page)
                .queryParam("size", size)
                .get(GET_COUNTRIES_PAGINATION);
        return actualResponsePage.as(new TypeRef<CountryPagination>() {
        });
    }
    @Test
    void verifyGetCountriesPagination() {
        int pageSize = 3;
        CountryPagination countryPaginationFirstPage = getCountryPagination(1, pageSize);

        CountryPagination countryPaginationSecondPage = getCountryPagination(2, pageSize);

        assertThat(countryPaginationFirstPage.getData().size(), equalTo(pageSize));
        assertThat(countryPaginationSecondPage.getData().size(), equalTo(pageSize));
        assertThat(countryPaginationFirstPage.getData().containsAll(countryPaginationSecondPage.getData()), is(false));

        int sizeOfLastPage = countryPaginationFirstPage.getTotal() % pageSize;//Lấy phần dư
        int lastPage = countryPaginationFirstPage.getTotal() / pageSize; //Lấy phần nguyên
        if (sizeOfLastPage > 0){
            lastPage++;
        }
        if (sizeOfLastPage == 0){
            sizeOfLastPage = pageSize;
        }
        CountryPagination countryPaginationLastPage = getCountryPagination(lastPage, pageSize);
        assertThat(countryPaginationLastPage.getData().size(), equalTo(sizeOfLastPage));

        CountryPagination countryPaginationLastPagePlus = getCountryPagination(lastPage + 1, pageSize);
        assertThat(countryPaginationLastPagePlus.getData().size(), equalTo(0));
    }
//----------------------------------------------------------------------------------
    @Test
    void verifyGetCountriesWithPrivateKeyResponseJsonSchema(){
        //String responseBody = RestAssured.get(GET_COUNTRIES_PRIVATE_KEY).getBody().asString();
        RestAssured.get(GET_COUNTRIES_PRIVATE_KEY).then().assertThat().body(matchesJsonSchemaInClasspath("json-schema/get-countries-with-private-key-json-schema.json"));
    }
    @Test
    void verifyGetCountriesWithPrivateKey(){
        Response actualResponse = RestAssured.given().log().all()
                .header("api-key","private")
                .get(GET_COUNTRIES_PRIVATE_KEY);
        List<CountryVersionThree> countries = actualResponse.as(new TypeRef<List<CountryVersionThree>>() {
        });
        assertThat(actualResponse.asString(), jsonEquals(COUNTRY_WITH_PRIVATE_KEY).when(IGNORING_ARRAY_ORDER));

    }
}
