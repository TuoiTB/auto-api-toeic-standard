package api.test;

import api.common.ConstantUtils;
import api.common.DatabaseConnection;
import api.common.LoginUtils;
import api.common.RestAssuredSetUp;
import api.model.login.LoginResponse;
import api.model.user.*;
import api.model.user.dto.DbAddresses;
import api.model.user.dto.DbUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UpdateUserApiTest {
    private static String TOKEN = "";
    private static final List<String> createdUserIds = new ArrayList<>();
    private static long TIMEOUT = -1;
    private static long TIME_BEFORE_GET_TOKEN = -1;
    private static final SessionFactory sessionFactory = DatabaseConnection.getSession();

    @BeforeAll
    static void setUp() {
        RestAssuredSetUp.setUp();
    }


    @BeforeEach
    void beforeEach() {
        if (TIMEOUT == -1 || (System.currentTimeMillis() - TIME_BEFORE_GET_TOKEN) > TIMEOUT * 0.8) {
            TIME_BEFORE_GET_TOKEN = System.currentTimeMillis();
            LoginResponse loginResponse = LoginUtils.login();
            assertThat(loginResponse.getToken(), not(blankString()));
            TOKEN = "Bearer ".concat(loginResponse.getToken());
            TIMEOUT = loginResponse.getTimeout();
        }
    }

    @Test
    void verifyStaffUpdateUserSuccessfully() {
        //1. Create successfully
        AddressesInput addresses = AddressesInput.getDefault();
        UserInput<AddressesInput> user = UserInput.getDefaultWithEmail();
        user.setAddresses(List.of(addresses));

        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(ConstantUtils.AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(ConstantUtils.CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(200));

        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        createdUserIds.add(userResponse.getId());
        System.out.printf("Create user response: %s%n", createUserResponse.asString());

        //2. Put user successfully
        UpdateAddressesInput updateAddressesInput = UpdateAddressesInput.getDefault();
        UpdateUserInput<UpdateAddressesInput> updateUserInput = UpdateUserInput.getDefaultWithEmail();
        updateUserInput.setAddresses(List.of(updateAddressesInput));
        //Store the moment before execution
        Instant beforeExecution = Instant.now();

        Response updateUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(ConstantUtils.AUTHOZIZATON_HEADER, TOKEN)
                .pathParam("id", userResponse.getId())
                .body(updateUserInput)
                .put(ConstantUtils.PUT_USER_PATH);
        assertThat(updateUserResponse.statusCode(), equalTo(200));
        UpdateUserResponse userUpdatedResponse = updateUserResponse.as(UpdateUserResponse.class);
        assertThat(userUpdatedResponse.getId(), not(blankString()));
        assertThat(userUpdatedResponse.getMessage(), equalTo("Customer updated"));

        //3. Save expected result
        ObjectMapper mapper = new ObjectMapper();
        GetUserUpdateResponse<UpdateAddressesResponse> expectedUserUpdated = mapper.convertValue(updateUserInput, new TypeReference<GetUserUpdateResponse<UpdateAddressesResponse>>() {
        });
        expectedUserUpdated.setId(userUpdatedResponse.getId());
        expectedUserUpdated.getAddresses().get(0).setCustomerId(userUpdatedResponse.getId());

        //4. Get actual result
        Response getActualUserUpdateResponse = RestAssured.given().log().all()
                .header(ConstantUtils.AUTHOZIZATON_HEADER, TOKEN)
                .pathParam("id", userUpdatedResponse.getId())
                .get(ConstantUtils.GET_USER_PATH);
        System.out.printf("Get user response: %s%n", getActualUserUpdateResponse.asString());
        String actualGetUserUpdated = getActualUserUpdateResponse.asString();
        //verify data, ignore some fieds
        assertThat(actualGetUserUpdated, jsonEquals(expectedUserUpdated).whenIgnoringPaths(
                "createdAt", "updatedAt", "addresses[*].id", "addresses[*].createdAt", "addresses[*].updatedAt"));

        //5. verify ignore fields which is in before step
        GetUserUpdateResponse<UpdateAddressesResponse> actualGetUpdatedModel = getActualUserUpdateResponse.as(new TypeRef<GetUserUpdateResponse<UpdateAddressesResponse>>() {
        });

        Instant userEditCreatedAt = Instant.parse(actualGetUpdatedModel.getCreatedAt());
        datetimeVerifier(beforeExecution, userEditCreatedAt);
        Instant userEditUpdatedAt = Instant.parse(actualGetUpdatedModel.getUpdatedAt());
        datetimeVerifier(beforeExecution, userEditUpdatedAt);

        actualGetUpdatedModel.getAddresses().forEach(actualAddress -> {
            assertThat(actualAddress.getId(), not(blankString()));

            Instant addressEditCreateAt = Instant.parse(actualAddress.getCreatedAt());
            datetimeVerifier(beforeExecution, addressEditCreateAt);

            Instant addressEditUpdatedAt = Instant.parse(actualAddress.getUpdatedAt());
            datetimeVerifier(beforeExecution, addressEditUpdatedAt);
        });
        tearDown();
    }

    @Test
    void verifyStaffCreateUserSuccessfullyWithDB() {
        //1. Create successfully
        AddressesInput addresses = AddressesInput.getDefault();
        UserInput<AddressesInput> user = UserInput.getDefaultWithEmail();
        user.setAddresses(List.of(addresses));

        Response createUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(ConstantUtils.AUTHOZIZATON_HEADER, TOKEN)
                .body(user)
                .post(ConstantUtils.CREATE_USER_PATH);
        assertThat(createUserResponse.statusCode(), equalTo(200));

        CreateUserResponse userResponse = createUserResponse.as(CreateUserResponse.class);
        createdUserIds.add(userResponse.getId());
        System.out.printf("Create user response: %s%n", createUserResponse.asString());

        //2. Put user successfully
        UpdateAddressesInput updateAddressesInput = UpdateAddressesInput.getDefault();
        UpdateUserInput<UpdateAddressesInput> updateUserInput = UpdateUserInput.getDefaultWithEmail();
        updateUserInput.setAddresses(List.of(updateAddressesInput));
        //Store the moment before execution
        Instant beforeExecution = Instant.now();

        Response updateUserResponse = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header(ConstantUtils.AUTHOZIZATON_HEADER, TOKEN)
                .pathParam("id", userResponse.getId())
                .body(updateUserInput)
                .put(ConstantUtils.PUT_USER_PATH);
        assertThat(updateUserResponse.statusCode(), equalTo(200));
        UpdateUserResponse userUpdatedResponse = updateUserResponse.as(UpdateUserResponse.class);
        assertThat(userUpdatedResponse.getId(), not(blankString()));
        assertThat(userUpdatedResponse.getMessage(), equalTo("Customer updated"));

        //3. Save expected result
        ObjectMapper mapper = new ObjectMapper();
        GetUserUpdateResponse<UpdateAddressesResponse> expectedUserUpdated = mapper.convertValue(updateUserInput, new TypeReference<GetUserUpdateResponse<UpdateAddressesResponse>>() {
        });
        expectedUserUpdated.setId(userUpdatedResponse.getId());
        expectedUserUpdated.getAddresses().get(0).setCustomerId(userUpdatedResponse.getId());

        //4. Query DB
        sessionFactory.inTransaction(session -> {
            DbUser dbUser = session.createSelectionQuery("from DbUser where id=:id", DbUser.class)
                    .setParameter("id", UUID.fromString(userUpdatedResponse.getId()))
                    .getSingleResult();
            System.out.println(dbUser);
            List<DbAddresses> dbAddresses = session.createSelectionQuery("from DbAddresses where customerId=:customerId", DbAddresses.class)
                    .setParameter("customerId", UUID.fromString(userUpdatedResponse.getId()))
                    .getResultList();
            System.out.println(dbAddresses);


            //5. verify ignore fields which is in before step
            GetUserUpdateResponse<UpdateAddressesResponse> actualGetUpdatedModel = mapper.convertValue(dbUser, new TypeReference<GetUserUpdateResponse<UpdateAddressesResponse>>() {
            });
            actualGetUpdatedModel.setAddresses(mapper.convertValue(dbAddresses,
                    new TypeReference<List<UpdateAddressesResponse>>() {
                    }));
            assertThat(actualGetUpdatedModel, jsonEquals(expectedUserUpdated).whenIgnoringPaths("createdAt", "updatedAt", "addresses[*].id",
                    "addresses[*].createdAt", "addresses[*].updatedAt"));
            Instant userEditCreatedAt = Instant.parse(actualGetUpdatedModel.getCreatedAt());
            datetimeVerifier(beforeExecution, userEditCreatedAt);

            Instant userEditUpdatedAt = Instant.parse(actualGetUpdatedModel.getUpdatedAt());
            datetimeVerifier(beforeExecution, userEditUpdatedAt);

            actualGetUpdatedModel.getAddresses().forEach(actualAddress -> {
                assertThat(actualAddress.getId(), not(blankString()));

                Instant addressEditCreateAt = Instant.parse(actualAddress.getCreatedAt());
                datetimeVerifier(beforeExecution, addressEditCreateAt);

                Instant addressEditUpdatedAt = Instant.parse(actualAddress.getUpdatedAt());
                datetimeVerifier(beforeExecution, addressEditUpdatedAt);
            });
        });
    }
    private void datetimeVerifier(Instant timeBeforeExecution, Instant actualTime) {
        assertThat(actualTime.isAfter(timeBeforeExecution), equalTo(true));
        assertThat(actualTime.isBefore(Instant.now()), equalTo(true));
    }
    @AfterAll
    static void tearDown() {
        // Clean up data
        createdUserIds.forEach(id -> {
            RestAssured.given().log().all()
                    .header(ConstantUtils.AUTHOZIZATON_HEADER, TOKEN)
                    .pathParam("id", id)
                    .delete(ConstantUtils.DELETE_USER_PATH);
        });
    }
}