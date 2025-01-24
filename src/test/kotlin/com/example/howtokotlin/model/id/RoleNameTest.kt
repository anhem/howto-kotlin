package com.example.howtokotlin.model.id

import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.RoleName.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RoleNameTest {
    @Test
    fun allRoleNameValuesShouldMatchEnumNameAndPrefix() {
        assertThat(RoleName.entries.toTypedArray()).allMatch { roleName -> String.format("ROLE_%s", roleName.name) == roleName.role }
    }

    @Test
    fun fromNameReturnsRoleName() {
        assertThat(RoleName.fromName(ADMINISTRATOR.name)).isEqualTo(ADMINISTRATOR)
    }

    @Test
    fun fromNameThrowsExceptionWhenInvalidRoleName() {
        Assertions.assertThatThrownBy { RoleName.fromName("invalidRoleName") }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun fromRoleReturnsRoleName() {
        assertThat(RoleName.fromRole(ADMINISTRATOR.role)).isEqualTo(ADMINISTRATOR)
    }

    @Test
    fun fromRoleThrowsExceptionWhenInvalidRoleName() {
        Assertions.assertThatThrownBy { RoleName.fromRole("invalidRole") }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun valueReturnsEnumName() {
        assertThat(ADMINISTRATOR.value).isEqualTo(ADMINISTRATOR.name)
        assertThat(MODERATOR.value).isEqualTo(MODERATOR.name)
        assertThat(USER.value).isEqualTo(USER.name)
    }
}
