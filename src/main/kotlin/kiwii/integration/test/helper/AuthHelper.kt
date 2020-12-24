package kiwii.integration.test.helper

import kiwii.integration.test.common.JSONResponse
import kiwii.integration.test.common.RestClient
import net.minidev.json.JSONObject
import org.apache.http.Header
import org.apache.http.entity.StringEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthHelper (@Autowired val restClient: RestClient){

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${service.url}")
    private val serviceUrl: String = "service-url"

    @Value("\${auth.firstName}")
    private val firstName: String = "Gerry"

    @Value("\${auth.lastName}")
    private val lastName: String = "Chalkley"

    @Value("\${auth.email}")
    private val email: String = "gc@abc.com"

    fun login(): JSONResponse {
        var list  = mutableListOf<Header>()
        // list.add(BasicHeader("Authorization", "Bearer XXYYZZ"))
        var headers  = list.toTypedArray()
        var request = JSONObject()
        request.put("firstName", firstName)
        request.put("lastName", lastName)
        request.put("email", email)
        val payload = StringEntity(request.toString());
        logger.debug("login_success() payload=$payload, url=$serviceUrl/auth/login")

        return restClient.post("$serviceUrl/auth/login", headers, payload)
    }
}