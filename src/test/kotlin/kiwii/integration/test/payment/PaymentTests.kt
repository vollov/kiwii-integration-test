package kiwii.integration.test.payment

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import kiwii.integration.test.common.JSONResponse
import kiwii.integration.test.common.RestClient
import kiwii.integration.test.helper.AuthHelper
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.apache.http.entity.StringEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaymentTests (@Autowired val authHelper: AuthHelper,
                    @Autowired val restClient: RestClient) {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${service.url}")
    private val serviceUrl: String = "service-url"

    fun buildAddress(isDefault: Boolean, type: String): JSONObject{
        var request = JSONObject()
        request.put("quantity", 2)
        request.put("firstName", "Gerry")
        request.put("lastName", "Chalkley")
        request.put("phone", "519-998-2061")
        request.put("street", "48 Steepleridge ST")
        request.put("city", "Kitchener")
        request.put("province", "ON")
        request.put("unit", "")
        request.put("postcode", "n2p2w3")
        request.put("type",type)
        request.put("default", isDefault)
        return request
    }

    @Test
    fun add_delete_billing_address() {
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")

        // add billing address
        var headers = restClient.getHeaders(token)

        var request = buildAddress(false, "B")
        logger.debug("add address request, json string=$request")
        val payload = StringEntity(request.toString())

        val addResponse: JSONResponse = restClient.post("$serviceUrl/payments/addresses", headers, payload)

        logger.debug("add address response, json={}", addResponse.json)
        Assertions.assertEquals(200, addResponse.httpStatus)

        // find billing address
        val findResponse: JSONResponse = restClient.get("$serviceUrl/payments/addresses/b", headers)
        logger.debug("find billing address response, json={}", findResponse.json)

        val findContext: DocumentContext = JsonPath.parse(findResponse.json)
        val type: String = findContext.read("$.type")
        val id: String = findContext.read("$._id")
        Assertions.assertEquals(200, findResponse.httpStatus)
        Assertions.assertEquals("B", type)
        Assertions.assertNotNull(id)

        // delete billing address
        val delResponse: JSONResponse = restClient.delete("$serviceUrl/payments/addresses/$id", headers)
        logger.debug("delete billing address response, json={}", delResponse.json)
        Assertions.assertEquals(200, delResponse.httpStatus)
    }

    @Test
    fun add_default_shipping_address(){
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")

        var headers  = restClient.getHeaders(token)

        // add shipping address
        var request = buildAddress(true, "S")
        logger.debug("add address request, json string=$request")
        val payload = StringEntity(request.toString())
        
        val addResponse: JSONResponse = restClient.post("$serviceUrl/payments/addresses", headers, payload)

        logger.debug("add address response, json={}", addResponse.json)
        Assertions.assertEquals(200, addResponse.httpStatus)

        // find shipping address
        val findResponse: JSONResponse = restClient.get("$serviceUrl/payments/addresses/s", headers)

        logger.debug("find shipping address response, json={}", findResponse.json)
        val findContext: DocumentContext = JsonPath.parse(findResponse.json)
        val type: String = findContext.read("$.type")
        Assertions.assertEquals(200, findResponse.httpStatus)
        Assertions.assertEquals("S", type)

    }

//    @Test
//    fun del_billing_address(){
//        val response: JSONResponse = authHelper.login()
//        logger.debug("login_success response, json={}", response.json)
//
//        val jsonContext: DocumentContext = JsonPath.parse(response.json)
//        val token: String = jsonContext.read("$.token")
//
//        var headers  = restClient.getHeaders(token)
//        val findResponse: JSONResponse = restClient.get("$serviceUrl/payments/addresses/b", headers)
//
//        logger.debug("find billing address response, json={}", findResponse.json)
//
//        val findContext: DocumentContext = JsonPath.parse(findResponse.json)
//        val type: String = findContext.read("$.type")
//        val id: String = findContext.read("$._id")
//        Assertions.assertEquals(200, findResponse.httpStatus)
//        Assertions.assertEquals("B", type)
//        Assertions.assertNotNull(id)
//
//        val delResponse: JSONResponse = restClient.delete("$serviceUrl/payments/addresses/$id", headers)
//        logger.debug("delete billing address response, json={}", delResponse.json)
//        Assertions.assertEquals(200, delResponse.httpStatus)
//    }

    @Test
    fun find_delete_shipping_address(){
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")

        var headers  = restClient.getHeaders(token)

        val findResponse: JSONResponse = restClient.get("$serviceUrl/payments/addresses/s", headers)

        logger.debug("find shipping address response, json={}", findResponse.json)
        val findContext: DocumentContext = JsonPath.parse(findResponse.json)
        val type: String = findContext.read("$.type")
        val id: String = findContext.read("$._id")
        Assertions.assertEquals(200, findResponse.httpStatus)
        Assertions.assertEquals("S", type)
        Assertions.assertNotNull(id)

        val delResponse: JSONResponse = restClient.delete("$serviceUrl/payments/addresses/$id", headers)
        logger.debug("delete billing address response, json={}", delResponse.json)
        Assertions.assertEquals(200, delResponse.httpStatus)
    }

    @Test
    fun find_default_address(){
        val response: JSONResponse = authHelper.login()
        logger.debug("login_success response, json={}", response.json)

        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val token: String = jsonContext.read("$.token")

        var headers  = restClient.getHeaders(token)
        val findResponse: JSONResponse = restClient.get("$serviceUrl/payments/addresses/d", headers)

        logger.debug("find default address response, json={}", findResponse.json)
        val findContext: DocumentContext = JsonPath.parse(findResponse.json)
        val isDefault: Boolean = findContext.read("$.default")
        Assertions.assertEquals(200, findResponse.httpStatus)
        Assertions.assertTrue(isDefault)
    }

//    @Test
//    fun update_del_address(){
//        val response: JSONResponse = authHelper.login()
//        logger.debug("login_success response, json={}", response.json)
//
//        val jsonContext: DocumentContext = JsonPath.parse(response.json)
//        val token: String = jsonContext.read("$.token")
//
//        var headers  = restClient.getHeaders(token)
//        val cartResponse: JSONResponse = restClient.get("$serviceUrl/orders/cart", headers)
//
//        val cartContext: DocumentContext = JsonPath.parse(cartResponse.json)
//        //val quantity: Int = jsonContext.read("$.BAT0001.quantity")
//
//        logger.debug("get_cart_orders response, json={}", cartResponse.json)
//        Assertions.assertEquals(200, cartResponse.httpStatus)
//        //Assertions.assertNotNull(quantity)
//    }
}