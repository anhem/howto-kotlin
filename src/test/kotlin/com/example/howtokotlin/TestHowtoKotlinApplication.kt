package com.example.howtokotlin

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<HowtoKotlinApplication>().with(TestcontainersConfiguration::class).run(*args)
}
