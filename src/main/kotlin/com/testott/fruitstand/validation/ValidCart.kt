package com.testott.fruitstand.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [CartValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidCart(
    val message: String = "Invalid cart fruit names",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)