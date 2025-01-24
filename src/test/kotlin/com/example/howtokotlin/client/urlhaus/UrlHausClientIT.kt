package com.example.howtokotlin.client.urlhaus

import com.example.howtokotlin.client.urlhaus.UrlHausClient.Companion.TOO_MANY_URLS
import com.example.howtokotlin.exception.ValidationException
import com.example.howtokotlin.testutil.TestApplication
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import java.net.URI

internal class UrlHausClientIT : TestApplication() {
    @Value("classpath:/urlhaus/malicious-url-response.json")
    private lateinit var maliciousUrlResponse: Resource

    @Value("classpath:/urlhaus/ok-url-response.json")
    private lateinit var okUrlResponse: Resource

    @Autowired
    private lateinit var urlHausRestTemplate: RestTemplate

    @Autowired
    private lateinit var urlHausClient: UrlHausClient
    private lateinit var mockRestServiceServer: MockRestServiceServer
    private lateinit var uri: URI

    @BeforeEach
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer.createServer(urlHausRestTemplate)
        uri = URI("http://localhost:8080/v1/url/")
    }

    @Test
    fun checkForMaliciousUrlsReturnsFalseWhenUrlIsMalicious() {
        setupUrlHausResponse(maliciousUrlResponse)

        assertThat(urlHausClient.checkForMaliciousUrls(setOf(URL))).isTrue()
    }

    @Test
    fun checkForMaliciousUrlsReturnsTrueWhenUrlIsOk() {
        setupUrlHausResponse(okUrlResponse)

        assertThat(urlHausClient.checkForMaliciousUrls(setOf(URL))).isFalse()
    }

    @Test
    fun exceptionIsThrownWhenMaxAllowedUrlsHasBeenPassed() {
        Assertions
            .assertThatThrownBy {
                urlHausClient.checkForMaliciousUrls(
                    setOf(
                        URL,
                        "https://home.local/",
                    ),
                )
            }.isInstanceOf(ValidationException::class.java)
            .hasMessageContaining(String.format(TOO_MANY_URLS, 2, 1))
    }

    private fun setupUrlHausResponse(response: Resource) {
        mockRestServiceServer
            .expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(uri))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.content().string(URL_ENCODED))
            .andRespond(
                MockRestResponseCreators
                    .withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response),
            )
    }

    companion object {
        private const val URL = "https://local.home.arpa/"
        private const val URL_ENCODED = "url=https%3A%2F%2Flocal.home.arpa%2F"
    }
}
