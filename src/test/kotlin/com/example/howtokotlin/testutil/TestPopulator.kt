package com.example.howtokotlin.testutil

import com.github.anhem.testpopulator.PopulateFactory
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.PopulateConfig

object TestPopulator {

    private val populateConfig: PopulateConfig = PopulateConfig.builder()
        .kotlinSupport(true)
        .build()

    @PublishedApi
    internal val populateFactory = PopulateFactory(populateConfig)

    inline fun <reified T> populate(): T {
        return populateFactory.populate(T::class.java)
    }

    inline fun <reified T, reified U> populate(noinline overridePopulate: () -> U): T {
        return populateFactory.populate(T::class.java, U::class.java, overridePopulate)
    }

    inline fun <reified T> populate(name: String, clazz: Class<*>, noinline overridePopulate: () -> Any?): T {
        return populateFactory.populate(T::class.java, name, clazz, overridePopulate)
    }

    inline fun <reified T> populate(overrides: Map<*, OverridePopulate<*>>): T {
        return populateFactory.populate(T::class.java, overrides)
    }

}