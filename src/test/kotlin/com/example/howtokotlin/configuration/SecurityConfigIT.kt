package com.example.howtokotlin.configuration

import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.testutil.TestApplication
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.pattern.PathPattern
import java.util.function.Consumer
import java.util.stream.Collectors

internal class SecurityConfigIT : TestApplication() {
    companion object {
        private val GET_URL_WHITELIST = listOf("/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/swagger-config", "/api/hello-world")
        private val PATCH_URL_WHITELIST = listOf<String>()
        private val POST_URL_WHITELIST = listOf("/api/auth/authenticate")
        private val PUT_URL_WHITELIST = listOf<String>()
        private val DELETE_URL_WHITELIST = listOf<String>()
        const val VALIDATION_ERROR_MESSAGE: String = "%s on %s does not return %s. Either this url should be protected or it should be added to %s"
        const val FAILED_VALIDATION_ERROR_MESSAGE: String = "%s on %s failed to validate: %s"
    }

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    @Test
    fun endpointsShouldRespondWithForbiddenWhenNotLoggedIn() {
        val failedEndpoints: MutableList<String> = ArrayList()
        requestMappingHandlerMapping.handlerMethods.forEach { (requestMappingInfo: RequestMappingInfo, _: HandlerMethod) ->
            val patterns = requestMappingInfo.pathPatternsCondition!!.patterns
            val methods = requestMappingInfo.methodsCondition.methods
            patterns.forEach(
                Consumer { pathPattern: PathPattern ->
                    methods.forEach(assertEndpointForbidden(pathPattern, failedEndpoints))
                },
            )
        }
        if (failedEndpoints.isNotEmpty()) {
            fail<Any>(failedEndpoints.stream().collect(Collectors.joining(System.lineSeparator())))
        }
    }

    private fun assertEndpointForbidden(
        pathPattern: PathPattern,
        failedEndpoints: MutableList<String>,
    ): Consumer<RequestMethod> =
        Consumer<RequestMethod> { requestMethod: RequestMethod ->
            try {
                when (requestMethod.name) {
                    "GET" -> assertGetForbidden(pathPattern, failedEndpoints)
                    "PATCH" -> assertPatchForbidden(pathPattern, failedEndpoints)
                    "POST" -> assertPostForbidden(pathPattern, failedEndpoints)
                    "PUT" -> assertPutForbidden(pathPattern, failedEndpoints)
                    "DELETE" -> assertDeleteForbidden(pathPattern, failedEndpoints)
                    else -> fail<Any>(String.format("%s not implemented", requestMethod.name))
                }
            } catch (e: Exception) {
                val failed = String.format(FAILED_VALIDATION_ERROR_MESSAGE, requestMethod.name, pathPattern.patternString, e.message)
                failedEndpoints.add(failed)
                log.error(failed, e)
            }
        }

    private fun assertGetForbidden(
        pathPattern: PathPattern,
        failedEndpoints: MutableList<String>,
    ) {
        val url = pathPattern.patternString
        if (!GET_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.getForEntity(cleanUrl(url), Any::class.java).statusCode !== HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "GET", url, HttpStatus.FORBIDDEN, "GET_URL_WHITELIST"))
            }
        } else {
            log.info("GET on {} is whitelisted, skipping", url)
        }
    }

    private fun assertPatchForbidden(
        pathPattern: PathPattern,
        failedEndpoints: MutableList<String>,
    ) {
        val url = pathPattern.patternString
        if (!PATCH_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.exchange(cleanUrl(url), HttpMethod.PATCH, null, Any::class.java).statusCode !==
                HttpStatus.FORBIDDEN
            ) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "PATCH", url, HttpStatus.FORBIDDEN, "PATCH_URL_WHITELIST"))
            }
        } else {
            log.info("PATCH on {} is whitelisted, skipping", url)
        }
    }

    private fun assertPostForbidden(
        pathPattern: PathPattern,
        failedEndpoints: MutableList<String>,
    ) {
        val url = pathPattern.patternString
        if (!POST_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.postForEntity(cleanUrl(url), MessageDTO.OK, Any::class.java).statusCode !== HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "POST", url, HttpStatus.FORBIDDEN, "POST_URL_WHITELIST"))
            }
        } else {
            log.info("POST on {} is whitelisted, skipping", url)
        }
    }

    private fun assertPutForbidden(
        pathPattern: PathPattern,
        failedEndpoints: MutableList<String>,
    ) {
        val url = pathPattern.patternString
        if (!PUT_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.exchange(cleanUrl(url), HttpMethod.PUT, null, Any::class.java).statusCode !== HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "PUT", url, HttpStatus.FORBIDDEN, "PUT_URL_WHITELIST"))
            }
        } else {
            log.info("PUT on {} is whitelisted, skipping", url)
        }
    }

    private fun assertDeleteForbidden(
        pathPattern: PathPattern,
        failedEndpoints: MutableList<String>,
    ) {
        val url = pathPattern.patternString
        if (!DELETE_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.exchange(cleanUrl(url), HttpMethod.DELETE, null, Any::class.java).statusCode !==
                HttpStatus.FORBIDDEN
            ) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "DELETE", url, HttpStatus.FORBIDDEN, "DELETE_URL_WHITELIST"))
            }
        } else {
            log.info("DELETE on {} is whitelisted, skipping", url)
        }
    }

    private fun cleanUrl(url: String): String =
        url
            .replace("{", "")
            .replace("}", "")
}
