package com.example.service

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.*
import kotlin.test.assertNotNull

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserApiTest {
    companion object {
        private var userToken: String? = null
        private var adminToken: String? = null
        private var registeredUserId = 0

        @JvmStatic
        @BeforeAll
        fun setup() {
            RestAssured.baseURI = "http://127.0.0.1:8080"

            val adminLoginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"admin\", \"password\": \"admin\"}")
                .`when`()
                .post("/user/login")
                .then()
                .statusCode(200)
                .extract()
                .response()
            adminToken = "Bearer " + adminLoginResponse.jsonPath().getString("token")
            assertNotNull(adminToken)
        }
    }


    @Test
    @Order(1)
    fun testFirstRegisterUser400() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"user2\", \"password\": \"\"}")
            .post("/user/register")

        Assertions.assertEquals(400, response.statusCode)
    }


    @Test
    @Order(2)
    fun testSecondRegisterUser400() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"\", \"password\": \"user2\"}")
            .post("/user/register")

        Assertions.assertEquals(400, response.statusCode)
    }


    @Test
    @Order(3)
    fun testRegisterUser201() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"user2\", \"password\": \"user2\"}")
            .post("/user/register")

        Assertions.assertEquals(201, response.statusCode)
        Assertions.assertNotNull(response.jsonPath())
        registeredUserId = response.jsonPath().getInt("")
    }

    @Test
    @Order(4)
    fun testRegisterUser409() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"user2\", \"password\": \"user2\"}")
            .post("/user/register")

        Assertions.assertEquals(409, response.statusCode)
    }

    @Test
    @Order(5)
    fun testFirstLoginUser400() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"user2\", \"password\": \"\"}")
            .post("/user/login")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(6)
    fun testSecondLoginUser400() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"\", \"password\": \"user2\"}")
            .post("/user/login")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(7)
    fun testThirdLoginUser400() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"\", \"password\": \"\"}")
            .post("/user/login")

        Assertions.assertEquals(400, response.statusCode)
    }

    @Test
    @Order(8)
    fun testLoginUser200() {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"user2\", \"password\": \"user2\"}")
            .post("/user/login")

        Assertions.assertEquals(200, response.statusCode)
        Assertions.assertNotNull(response.jsonPath().getString("token"))
        userToken = "Bearer " + response.jsonPath().getString("token")

    }

    @Test
    @Order(9)
    fun testAdminGetAllUsers() {
        val response = RestAssured.given()
            .header("Authorization", adminToken)
            .contentType(ContentType.JSON)
            .get("/user/getAll")

        Assertions.assertEquals(302, response.statusCode)
    }

    @Test
    @Order(10)
    fun testAdminDeleteUser() {
        val response = RestAssured.given()
            .header("Authorization", adminToken)
            .contentType(ContentType.JSON)
            .delete("/user/$registeredUserId")

        Assertions.assertEquals(200, response.statusCode)
    }
}
