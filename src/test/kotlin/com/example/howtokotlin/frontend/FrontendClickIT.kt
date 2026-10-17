package com.example.howtokotlin.frontend

import com.example.howtokotlin.client.urlhaus.UrlHausClient
import com.example.howtokotlin.testutil.TestApplication
import com.microsoft.playwright.*
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.bean.override.mockito.MockitoBean

internal class FrontendClickIT : TestApplication() {

    @LocalServerPort
    private var port: Int = 0

    @MockitoBean
    private lateinit var urlHausClient: UrlHausClient

    @Test
    fun userCanAuthenticateBrowseForumCreatePostAndReply() {
        Playwright.create().use { playwright ->
            val browser: Browser = playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true))
            val context: BrowserContext = browser.newContext()
            val page: Page = context.newPage()

            val baseUrl = "http://localhost:$port"
            page.navigate(baseUrl)

            assertThat(page.locator("#auth-status")).hasText("Not logged in")

            page.locator("#navbar-login-btn").click()
            page.locator("#login-username-input").fill("admin")
            page.locator("#login-password-input").fill("superSecret1!")
            page.locator("#login-submit-btn").click()

            assertThat(page.locator("#auth-status")).hasText("admin")
            assertThat(page.locator("#user-role-badge")).hasText("ADMINISTRATOR")

            page.locator("#navbar-accounts-btn").click()
            assertThat(page.locator("h2")).containsText("User Accounts")
            page.locator("#breadcrumb-forum-home").click()

            page.locator("#create-category-btn").click()
            val categoryName = "Playwright Discussions " + System.currentTimeMillis()
            page.locator("#category-name-input").fill(categoryName)
            page.locator("#category-desc-input").fill("Category created during automated click test")
            page.locator("#category-submit-btn").click()

            val categoryCard: Locator = page.locator(".card", Page.LocatorOptions().setHasText(categoryName))
            assertThat(categoryCard).isVisible()
            categoryCard.click()

            assertThat(page.locator("#category-page-title")).hasText(categoryName)
            page.locator("#create-post-btn").click()
            val postTitle = "E2E Test Post " + System.currentTimeMillis()
            page.locator("#post-title-input").fill(postTitle)
            page.locator("#post-body-input").fill("This is the post content created by Playwright click test.")
            page.locator("#post-submit-btn").click()

            val postCard: Locator = page.locator(".card", Page.LocatorOptions().setHasText(postTitle))
            assertThat(postCard).isVisible()
            postCard.click()

            assertThat(page.locator("#post-detail-title")).hasText(postTitle)
            val replyContent = "Automated reply test from Playwright."
            page.locator("#reply-content-input").fill(replyContent)
            page.locator("#reply-submit-btn").click()

            assertThat(page.locator("#replies-container")).containsText(replyContent)

            page.locator("#navbar-logout-btn").click()
            assertThat(page.locator("#auth-status")).hasText("Not logged in")

            val newUsername = "user" + System.currentTimeMillis()
            page.locator("#navbar-login-btn").click()
            page.locator("#tab-signup-btn").click()
            page.locator("#signup-username-input").fill(newUsername)
            page.locator("#signup-email-input").fill("$newUsername@example.com")
            page.locator("#signup-firstname-input").fill("New")
            page.locator("#signup-lastname-input").fill("Member")
            page.locator("#signup-password-input").fill("superSecretPassword123!")
            page.locator("#signup-submit-btn").click()

            assertThat(page.locator("#auth-status")).hasText(newUsername)
            assertThat(page.locator("#user-role-badge")).hasText("USER")

            page.locator("#navbar-logout-btn").click()
            assertThat(page.locator("#auth-status")).hasText("Not logged in")
        }
    }
}
