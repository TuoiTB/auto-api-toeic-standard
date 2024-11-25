package api.test;

import api.data.GetCountriesData;
import api.model.country.Country;
import api.model.country.CountryPagination;
import api.model.country.CountryVersionThree;
import api.model.country.CountryVersionTwo;
import api.model.login.LoginInput;
import api.model.login.LoginResponse;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static api.data.GetCountriesData.COUNTRY_WITH_PRIVATE_KEY;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginApiTest {
    private static final String LOGIN_PATH = "/api/login";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234567890";
    private static final String ERROR_MESSAGE = "Invalid credentials";


    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    void verifyStaffLoginSuccessfully() {
        LoginInput loginInput = new LoginInput(USERNAME, PASSWORD);
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(200));

        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getToken(), not(blankString()));
        assertThat(loginResponse.getTimeout(), equalTo(120000));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithInvalidUsername() {
        LoginInput loginInput = new LoginInput("userInvalid", PASSWORD);
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithInvalidPassword() {
        LoginInput loginInput = new LoginInput(USERNAME, "1");
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithInvalidPasswordAndInvalidUsername() {
        LoginInput loginInput = new LoginInput("1", "1");
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithUsernameNull() {
        LoginInput loginInput = new LoginInput(null, PASSWORD);
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithPasswordNull() {
        LoginInput loginInput = new LoginInput(USERNAME, null);
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithUsernameEmpty() {
        LoginInput loginInput = new LoginInput("", PASSWORD);
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }
    @Test
    void verifyStaffLoginWithPasswordEmpty() {
        LoginInput loginInput = new LoginInput(USERNAME, "");
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

}
