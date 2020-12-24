package kiwii.integration.test.order

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import kiwii.integration.test.common.Constants
import kiwii.integration.test.common.JSONResponse
import kiwii.integration.test.common.RestClient
import kiwii.integration.test.helper.AuthHelper
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.apache.http.Header
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderTests (@Autowired val authHelper: AuthHelper,
                  @Autowired val restClient: RestClient) {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${service.url}")
    private val serviceUrl: String = "service-url"

    @Test
    fun get_cart_orders(){
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")

        var headers  = restClient.getHeaders(token)
        val cartResponse: JSONResponse = restClient.get("$serviceUrl/orders/cart", headers)

        logger.debug("get_cart_orders response, json={}", cartResponse.json)
        Assertions.assertEquals(200, cartResponse.httpStatus)
    }

    @Test
    fun place_order(){
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")

        var request = JSONArray()
        var bat = JSONObject()
        bat.put("sku", "BAT0001")
        bat.put("quantity", 2)
        request.add(bat)
        logger.debug("get_cart_orders request, json string=$request")
        val payload = StringEntity(request.toString())

        var headers  = restClient.getHeaders(token)
        val cartResponse: JSONResponse = restClient.post("$serviceUrl/orders/cart", headers, payload)

        logger.debug("get_cart_orders response, json=$cartResponse.json")
        Assertions.assertEquals(200, cartResponse.httpStatus)
    }
}