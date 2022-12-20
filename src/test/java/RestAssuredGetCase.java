import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class RestAssuredGetCase {
    public static final String API_KEY = "apikey";
    public static final String API_VALUE = "MAGbUchGPtsLzksQmDXtaPNuNu0WdlHa";
    public static final String BASE_API_URL = "http://dataservice.accuweather.com/locations/v1/topcities/50";

    @Test
    public void httpRequestTest() {
        RestAssured
                .given()
                .queryParam(API_KEY, API_VALUE)
                .when()
                .get(BASE_API_URL)
                .then().log().all()
                .statusCode(200);
    }

    @Test
    public void httpGETTest() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addQueryParam(API_KEY, API_VALUE)
                .build();

        Response response = given()
                .expect()
                .header("Content-Type", "application/json")
                .statusLine("HTTP/1.1 200 OK")
                .statusCode(200)
                .when()
                .get(BASE_API_URL);

        Location[] locations = response
                .then()
                .extract().body().jsonPath().getObject("", Location[].class);

        Location location = locations[0];

        Assertions.assertEquals("Dhaka", location.getLocalizedName());
    }

    @Test
    public void httpGETNotFoundTest() {
        Response response = given()
                .expect()
                .statusLine("HTTP/1.1 404 Not Found")
                .statusCode(404)
                .when()
                .get(BASE_API_URL + "extra_text");
    }

}