package com.example.howtokotlin.exception

import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.id.Id

class NotFoundException : RuntimeException {
    constructor(id: Id<*>) : super(String.format(NOT_FOUND_MESSAGE, id))

    constructor(roleName: RoleName) : super(String.format(NOT_FOUND_MESSAGE, roleName))

    companion object {
        private const val NOT_FOUND_MESSAGE = "%s could not be found"
    }
}