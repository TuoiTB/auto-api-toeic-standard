package api.test;

import api.model.login.LoginInput;
import api.model.login.LoginResponse;
import api.model.user.Addresses;
import api.model.user.UserInput;
import api.model.user.CreateUserResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateUserApiTest {
    private static final String LOGIN_PATH = "/api/login";
    private static final String CREATE_USER_PATH = "/api/user";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    void verifyStaffCreateUserSuccessfully() {
        LoginInput loginInput = new LoginInput("admin", "1234567890");
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(200));

        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getToken(), not(blankString()));

        Addresses addresses = new Addresses();
        addresses.setStreetNumber("136");
        addresses.setStreet("136 Ho Tung Mau Street");
        addresses.setWard("Phu Dien");
        addresses.setDistrict("Bac Tu Liem");
        addresses.setCity("Ha Noi");
        addresses.setState("Ha Noi");
        addresses.setZip("10000");
        addresses.setCountry("VN");

        UserInput user = new UserInput();
        user.setId("dc6c7962-dc8f-49f7-a7a1-af990868badf");
        user.setFirstName("John");
        user.setLastName("Dow");
        user.setMiddleName("Smith");
        user.setBirthday("20-09-1999");
        user.setEmail("John12312@gmail.com");
        user.setPhone("0969156841");
        user.setAddresses(List.of(addresses));

        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ".concat(loginResponse.getToken()))
                .body(user)
                .post(CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(200));

        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        System.out.printf("Create user response: %s%n" , createUserResponse.asString());
        assertThat(userResponse.getId(), not(blankString()));
        assertThat(userResponse.getMessage(), equalTo("Customer created"));



    }

}
