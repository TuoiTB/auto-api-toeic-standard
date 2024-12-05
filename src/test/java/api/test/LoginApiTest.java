package api.test;

import api.common.RestAssuredSetUp;
import api.model.login.LoginInput;
import api.model.login.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginApiTest {
    private static final String LOGIN_PATH = "/api/login";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234567890";
    private static final String ERROR_MESSAGE = "Invalid credentials";


    @BeforeAll
    static void setUp() {
        RestAssuredSetUp.setUp();
    }
    @Test
    void verifyStaffLoginSuccessfully() {
        LoginInput loginInput = new LoginInput(USERNAME, PASSWORD);
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
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
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithInvalidPassword() {
        LoginInput loginInput = new LoginInput(USERNAME, "1");
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithUsernameNull() {
        LoginInput loginInput = new LoginInput(null, PASSWORD);
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithPasswordNull() {
        LoginInput loginInput = new LoginInput(USERNAME, null);
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithUsernameEmpty() {
        LoginInput loginInput = new LoginInput("", PASSWORD);
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    @Test
    void verifyStaffLoginWithPasswordEmpty() {
        LoginInput loginInput = new LoginInput(USERNAME, "");
        Response actualResponse = RestAssured.given().log().all().header("Content-Type", "application/json").body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(401));
        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
        System.out.println();
    }

    //custom code level 1, use provider
    static Stream<Arguments> loginProvider() {
        return Stream.of(Arguments.of(new LoginInput("admin1", "1234567890"), 401, ERROR_MESSAGE),//Invalid username
                Arguments.of(new LoginInput("admin", "12345678901"), 401, ERROR_MESSAGE),//invalid password
                Arguments.of(new LoginInput(null, "1234567890"), 401, ERROR_MESSAGE),//UsernameNull
                Arguments.of(new LoginInput("admin", null), 401, ERROR_MESSAGE),//PasswordNull
                Arguments.of(new LoginInput("", "1234567890"), 401, ERROR_MESSAGE),//UsernameEmpty
                Arguments.of(new LoginInput("admin", ""), 401, ERROR_MESSAGE)//PasswordEmpty
        );
    }

    @ParameterizedTest
    @MethodSource("loginProvider")
    void verifyLoginUnhappyCases(LoginInput loginInput, int expectedStatuscode, String expectedErrorMessage) {
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput).post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(expectedStatuscode));
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(expectedErrorMessage));
    }

//custom code level 2
    static Stream<List<String>> invalidLoginInputs() {
        return Stream.of(
                Arrays.asList("Invalid username", "admin1", "1234567890"),
                Arrays.asList("Invalid password", "admin", "12345678901"),
                Arrays.asList("Username null", null, "1234567890"),
                Arrays.asList("Password null", "admin", null),
                Arrays.asList("Username empty", "", "1234567890"),
                Arrays.asList("Password empty", "admin", ""));
    }

    static Response getActualLoginResponse(String username, String password) {
        LoginInput loginInput = new LoginInput(username, password);
        return RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput).post(LOGIN_PATH);
    }

    @ParameterizedTest
    @MethodSource("invalidLoginInputs")
    public void verifyLoginInvalid(List<String> invalidLoginInputs) {
        Response actualResponse = getActualLoginResponse(invalidLoginInputs.get(1), invalidLoginInputs.get(2));
        assertThat(actualResponse.statusCode(), equalTo(401));
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
    }

    //Custom code level 3
    static Stream<List<String>> invalidLoginInputs1() {
        return Stream.of(
                new LinkedList<>(Arrays.asList("Invalid username", "admin1", "1234567890")),
                new LinkedList<>(Arrays.asList("Invalid password", "admin", "12345678901")),
                new LinkedList<>(Arrays.asList("Username null", null, "1234567890")), // Hỗ trợ null
                new LinkedList<>(Arrays.asList("Password null", "admin", null)),    // Hỗ trợ null
                new LinkedList<>(Arrays.asList("Username empty", "", "1234567890")),
                new LinkedList<>(Arrays.asList("Password empty", "admin", ""))
        );
    }

    static Response getActualLoginResponse1(String username, String password) {
        LoginInput loginInput = new LoginInput(username, password);
        return RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput).post(LOGIN_PATH);
    }

    @ParameterizedTest
    @MethodSource("invalidLoginInputs1")
    public void verifyLoginInvalid1(List<String> invalidLoginInputs) {
        Response actualResponse = getActualLoginResponse1(invalidLoginInputs.get(1), invalidLoginInputs.get(2));
        assertThat(actualResponse.statusCode(), equalTo(401));
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getMessage(), equalTo(ERROR_MESSAGE));
    }
}
