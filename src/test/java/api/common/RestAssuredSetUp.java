package api.common;

import io.restassured.RestAssured;

public class RestAssuredSetUp {
    public static void setUp(){
    RestAssured.baseURI = ConstantUtils.BASE_URI;
    RestAssured.port = ConstantUtils.BASE_PORT;
    }
}
