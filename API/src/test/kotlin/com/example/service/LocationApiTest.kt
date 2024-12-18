package com.example.service

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation

@TestMethodOrder(OrderAnnotation::class)
class LocationApiTest {

    companion object {
        private var userToken: String? = null
        private var adminToken: String? = null
        private var locationId1: Int = 0
        private var locationId2: Int  = 0

        @BeforeAll
        @JvmStatic
        fun setup() {
            RestAssured.baseURI = "http://127.0.0.1:8080"

            val userLoginResponse: Response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"user\", \"password\": \"user\"}")
                .post("/user/login")

            Assertions.assertEquals(200, userLoginResponse.statusCode())
            Assertions.assertNotNull(userLoginResponse.jsonPath().getString("token"))
            userToken = "Bearer " + userLoginResponse.jsonPath().getString("token")


            val adminLoginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"admin\", \"password\": \"admin\"}")
                .post("/user/login")

            Assertions.assertEquals(200, adminLoginResponse.statusCode())
            Assertions.assertNotNull(adminLoginResponse.jsonPath().getString("token"))
            adminToken = "Bearer " + adminLoginResponse.jsonPath().getString("token")
        }
    }

    @Test
    @Order(1)
    fun testGetLatestLocation404() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/latest/device/6666666")

        Assertions.assertEquals(404, response.statusCode)
    }

    @Test
    @Order(2)
    fun testGetLatestLocation401() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/latest/device/7")

        Assertions.assertEquals(401, response.statusCode)
    }

    @Test
    @Order(3)
    fun testGetLatestLocation409() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/latest/device/6")

        Assertions.assertEquals(409, response.statusCode)
    }

    @Test
    @Order(4)
    fun testAddFirstLocation201() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminToken)
            .body("{\"deviceId\": 6, \"latitude\": 49.210060, \"longitude\": 16.599250, \"timestamp\": \"2024-10-10T08:00:00\"}")
            .post("/location")

        Assertions.assertEquals(201, response.statusCode)
        Assertions.assertNotNull(response.jsonPath().getInt(""))
        locationId1 = response.jsonPath().getInt("")
    }

    @Test
    @Order(5)
    fun testFirstGetLatestLocation200() {
        val expectedResponse = "{\"latitude\":49.21006,\"longitude\":16.59925,\"timestamp\":\"2024-10-10T08:00\"}"

        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/latest/device/6")

        Assertions.assertEquals(200, response.statusCode)

        val actualResponse = response.body.asString()
        Assertions.assertEquals(expectedResponse, actualResponse)
    }

    @Test
    @Order(6)
    fun testAddSecondLocation201() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminToken)
            .body("{\"deviceId\": 6, \"latitude\": 49.210800, \"longitude\": 16.598400, \"timestamp\": \"2024-10-10T09:30:00\"}")
            .post("/location")

        Assertions.assertEquals(201, response.statusCode)
        Assertions.assertNotNull(response.jsonPath().getInt(""))
        locationId2 = response.jsonPath().getInt("")
    }

    @Test
    @Order(7)
    fun testSecondGetLatestLocation200() {
        val expectedResponse = "{\"latitude\":49.2108,\"longitude\":16.5984,\"timestamp\":\"2024-10-10T09:30\"}"

        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/latest/device/6")

        Assertions.assertEquals(200, response.statusCode)

        val actualResponse = response.body.asString()
        Assertions.assertEquals(expectedResponse, actualResponse)
    }

    @Test
    @Order(8)
    fun testGetAll400() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/all/device/666666")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(9)
    fun testGetAll401() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/all/device/7")

        Assertions.assertEquals(401, response.statusCode)
    }

    @Test
    @Order(10)
    fun testGetAll404() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/all/device/")

        Assertions.assertEquals(404, response.statusCode)
    }

    @Test
    @Order(11)
    fun testGetAll200() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/all/device/6")

        Assertions.assertEquals(200, response.statusCode)
    }

    @Test
    @Order(12)
    fun testDeleteFirstLocation200() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminToken)
            .delete("/location/$locationId1")

        Assertions.assertEquals(200, response.statusCode)
    }

    @Test
    @Order(13)
    fun testDeleteSecondLocation200() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminToken)
            .delete("/location/$locationId2")

        Assertions.assertEquals(200, response.statusCode)
    }

    @Test
    @Order(14)
    fun testGetAll409() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/location/all/device/6")

        Assertions.assertEquals(409, response.statusCode)
    }
}
