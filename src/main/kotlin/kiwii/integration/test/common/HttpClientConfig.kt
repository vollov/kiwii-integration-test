package kiwii.integration.test.common

import org.apache.http.HeaderElementIterator
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeaderElementIterator
import org.apache.http.protocol.HTTP
import org.apache.http.protocol.HttpContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@Configuration
class HttpClientConfig {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    
    @Value("\${client.connection.total.max}")
    private val MAX_TOTAL_CONNECTIONS: Int = 10

    @Value("\${client.connection.route.max}")
    private val MAX_PER_ROUTE: Int = 2

    @Value("\${client.connection.alive.sec}")
    private val KEEP_ALIVE_IN_SECONDS: Long = 60

    // Determines the timeout in milliseconds until a connection is established.
    private val CONNECT_TIMEOUT = 30000

    // The timeout when requesting a connection from the connection manager in milliseconds.
    private val REQUEST_TIMEOUT = 30000

    // The timeout for waiting for data in milliseconds
    private val SOCKET_TIMEOUT = 60000

    private val CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30
    @Bean
    fun connectionManager(): PoolingHttpClientConnectionManager{
        val connManager = PoolingHttpClientConnectionManager()
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS)
        connManager.setDefaultMaxPerRoute(MAX_PER_ROUTE)
        return connManager
    }

    @Bean
    fun keepAliveStrategy(): ConnectionKeepAliveStrategy {
        return ConnectionKeepAliveStrategy { response: HttpResponse, context: HttpContext ->
            val it: HeaderElementIterator = BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE))
            while (it.hasNext()) {
                val he = it.nextElement()
                val param = he.name
                val value = he.value
                if (value != null && param.equals("timeout", ignoreCase = true)) {
                    return@ConnectionKeepAliveStrategy value.toLong() * 1000
                }
            }
            KEEP_ALIVE_IN_SECONDS * 1000
        }
    }

    @Bean
    fun httpClient(): CloseableHttpClient? {
        val requestConfig: RequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build()
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager())
                .setKeepAliveStrategy(keepAliveStrategy())
                .build()
    }

    @Bean
    fun idleConnectionMonitor(connectionManager: PoolingHttpClientConnectionManager?): Runnable? {
        return Runnable {
            try {
                if (connectionManager != null) {
                    logger.trace("run IdleConnectionMonitor - Closing expired and idle connections...")
                    connectionManager.closeExpiredConnections()
                    connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS.toLong(), TimeUnit.SECONDS)
                } else {
                    logger.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised")
                }
            } catch (e: Exception) {
                logger.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.message, e)
            }
        }
    }
}