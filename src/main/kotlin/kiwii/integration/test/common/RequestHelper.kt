package kiwii.integration.test.common

import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import java.io.IOException
import java.util.ArrayList

@Component
class RequestHelper(@Autowired val client: CloseableHttpClient) {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${header.source_system_id}")
    private val sourceSystemId: String = "CLIENT_MOBILE"

    @Value("\${header.application}")
    private val application: String = "Test"

    @Value("\${header.language}")
    private val language: String = "English"

    @Value("\${header.device_id}")
    private val deviceId: String = "FF380ADDC9142"

    @Value("\${header.originating_ip}")
    private val ip: String = "10.247.43.242"

    @Value("\${header.device_type}")
    private val deviceType: String = "iphone"

    @Value("\${header.carrier}")
    private val carrier: String = "fido"

    @Value("\${header.latitude}")
    private val latitude: Double=43.466667

    @Value("\${header.longitude}")
    private val longitude: Double = -80.516670



    fun getHeaders(): Array<Header> {
        var headers  = mutableListOf<Header>()
        headers.add(BasicHeader("Content-Type", Constants.APPLICATION_JSON))
//        headers.add(BasicHeader("source_system_id", sourceSystemId))
//        headers.add(BasicHeader("application", application))
//        headers.add(BasicHeader("language", language))
//        //Note: disable device_id here to avoid security questions
//        headers.add(BasicHeader("device_id", deviceId))
//        headers.add(BasicHeader("originating_ip", ip))
//        headers.add(BasicHeader("device_type", deviceType))
//        headers.add(BasicHeader("carrier", carrier))
//        headers.add(BasicHeader("geoLocation_latitude", latitude.toString()))
//        headers.add(BasicHeader("geoLocation_longitude", longitude.toString()))
        return headers.toTypedArray()
    }

    fun nextHeader(response: JSONResponse): Array<Header>{
        if(response.token == null){
            return getHeaders()
        } else {
            var headers: MutableList<Header> = getHeaders().toCollection(ArrayList())
            headers.add(BasicHeader("Authorization", "Bearer ${response.token}"))
            return headers.toTypedArray()
        }
    }

    fun getFormHeaders(): Array<Header> {
        var headers  = mutableListOf<Header>()
        headers.add(BasicHeader("source_system_id", sourceSystemId))
        headers.add(BasicHeader("application", application))
        headers.add(BasicHeader("language", language))
        //Note: disable device_id here to avoid security questions
        headers.add(BasicHeader("device_id", deviceId))
        headers.add(BasicHeader("originating_ip", ip))
        headers.add(BasicHeader("device_type", deviceType))
        headers.add(BasicHeader("carrier", carrier))
        headers.add(BasicHeader("geoLocation_latitude", latitude.toString()))
        headers.add(BasicHeader("geoLocation_longitude", longitude.toString()))
        return headers.toTypedArray()
    }

    fun convertReponse(response: HttpResponse): JSONResponse{
        val newToken = response.getFirstHeader("Authorization")?.value
        val json: String = EntityUtils.toString(response.getEntity(), "UTF-8")
        val statusCode: Int = response.getStatusLine().getStatusCode()
        return JSONResponse(json, statusCode, newToken)
    }

    fun postForm(url: String, headers: Array<Header>, payload: UrlEncodedFormEntity): HttpResponse {
        val client: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(url)

        httpPost.setHeaders(headers)
        httpPost.setEntity(payload)
        try {
            val response: HttpResponse  = client.execute(httpPost)
            return response
        } catch (e: IOException){
            logger.error("postForm request failed, error={}", e.localizedMessage)
            throw RestClientException(ErrorCodes.REST_REQUEST_ERROR)
        } finally{
            // DO NOT close socket!!
            //client.close()
        }
    }

    fun post(url: String, headers: Array<Header>, payload: StringEntity): HttpResponse {

        val httpPost = HttpPost(url)

        httpPost.setHeaders(headers)
        httpPost.setEntity(payload)
        try {
            val response: HttpResponse  = client.execute(httpPost)
            return response
        } catch (e: IOException){
            logger.error("post request failed, error={}", e.localizedMessage)
            throw RestClientException(ErrorCodes.REST_REQUEST_ERROR)
        } finally{
            // DO NOT close socket!!
            //client.close()
        }
    }

    fun put(url: String, headers: Array<Header>, payload: StringEntity): HttpResponse {

        val httpPut = HttpPut(url)

        httpPut.setHeaders(headers)
        httpPut.setEntity(payload)
        try {
            val response: HttpResponse  = client.execute(httpPut)
            return response
        } catch (e: IOException){
            logger.error("put request failed, error={}", e.localizedMessage)
            throw RestClientException(ErrorCodes.REST_REQUEST_ERROR)
        } finally{
            // DO NOT close socket!!
            //client.close()
        }
    }

    fun get(url: String, headers: Array<Header>): HttpResponse {

        val httpGet = HttpGet(url)
        httpGet.setHeaders(headers)
        try {
            val response: HttpResponse  = client.execute(httpGet)
            return response
        } catch (e: IOException){
            logger.error("get request failed, error={}", e.localizedMessage)
            throw RestClientException(ErrorCodes.REST_REQUEST_ERROR)
        }
    }

    fun delete(url: String, headers: Array<Header>): HttpResponse {

        val httpDelete = HttpDelete(url)
        httpDelete.setHeaders(headers)
        try {
            val response: HttpResponse  = client.execute(httpDelete)
            return response
        } catch (e: IOException){
            logger.error("delete request failed, error={}", e.localizedMessage)
            throw RestClientException(ErrorCodes.REST_REQUEST_ERROR)
        }
    }
}