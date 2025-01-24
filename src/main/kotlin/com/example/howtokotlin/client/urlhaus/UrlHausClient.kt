package com.example.howtokotlin.client.urlhaus

import com.example.howtokotlin.client.model.UrlCheckResponse
import com.example.howtokotlin.configuration.HowtoConfig
import com.example.howtokotlin.exception.ValidationException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class UrlHausClient(
    howtoConfig: HowtoConfig,
    private val urlHausRestTemplate: RestTemplate,
) {
    private val urlHausConfig: HowtoConfig.UrlHausConfig = howtoConfig.urlHaus

    fun checkForMaliciousUrls(urls: Set<String?>): Boolean {
        if (urls.size > urlHausConfig.maxAllowedUrls) {
            throw ValidationException(java.lang.String.format(TOO_MANY_URLS, urls.size, urlHausConfig.maxAllowedUrls))
        }
        return urls.stream().anyMatch { url: String? -> this.checkForMaliciousUrl(url) }
    }

    private fun checkForMaliciousUrl(url: String?): Boolean {
        val httpEntity = createRequest(url)
        val urlCheckResponse: UrlCheckResponse? =
            urlHausRestTemplate.postForObject(
                java.lang.String.format("%s/%s", urlHausConfig.baseUrl, "/v1/url/"),
                httpEntity,
                UrlCheckResponse::class.java,
            )

        return isUrlMalicious(urlCheckResponse)
    }

    companion object {
        const val TOO_MANY_URLS: String = "Too many (%d) urls provided. Maximum allowed is %d"

        private fun createRequest(url: String?): HttpEntity<MultiValueMap<String, String>> {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

            val map: MultiValueMap<String, String> = LinkedMultiValueMap()
            map.add("url", url)

            return HttpEntity(map, headers)
        }

        private fun isUrlMalicious(urlCheckResponse: UrlCheckResponse?): Boolean {
            if (urlCheckResponse == null) {
                return true
            }
            return "online" == urlCheckResponse.urlStatus
        }
    }
}
