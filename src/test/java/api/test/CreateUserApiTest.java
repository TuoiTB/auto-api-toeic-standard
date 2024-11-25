package api.test;

import api.model.login.LoginInput;
import api.model.login.LoginResponse;
import api.model.user.Addresses;
import api.model.user.UserInput;
import api.model.user.CreateUserResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateUserApiTest {
    private static final String LOGIN_PATH = "/api/login";
    private static final String CREATE_USER_PATH = "/api/user";
    private static final String DELETE_USER_PATH = "/api/user/{id}";
    private static final String AUTHOZIZATON_HEADER = "Authorization";
    private static String TOKEN = "";

    private static List<String> createdUserIds = new ArrayList<>();



    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
//Get token
        LoginInput loginInput = new LoginInput("admin", "1234567890");
        Response actualResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .body(loginInput)
                .post(LOGIN_PATH);
        assertThat(actualResponse.statusCode(), equalTo(200));

        //Need to verify schema
        LoginResponse loginResponse = actualResponse.as(LoginResponse.class);
        assertThat(loginResponse.getToken(), not(blankString()));
        TOKEN = "Bearer ".concat(loginResponse.getToken());
    }

    @Test
    void verifyStaffCreateUserSuccessfully() {
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
        user.setId("125966b6-21e1-446f-9f75-53e206a4c496");
        user.setFirstName("John");
        user.setLastName("Dow");
        user.setMiddleName("Smith");
        user.setBirthday("20-09-1999");
        String randomEmail = String.format("auto_api_%s@test.com", System.currentTimeMillis());
        user.setEmail(randomEmail);
        user.setPhone("0962065317");
        user.setAddresses(List.of(addresses));

        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(200));

        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        createdUserIds.add(userResponse.getId());
        System.out.printf("Create user response: %s%n" , createUserResponse.asString());
        assertThat(userResponse.getId(), not(blankString()));
        assertThat(userResponse.getMessage(), equalTo("Customer created"));
        //Clean data
        //Cach 1
        /*RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ".concat(loginResponse.getToken()))
                .delete(DELETE_USER_PATH, Map.of("id",userResponse.getId()))
                */
        //Cach 2
        RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .pathParam("id",userResponse.getId())
                .delete(DELETE_USER_PATH);
    }
    @AfterAll
    static void tearDown(){
        //Clean data
        createdUserIds.forEach(id -> {
            RestAssured.given().log().all()
                    .header(AUTHOZIZATON_HEADER, TOKEN)
                    .pathParam("id",id)
                    .delete(DELETE_USER_PATH);
        });
    }
}
