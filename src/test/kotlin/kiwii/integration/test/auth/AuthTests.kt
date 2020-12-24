package kiwii.integration.test.auth

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import kiwii.integration.test.common.JSONResponse
import kiwii.integration.test.helper.AuthHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AuthTests(@Autowired val authHelper: AuthHelper){

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun login_success(){
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")
        Assertions.assertEquals(200, response.httpStatus)
        Assertions.assertNotNull(token)
    }
}