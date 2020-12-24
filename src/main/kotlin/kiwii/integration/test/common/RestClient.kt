package kiwii.integration.test.common

import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RestClient(@Autowired val client: RequestHelper) {

    fun getHeaders(token: String): Array<Header> {
        var headers = mutableListOf<Header>()
        headers.add(BasicHeader("Content-Type", Constants.APPLICATION_JSON))
        headers.add(BasicHeader("Authorization", "Bearer $token"))
        return headers.toTypedArray()
    }

    fun get(url: String, headers: Array<Header>): JSONResponse {
        val response: HttpResponse = client.get(url, headers)
        return client.convertReponse(response)
    }

    fun delete(url: String, headers: Array<Header>): JSONResponse {
        val response: HttpResponse = client.delete(url, headers)
        return client.convertReponse(response)
    }

    fun put(url: String, headers: Array<Header>, payload: StringEntity): JSONResponse{
        val response: HttpResponse = client.put(url, headers, payload)
        return client.convertReponse(response)
    }

    fun post(url: String, headers: Array<Header>, payload: StringEntity): JSONResponse {
        val response: HttpResponse = client.post(url, headers, payload)
        return client.convertReponse(response)
    }
}