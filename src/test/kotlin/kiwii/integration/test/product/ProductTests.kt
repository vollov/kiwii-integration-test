package kiwii.integration.test.product

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import kiwii.integration.test.common.JSONResponse
import kiwii.integration.test.common.RequestHelper
import kiwii.integration.test.common.RestClient
import kiwii.integration.test.helper.AuthHelper
import org.apache.http.Header
import org.apache.http.message.BasicHeader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductTests(@Autowired val authHelper: AuthHelper,
                   @Autowired val restClient: RestClient) {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${service.url}")
    private val serviceUrl: String = "service-url"

    @Test
    fun find_by_sku_success() {
        var headers  = mutableListOf<Header>().toTypedArray()
        val response: JSONResponse = restClient.get("$serviceUrl/products/BAT0001", headers)

        logger.debug("find_by_sku_success response, json={}", response.json)
        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val imageUrl: String = jsonContext.read("$.imageUrl")
        val price: Double = jsonContext.read("$.price")
        Assertions.assertEquals(200, response.httpStatus)
        Assertions.assertNotNull(imageUrl)
        Assertions.assertNotNull(price)
    }

    @Test
    fun list_success() {
        var headers  = mutableListOf<Header>().toTypedArray()
        val response: JSONResponse = restClient.get("$serviceUrl/products", headers)

        logger.debug("list product success response, json={}", response.json)
        val jsonContext: DocumentContext = JsonPath.parse(response.json)
        val imageUrl: String = jsonContext.read("$.[0].imageUrl")
        Assertions.assertEquals(200, response.httpStatus)
        Assertions.assertNotNull(imageUrl)
    }
}