package com.example.service

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OnlineUsersApiTest {

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
    fun testAddToken400() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"token\": \"\", \"activeNotification\": false}")
            .post("/onlineUser/add")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(2)
    fun testAddToken201() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"token\": \"abcde\", \"activeNotification\": false}")
            .post("/onlineUser/add")

        Assertions.assertEquals(201, response.statusCode)
    }

    @Test
    @Order(3)
    fun testAddToken200() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .body("{\"token\": \"edcba\", \"activeNotification\": false}")
            .post("/onlineUser/add")

        Assertions.assertEquals(200, response.statusCode)
    }

    @Test
    @Order(4)
    fun testRemoveToken200() {
        val response: Response = RestAssured.given()
            .header("Authorization", userToken)
            .contentType(ContentType.JSON)
            .delete("/onlineUser/remove")

        Assertions.assertEquals(200, response.statusCode)
    }
}