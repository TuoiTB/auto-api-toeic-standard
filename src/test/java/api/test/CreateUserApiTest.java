package api.test;

import api.model.login.LoginInput;
import api.model.login.LoginResponse;
import api.model.user.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateUserApiTest {
    private static final String LOGIN_PATH = "/api/login";
    private static final String CREATE_USER_PATH = "/api/user";
    private static final String DELETE_USER_PATH = "/api/user/{id}";
    private static final String GET_USER_PATH = "/api/user/{id}";
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
        AddressesInput addresses = AddressesInput.getDefault();
        UserInput<AddressesInput> user = UserInput.getDefault();
        String randomEmail = String.format("auto_api_%s@test.com", System.currentTimeMillis());
        user.setEmail(randomEmail);
        user.setAddresses(List.of(addresses));
        //Store the moment before execution
        Instant beforeExecution = Instant.now();

        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(200));

        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        createdUserIds.add(userResponse.getId());
        System.out.printf("Create user response: %s%n", createUserResponse.asString());
        assertThat(userResponse.getId(), not(blankString()));
        assertThat(userResponse.getMessage(), equalTo("Customer created"));

        Response getCreateUserResponse = RestAssured.given().log().all()
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .pathParam("id", userResponse.getId())
                .get(GET_USER_PATH);
        System.out.printf("Get user response: %s%n", getCreateUserResponse.asString());
        //verify status code
        assertThat(getCreateUserResponse.statusCode(), equalTo(200));
        //TO-DO:verify schema
        //Verify correct data
      /*  String expectedTemplate = """
                {
                    "id": "%s",
                    "firstName": "John",
                    "lastName": "Dow",
                    "middleName": "Smith",
                    "birthday": "20-09-1999",
                    "phone": "0962065307",
                    "email": "%s",
                    "createdAt": "",
                    "updatedAt": "",
                    "addresses": [
                        {
                            "id": "",
                            "customerId": "%s",
                            "streetNumber": "136",
                            "street": "136 Ho Tung Mau Street",
                            "ward": "Phu Dien",
                            "district": "Bac Tu Liem",
                            "city": "Ha Noi",
                            "state": "Ha Noi",
                            "zip": "10000",
                            "country": "VN",
                            "createdAt": "",
                            "updatedAt": ""
                        }
                    ]
                }
                """;*/
     /*   AddressesResponse addressResponse = new AddressesResponse();
        addressResponse.setCustomerId(userResponse.getId());
        addressResponse.setStreetNumber("136");
        addressResponse.setStreet("136 Ho Tung Mau Street");
        addressResponse.setWard("Phu Dien");
        addressResponse.setDistrict("Bac Tu Liem");
        addressResponse.setCity("Ha Noi");
        addressResponse.setState("Ha Noi");
        addressResponse.setZip("10000");
        addressResponse.setCountry("VN");

        GetUserResponse<AddressesResponse>  expectedUser = new GetUserResponse<AddressesResponse>();
        expectedUser.setId(userResponse.getId());
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Dow");
        expectedUser.setMiddleName("Smith");
        expectedUser.setBirthday("20-09-1999");
        expectedUser.setEmail(randomEmail);
        expectedUser.setPhone("0962065307");
        expectedUser.setAddresses(List.of(addressResponse));*/
        ObjectMapper mapper = new ObjectMapper();

        GetUserResponse<AddressesResponse> expectedUser = mapper.convertValue(user, new TypeReference<GetUserResponse<AddressesResponse>>() {
        });
        expectedUser.setId(userResponse.getId());
        expectedUser.getAddresses().get(0).setCustomerId(userResponse.getId());

        //String expected = String.format(expectedTemplate, userResponse.getId(), randomEmail, userResponse.getId());
        String actualGetCreated = getCreateUserResponse.asString();
        //verify data, ignore some fieds
        assertThat(actualGetCreated, jsonEquals(expectedUser).whenIgnoringPaths(
                "createdAt", "updatedAt", "addresses[*].id", "addresses[*].createdAt", "addresses[*].updatedAt"));

        //verify ignore fields which is in before step
        GetUserResponse<AddressesResponse> actualGetCreatedModel = getCreateUserResponse.as(new TypeRef<GetUserResponse<AddressesResponse>>() {
        });

        Instant userCreatedAt = Instant.parse(actualGetCreatedModel.getCreatedAt());
        System.out.println(userCreatedAt);
        assertThat(userCreatedAt.isAfter(beforeExecution), equalTo(true));
        assertThat(userCreatedAt.isBefore(Instant.now()), equalTo(true));

        Instant userUpdatedAt = Instant.parse(actualGetCreatedModel.getUpdatedAt());
        System.out.println(userUpdatedAt);
        assertThat(userUpdatedAt.isAfter(beforeExecution), equalTo(true));
        assertThat(userUpdatedAt.isBefore(Instant.now()), equalTo(true));

        actualGetCreatedModel.getAddresses().forEach(actualAddress -> {
            assertThat(actualAddress.getId(), not(blankString()));

            Instant adrressCreatedAt = Instant.parse(actualAddress.getCreatedAt());
            assertThat(adrressCreatedAt.isAfter(beforeExecution), equalTo(true));
            assertThat(adrressCreatedAt.isBefore(Instant.now()), equalTo(true));

            Instant addressUpdatedAt = Instant.parse(actualAddress.getUpdatedAt());
            assertThat(addressUpdatedAt.isAfter(beforeExecution), equalTo(true));
            assertThat(addressUpdatedAt.isBefore(Instant.now()), equalTo(true));
        });
    }

    @Test
    void verifyStaffCreateUserSuccessfullyWithMultipleAddresses() {
        AddressesInput addresses1 = AddressesInput.getDefault();
        AddressesInput addresses2 = AddressesInput.getDefault();
        addresses2.setStreet("Le Duc Tho");
        UserInput<AddressesInput> user = UserInput.getDefault();
        String randomEmail = String.format("auto_api_%s@test.com", System.currentTimeMillis());
        user.setEmail(randomEmail);
        user.setAddresses(List.of(addresses1, addresses2));
        //Store the moment before execution
        Instant beforeExecution = Instant.now();

        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(200));

        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        createdUserIds.add(userResponse.getId());
        System.out.printf("Create user response: %s%n", createUserResponse.asString());
        assertThat(userResponse.getId(), not(blankString()));
        assertThat(userResponse.getMessage(), equalTo("Customer created"));

        Response getCreateUserResponse = RestAssured.given().log().all()
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .pathParam("id", userResponse.getId())
                .get(GET_USER_PATH);
        System.out.printf("Get user response: %s%n", getCreateUserResponse.asString());
        //verify status code
        assertThat(getCreateUserResponse.statusCode(), equalTo(200));
        //TO-DO:verify schema

        //Verify correct data
        ObjectMapper mapper = new ObjectMapper();

        GetUserResponse<AddressesResponse> expectedUser = mapper.convertValue(user, new TypeReference<GetUserResponse<AddressesResponse>>() {
        });
        expectedUser.setId(userResponse.getId());
        expectedUser.getAddresses().get(0).setCustomerId(userResponse.getId());
        expectedUser.getAddresses().get(1).setCustomerId(userResponse.getId());

        String actualGetCreated = getCreateUserResponse.asString();
        //verify data, ignore some fieds
        assertThat(actualGetCreated, jsonEquals(expectedUser).whenIgnoringPaths(
                "createdAt", "updatedAt", "addresses[*].id", "addresses[*].createdAt", "addresses[*].updatedAt"));

        //verify ignore fields which is in before step
        GetUserResponse<AddressesResponse> actualGetCreatedModel = getCreateUserResponse.as(new TypeRef<GetUserResponse<AddressesResponse>>() {
        });

        Instant userCreatedAt = Instant.parse(actualGetCreatedModel.getCreatedAt());
        System.out.println(userCreatedAt);
        assertThat(userCreatedAt.isAfter(beforeExecution), equalTo(true));
        assertThat(userCreatedAt.isBefore(Instant.now()), equalTo(true));

        Instant userUpdatedAt = Instant.parse(actualGetCreatedModel.getUpdatedAt());
        System.out.println(userUpdatedAt);
        assertThat(userUpdatedAt.isAfter(beforeExecution), equalTo(true));
        assertThat(userUpdatedAt.isBefore(Instant.now()), equalTo(true));

        actualGetCreatedModel.getAddresses().forEach(actualAddress -> {
            assertThat(actualAddress.getId(), not(blankString()));

            Instant adrressCreatedAt = Instant.parse(actualAddress.getCreatedAt());
            assertThat(adrressCreatedAt.isAfter(beforeExecution), equalTo(true));
            assertThat(adrressCreatedAt.isBefore(Instant.now()), equalTo(true));

            Instant addressUpdatedAt = Instant.parse(actualAddress.getUpdatedAt());
            assertThat(addressUpdatedAt.isAfter(beforeExecution), equalTo(true));
            assertThat(addressUpdatedAt.isBefore(Instant.now()), equalTo(true));
        });
    }

/*    @Test
    void verifyRequiredFirstNameNull() {
        AddressesInput addresses = AddressesInput.getDefault();
        UserInput<AddressesInput> user = UserInput.getDefault();
        String randomEmail = String.format("auto_api_%s@test.com", System.currentTimeMillis());
        user.setEmail(randomEmail);
        user.setAddresses(List.of(addresses));
        user.setFirstName(null);
        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(400));
        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        System.out.printf("Create user response: %s%n", createUserResponse.asString());
        assertThat(userResponse.getMessage(), equalTo("must be string"));
    }

    @Test
    void verifyRequiredFirstNameEmpty() {
        AddressesInput addresses = AddressesInput.getDefault();
        UserInput<AddressesInput> user = UserInput.getDefault();
        String randomEmail = String.format("auto_api_%s@test.com", System.currentTimeMillis());
        user.setEmail(randomEmail);
        user.setAddresses(List.of(addresses));
        user.setFirstName("");
        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(400));
        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        System.out.printf("Create user response: %s%n", createUserResponse.asString());
        assertThat(userResponse.getMessage(), equalTo("must NOT have fewer than 1 characters"));
    }

    @Test
    void verifyRequiredLastNameNull() {

    }

    @Test
    void verifyRequiredLastNameEmpty() {

    }

    @Test
    void verifyRequiredBirthdayNameNull() {

    }

    @Test
    void verifyRequiredBirthdayEmpty() {

    }

    @Test
    void verifyRequiredEmailNull() {

    }

    @Test
    void verifyRequiredEmailEmpty() {

    }

    @Test
    void verifyRequiredPhoneNumberNull() {

    }

    @Test
    void verifyRequiredPhoneNumberEmpty() {

    }*/

    //Custom code to verify required field
    @ParameterizedTest()
    @MethodSource("validationUserProvider")
    void verifyRequiredFieldWhenCreatingUser(String testcase, UserInput<AddressesInput> userInput, ValidationResponse expectedResponse) {
        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(AUTHOZIZATON_HEADER, TOKEN)
                .body(userInput)
                .post(CREATE_USER_PATH);
        System.out.printf("Create user response: %s%n", createUserResponse.asString());
        assertThat(createUserResponse.statusCode(), equalTo(400));
        ValidationResponse actual = createUserResponse.as(ValidationResponse.class);
        assertThat(actual, samePropertyValuesAs(expectedResponse));

    }

    static Stream<Arguments> validationUserProvider() throws JsonProcessingException {
        /*List<UserInput<AddressesInput>> userInputs = new ArrayList<>();

        UserInput<AddressesInput> userInput = UserInput.getDefault();
        userInput.setFirstName(null);
        userInputs.add(userInput);

        userInput = UserInput.getDefault();
        userInput.setFirstName("");
        userInputs.add(userInput);
        return userInputs.stream();*///=> code này ra kết quả testcase xấu
        List<Arguments> argumentsList = new ArrayList<>();

        //Data to verify firstName null, empty, more than 100 characters
        UserInput<AddressesInput> userInput = UserInput.getDefaultWithEmail();
        userInput.setFirstName(null);
        argumentsList.add(Arguments.arguments("Verify API return 400 when firstName is null", userInput,
                new ValidationResponse<>("", "must have required property 'firstName'")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setFirstName("");
        argumentsList.add(Arguments.arguments("Verify API return 400 when firstName is empty", userInput,
                new ValidationResponse<>("/firstName", "must NOT have fewer than 1 characters")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setFirstName("yyyyyyyyyyyyyyyyyyyyyyy yyyyyyyyyyyyyyy yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy fffffffffffffffffff");
        argumentsList.add(Arguments.arguments("Verify API return 400 when firstName is more than 100 characters", userInput,
                new ValidationResponse<>("/firstName", "must NOT have more than 100 characters")));

        //Data to verify lastName null, empty, more than 100 characters
        userInput = UserInput.getDefaultWithEmail();
        userInput.setLastName(null);
        argumentsList.add(Arguments.arguments("Verify API return 400 when lastName is null", userInput,
                new ValidationResponse<>("", "must have required property 'lastName'")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setLastName("");
        argumentsList.add(Arguments.arguments("Verify API return 400 when lastName is empty", userInput,
                new ValidationResponse<>("/lastName", "must NOT have fewer than 1 characters")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setLastName("yyyyyyyyyyyyyyyyyyyyyyy yyyyyyyyyyyyyyy yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy fffffffffffffffffff");
        argumentsList.add(Arguments.arguments("Verify API return 400 when lastName is more than 100 characters", userInput,
                new ValidationResponse<>("/lastName", "must NOT have more than 100 characters")));

        //Data to verify middleName empty, more than 100 characters
        userInput = UserInput.getDefaultWithEmail();
        userInput.setMiddleName("");
        argumentsList.add(Arguments.arguments("Verify API return 400 when middleName is null", userInput,
                new ValidationResponse<>("/middleName", "must NOT have fewer than 1 characters")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setMiddleName("yyyyyyyyyyyyyyyyyyyyyyy yyyyyyyyyyyyyyy yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy fffffffffffffffffff");
        argumentsList.add(Arguments.arguments("Verify API return 400 when middleName is more than 100 characters", userInput,
                new ValidationResponse<>("/middleName", "must NOT have more than 100 characters")));

        //Data to verify birthday null, empty, contains letter characters
        userInput = UserInput.getDefaultWithEmail();
        userInput.setBirthday(null);
        argumentsList.add(Arguments.arguments("Verify API return 400 when birthday is null", userInput,
                new ValidationResponse<>("", "must have required property 'birthday'")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setBirthday("");
        argumentsList.add(Arguments.arguments("Verify API return 400 when birthday is empty", userInput,
                new ValidationResponse<>("/birthday", "must match pattern \"^\\d{2}-\\d{2}-\\d{4}$\"")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setBirthday("20-09-1997a");
        argumentsList.add(Arguments.arguments("Verify API return 400 when birthday is contains letter characters", userInput,
                new ValidationResponse<>("/birthday", "must match pattern \"^\\d{2}-\\d{2}-\\d{4}$\"")));

        //Data to verify email null, empty, isn't email format
        userInput = UserInput.getDefaultWithEmail();
        userInput.setEmail(null);
        argumentsList.add(Arguments.arguments("Verify API return 400 when email is null", userInput,
                new ValidationResponse<>("", "must have required property 'email'")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setEmail("");
        argumentsList.add(Arguments.arguments("Verify API return 400 when email is empty", userInput,
                new ValidationResponse<>("/email", "must match format \"email\"")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setEmail("a.com");
        argumentsList.add(Arguments.arguments("Verify API return 400 when email is not email format", userInput,
                new ValidationResponse<>("/email", "must match format \"email\"")));

        //Data to verify phone null, empty, contains letter characters,
        userInput = UserInput.getDefaultWithEmail();
        userInput.setPhone(null);
        argumentsList.add(Arguments.arguments("Verify API return 400 when phone is null", userInput,
                new ValidationResponse<>("", "must have required property 'phone'")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setPhone("");
        argumentsList.add(Arguments.arguments("Verify API return 400 when phone is empty", userInput,
                new ValidationResponse<>("/phone", "must match pattern \"^\\d{10,11}$\"")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setPhone("065H145854");
        argumentsList.add(Arguments.arguments("Verify API return 400 when phone is contains letter characters", userInput,
                new ValidationResponse<>("/phone", "must match pattern \"^\\d{10,11}$\"")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setPhone("021");
        argumentsList.add(Arguments.arguments("Verify API return 400 when phone is fewer than 10 or 11 characters", userInput,
                new ValidationResponse<>("/phone", "must match pattern \"^\\d{10,11}$\"")));

        userInput = UserInput.getDefaultWithEmail();
            userInput.setPhone("0213547891254");
        argumentsList.add(Arguments.arguments("Verify API return 400 when phone is more than 10 or 11 characters", userInput,
                new ValidationResponse<>("/phone", "must match pattern \"^\\d{10,11}$\"")));

        userInput = UserInput.getDefaultWithEmail();
        userInput.setPhone("-0965210452");
        argumentsList.add(Arguments.arguments("Verify API return 400 when phone is contains special characters", userInput,
                new ValidationResponse<>("/phone", "must match pattern \"^\\d{10,11}$\"")));

        return argumentsList.stream();
    }


    @AfterAll
    static void tearDown() {
        //Clean data
        createdUserIds.forEach(id -> {
            RestAssured.given().log().all()
                    .header(AUTHOZIZATON_HEADER, TOKEN)
                    .pathParam("id", id)
                    .delete(DELETE_USER_PATH);
        });
    }
}
