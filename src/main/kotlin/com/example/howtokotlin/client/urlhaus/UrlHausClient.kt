package com.example.howtokotlin.client.urlhaus

import com.example.howtokotlin.client.model.UrlCheckResponse
import com.example.howtokotlin.configuration.HowtoConfig
import com.example.howtokotlin.exception.ValidationException
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient

@Component
class UrlHausClient(
    howtoConfig: HowtoConfig,
    private val urlHausRestClient: RestClient,
) {
    private val urlHausConfig: HowtoConfig.UrlHausConfig = howtoConfig.urlHaus

    fun checkForMaliciousUrls(urls: Set<String?>): Boolean {
        if (urls.size > urlHausConfig.maxAllowedUrls) {
            throw ValidationException(String.format(TOO_MANY_URLS, urls.size, urlHausConfig.maxAllowedUrls))
        }
        return urls.stream().anyMatch { url: String? -> this.checkForMaliciousUrl(url) }
    }

    private fun checkForMaliciousUrl(url: String?): Boolean {
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("url", url)

        val urlCheckResponse: UrlCheckResponse? =
            urlHausRestClient.post()
                .uri(String.format("%s/%s", urlHausConfig.baseUrl, "/v1/url/"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .body(UrlCheckResponse::class.java)

        return isUrlMalicious(urlCheckResponse)
    }

    companion object {
        const val TOO_MANY_URLS: String = "Too many (%d) urls provided. Maximum allowed is %d"

        private fun isUrlMalicious(urlCheckResponse: UrlCheckResponse?): Boolean {
            if (urlCheckResponse == null) {
                return true
            }
            return "online" == urlCheckResponse.urlStatus
        }
    }
}
