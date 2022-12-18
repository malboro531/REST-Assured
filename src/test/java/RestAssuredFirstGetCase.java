import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RestAssuredFirstGetCase {

    private Logger logger = LogManager.getLogger(RestAssuredSecondPostCase.class);

    @Test
    public void httpRequestTest() {
        // RestAssured.requestSpecification - статическое поле, хранящее спецификацию запроса
        // RequestSpecBuilder - класс билдер, для создания спецификации запроса
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://apislist.com/")
                .build();
        // Response - класс, хранящий ответ запроса
        Response response = RestAssured.request("GET");
        // Вывод ответа
        response.prettyPrint();
        // Проверка кода статуса ответа
        response.then().statusCode(200);
        logger.info("Код ответа запроса сайта https://apislist.com/ соответсвует запрашиваемому");
    }

    @Test
    public void checkHeader() {

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://apislist.com/")
                // Требуется задать параметры (должны предоставить):
                .addQueryParam("randomKey", "randomValue")
                .build();
        Response response = RestAssured.post("search/randomSearch");
        String header = String.valueOf(response.getHeaders().get("content-type"));

        // Проверка соответствия значения Заголовка
        Assertions.assertEquals("Content-Type=text/html; charset=UTF-8", header);
        logger.info("Заголовок Content-Type=text/html; charset=UTF-8 найден");
    }

    @Test
    public void checkBody() {

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://apislist.com/")
                .build();
        Response response = RestAssured.get("");

        // Проверка соответствия значения Body
        Assertions.assertTrue(response.getBody().asPrettyString().contains(
                "meta name=\"author\" content=\"APIsList\""));
        logger.info("Body содержит запрашиваемую строку");
    }

}
