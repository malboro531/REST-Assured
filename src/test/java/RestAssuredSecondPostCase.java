import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestAssuredSecondPostCase {

    private Logger logger = LogManager.getLogger(RestAssuredSecondPostCase.class);

    @Test
    public void authTruePostTest() {
        String CSRF_TOKEN = "csrf-token";
        String AJS_ANONYMOUS_ID = "ajs_anonymous_id";
        String _CSRF = "_csrf";
        String __CFLB = "__cflb";

        // Шаг 1. Получаем csrf-token
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://rapidapi.com/")
                .build();
        Response responseWithCsrfToken = RestAssured.get("gateway/csrf");
        String rawToken = responseWithCsrfToken.getBody().prettyPrint();
        String token = getCsrfToken(rawToken);

        // Шаг 2. Вход
        String authData = "{\r\n" +
                "   \"email\" : \"kovalev-denis_1996@mail.ru\",\r\n" +
                "   \"password\" : \"paSSword186!\"\r\n" +
                "}";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader(CSRF_TOKEN, token)
                .setBaseUri("https://rapidapi.com/")
                .setContentType(ContentType.JSON)
                .addCookies(
                        Map.of(
                                AJS_ANONYMOUS_ID, responseWithCsrfToken.getCookies().get(AJS_ANONYMOUS_ID),
                                _CSRF, responseWithCsrfToken.getCookies().get(_CSRF),
                                __CFLB, responseWithCsrfToken.getCookies().get(__CFLB)
                        )
                ).setBody(authData)
                .build();
        Response response = RestAssured.post("authentication/login");
        // Вывод ответа
        response.prettyPrint();
        // Проверка кода статуса ответа
        response.then().statusCode(201);
        logger.info("Код ответа авторизациии на сайте https://rapidapi.com/ при правильных авторизационных " +
                "данных соответсвует запрашиваемому");
    }

//    @Test
//    public void authFalsePostTest() {
//        String CSRF_TOKEN = "csrf-token";
//        String AJS_ANONYMOUS_ID = "ajs_anonymous_id";
//        String _CSRF = "_csrf";
//        String __CFLB = "__cflb";
//
//        // Шаг 1. Получаем csrf-token
//        RestAssured.requestSpecification = new RequestSpecBuilder()
//                .setContentType(ContentType.JSON)
//                .setBaseUri("https://rapidapi.com/")
//                .build();
//        Response responseWithCsrfToken = RestAssured.get("gateway/csrf");
//        String rawToken = responseWithCsrfToken.getBody().prettyPrint();
//        String token = getCsrfToken(rawToken);
//
//        // Шаг 2. Вход
//        String authData = "{\r\n" +
//                "   \"email\" : \"FALSEEMAIL\",\r\n" +
//                "   \"password\" : \"FALSEPASSWORD\"\r\n" +
//                "}";
//        RestAssured.requestSpecification = new RequestSpecBuilder()
//                .addHeader(CSRF_TOKEN, token)
//                .setBaseUri("https://rapidapi.com/")
//                .setContentType(ContentType.JSON)
//                .addCookies(
//                        Map.of(
//                                AJS_ANONYMOUS_ID, responseWithCsrfToken.getCookies().get(AJS_ANONYMOUS_ID),
//                                _CSRF, responseWithCsrfToken.getCookies().get(_CSRF),
//                                __CFLB, responseWithCsrfToken.getCookies().get(__CFLB)
//                        )
//                ).setBody(authData)
//                .build();
//        Response response = RestAssured.post("authentication/login");
//        // Вывод ответа
//        response.prettyPrint();
//        // Проверка кода статуса ответа
//        response.then().statusCode(400);
//        logger.info("Код ответа авторизациии на сайте https://rapidapi.com/ при неправильных авторизационных " +
//                "данных соответсвует запрашиваемому");
//    }

    private String getCsrfToken(String rawToken) {
        String pattern = "\"csrfToken\".*:.*\"(?<token>.+)\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(rawToken);

        while(m.find()) {
            return m.group("token");
        }

        return null;
    }

    private boolean isContainStringToken(String body) { return body.contains("csrfToken"); }

    @Test
    public void checkHeader() {

        // Шаг 1. Получаем csrf-token
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://rapidapi.com/")
                // Требуется задать параметры (должны предоставить):
                .addQueryParam("randomKey", "randomValue")
                .build();
        Response responseWithCsrfToken = RestAssured.get("gateway/csrf");
        String header = String.valueOf(responseWithCsrfToken.getHeaders().get("Connection"));

        // Проверка соответствия значения Заголовка
        Assertions.assertEquals("Connection=keep-alive", header);
        logger.info("Заголовок Connection=keep-alive найден");
    }

    @Test
    public void checkBody() {
        // Шаг 1. Получаем csrf-token
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://rapidapi.com/")
                .build();
        Response responseWithCsrfToken = RestAssured.get("gateway/csrf");

        // Проверка соответствия значения Body
        Assertions.assertTrue(isContainStringToken(responseWithCsrfToken.getBody().prettyPrint()));
        logger.info("Body включает в себя строку csrfToken");
    }

}