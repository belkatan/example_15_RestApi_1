package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RestApiTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    public void unsuccessfulLoginTest() {
        given().contentType(ContentType.JSON)
               .body("{ \"email\": \"peter@klaven\" }")
               .post("/api/login")
               .then()
               .statusCode(400)
               .body("error", is("Missing password"));
    }

    @Test
    public void successfulLoginTest() {
        given().contentType(ContentType.JSON)
                .body("{" +
                        "\"email\": \"eve.holt@reqres.in\",\n" +
                        "\"password\": \"cityslicka\"" +
                        "}")
                .post("/api/login")
                .then()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    public void successfulRegisterTest() {
        given().contentType(ContentType.JSON)
                .body("{" +
                        "\"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"pistol\"" +
                        "}")
                .post("/api/register")
                .then()
                .statusCode(200)
                .body("id", is(4))
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    public void deleteTest() {
        given().when()
                .delete("/api/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    public void updateTest() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        given().contentType(ContentType.JSON)
                .body("{" +
                        "\"name\": \"morpheus\",\n" +
                        "\"job\": \"zion resident\"" +
                        "}")
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .body("name", is("morpheus"))
                .body("job", is("zion resident"))
                .body("updatedAt", containsString(currentDate));
    }

    @Test
    public void listUserTest() {
        given().when()
                .get("/api/users?page=1")
                .then()
                .statusCode(200)
                .body("page", is(1))
                .body("per_page", is(6))
                .body("total", is(12))
                .body("total_pages", is(2))
                .body("data", not(empty()))
                .body("support.url", is("https://reqres.in/#support-heading"))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }




    @Test
    void unsuccessfulSingleUserTest() {
        Spec.request
                .when()
                .get("/users/50")
                .then()
                .log().body()
                .statusCode(404);
    }

    @Test
    void successfulListUserTest() {
        Spec.request
                .when()
                .get("/users?page=2")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.findAll{it.id = 7}.email.flatten()",
                        hasItem("michael.lawson@reqres.in"))
                .body("data.findAll{it.id = 7}.first_name.flatten()",
                        hasItem("Michael"))
                .body("data.findAll{it.id = 7}.last_name.flatten()",
                        hasItem("Lawson"))
                .body("data.findAll{it.id = 7}.avatar.flatten()",
                        hasItem("https://reqres.in/img/faces/7-image.jpg"));
    }

    @Test
    void successfulListResourcesTest() {
        Spec.request
                .when()
                .get("/unknown?page=2")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.findAll{it.id = 7}.name.flatten()",
                        hasItem("sand dollar"))
                .body("data.findAll{it.id = 10}.year.flatten()",
                        hasItem(2009))
                .body("data.findAll{it.year > 2010}.color.flatten()",
                        hasItem("#D94F70"))
                .body("data.findAll{it.color = '#BF1932'}.pantone_value.flatten()",
                        hasItem("15-5519"));
    }

    @Test
    void unsuccessfulRegisterTest() {
        Spec.request
                .body("{ \"email\": \"sydney@fife\" }")
                .when()
                .post("/register")
                .then()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));

    }



























}
