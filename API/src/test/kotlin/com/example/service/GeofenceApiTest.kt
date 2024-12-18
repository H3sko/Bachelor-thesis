package com.example.service

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation

@TestMethodOrder(OrderAnnotation::class)
class GeofenceApiTest {

    companion object {
        private var userToken: String? = null

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
        }
    }

    @Test
    @Order(1)
    fun testGetDeviceGeofence404() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/geofence/device/6")

        Assertions.assertEquals(404, response.statusCode)
    }

    @Test
    @Order(2)
    fun testAddGeofence400() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .body("[{\"latitude\":40.1,\"longitude\":-74.1},{\"latitude\":40.3,\"longitude\":-74.3}]")
            .post("/geofence/device/6")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(3)
    fun testAddGeofence404() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .body("[{\"latitude\":40.1,\"longitude\":-74.1},{\"latitude\":40.3,\"longitude\":-74.3},{\"latitude\":40.5,\"longitude\":-74.5}]")
            .post("/geofence/device/666666")

        Assertions.assertEquals(404, response.statusCode)
    }

    @Test
    @Order(4)
    fun testAddGeofence401() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .body("[{\"latitude\":40.1,\"longitude\":-74.1},{\"latitude\":40.3,\"longitude\":-74.3},{\"latitude\":40.5,\"longitude\":-74.5}]")
            .post("/geofence/device/7")

        Assertions.assertEquals(401, response.statusCode)
    }

    @Test
    @Order(5)
    fun testAddGeofence200() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .body("[{\"latitude\":40.1,\"longitude\":-74.1},{\"latitude\":40.3,\"longitude\":-74.3},{\"latitude\":40.5,\"longitude\":-74.5}]")
            .post("/geofence/device/6")

        Assertions.assertEquals(200, response.statusCode)
    }

    @Test
    @Order(6)
    fun testGetDeviceGeofence400() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/geofence/device/666666")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(7)
    fun testGetDeviceGeofence401() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/geofence/device/7")

        Assertions.assertEquals(401, response.statusCode)
    }

    @Test
    @Order(8)
    fun testGetDeviceGeofence200() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .get("/geofence/device/6")

        Assertions.assertEquals(200, response.statusCode)
    }

    @Test
    @Order(9)
    fun testDeleteDeviceGeofence404() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .delete("/geofence/device/666666")

        Assertions.assertEquals(404, response.statusCode)
    }

    @Test
    @Order(10)
    fun testDeleteDeviceGeofence401() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .delete("/geofence/device/7")

        Assertions.assertEquals(401, response.statusCode)
    }

    @Test
    @Order(11)
    fun testDeleteDeviceGeofence200() {
        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", userToken)
            .delete("/geofence/device/6")

        Assertions.assertEquals(200, response.statusCode)
    }
}
