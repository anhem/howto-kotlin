package com.example.howtokotlin.testutil

import com.github.anhem.testpopulator.PopulateFactory
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.PopulateConfig

object TestPopulator {

    // 1. Define the configuration
    private val populateConfig: PopulateConfig = PopulateConfig.builder()
        .kotlinSupport(true)
        .build()

    // 2. Create the factory
    // Use @PublishedApi internal so inline functions can access it
    @PublishedApi
    internal val populateFactory = PopulateFactory(populateConfig)

    // 3. Create idiomatic Kotlin helper methods using 'inline' and 'reified'

    // Basic usage: populate<MyClass>()
    inline fun <reified T> populate(): T {
        return populateFactory.populate(T::class.java)
    }

    // With a single class override: populate<MyClass, String> { "local-value" }
    inline fun <reified T, reified U> populate(noinline overridePopulate: () -> U): T {
        return populateFactory.populate(T::class.java, U::class.java, overridePopulate)
    }

    // With a single name and type override: populate<MyClass>("email", String::class.java) { "test@example.com" }
    inline fun <reified T> populate(name: String, clazz: Class<*>, noinline overridePopulate: () -> Any?): T {
        return populateFactory.populate(T::class.java, name, clazz, overridePopulate)
    }

    // With multiple overrides using a Map: populate<MyClass>(mapOf(String::class.java to { "local-value" }))
    inline fun <reified T> populate(overrides: Map<*, OverridePopulate<*>>): T {
        return populateFactory.populate(T::class.java, overrides)
    }

}