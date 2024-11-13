package com.example.service

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DeviceApiTest {

    companion object {
        private var userToken: String? = null
        private var deviceId: Int = 0

        @BeforeAll
        @JvmStatic
        fun setup() {
            RestAssured.baseURI = "http://127.0.0.1:8080"

            val response: Response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"user\", \"password\": \"user\"}")
                .post("/user/login")

            Assertions.assertEquals(200, response.statusCode())
            Assertions.assertNotNull(response.jsonPath().getString("token"))
            userToken = "Bearer " + response.jsonPath().getString("token")
        }
    }

    @Test
    @Order(1)
    fun testFirstAddDevice400() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"name\": \"Michal’s Airtag\",\"owner\": \"\"}")
            .post("/device/add")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(2)
    fun testSecondAddDevice400() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"name\": \"\",\"owner\": \"owner@localhost\"}")
            .post("/device/add")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(3)
    fun testAddDevice200() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"name\": \"Michal’s Airtag\",\"owner\": \"owner@localhost\"}")
            .post("/device/add")

        Assertions.assertEquals(200, response.statusCode)
        Assertions.assertNotNull(response.jsonPath().getInt(""))
        deviceId = response.jsonPath().getInt("")
    }

    @Test
    @Order(4)
    fun testAddDevice409() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"name\": \"Michal’s Airtag\",\"owner\": \"owner@localhost\"}")
            .post("/device/add")

        Assertions.assertEquals(409, response.statusCode)
    }

    @Test
    @Order(5)
    fun testGetUserDevices() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .get("/device/getUserDevices")

        Assertions.assertEquals(200, response.statusCode)

        val deviceNames = response.jsonPath().getList<String>("name")
        Assertions.assertTrue(deviceNames.contains("Michal’s Airtag"))
    }

    @Test
    @Order(6)
    fun testRemoveDevice() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .delete("/device/remove/$deviceId")

        Assertions.assertEquals(200, response.statusCode)
    }
}
