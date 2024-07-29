package com.testott.fruitstand.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class DiscountStrategyTest {

    @Test
    fun `getDiscountValue should return correct discount for BuyXGetYDiscountStrategy`() {
        // Arrange
        val strategy = BuyXGetYDiscountStrategy()
        val params = mapOf("buy" to 1, "get" to 1)
        val quantity = 2
        val cost = BigDecimal("0.60")

        // Act
        val result = strategy.getDiscountValue(quantity, cost, params)

        // Assert
        assertEquals(BigDecimal("0.60"), result)
    }

    @Test
    fun `getDiscountValue should return zero when quantity is less than buy`() {
        // Arrange
        val strategy = BuyXGetYDiscountStrategy()
        val params = mapOf("buy" to 2, "get" to 1)
        val quantity = 1
        val cost = BigDecimal("0.60")

        // Act
        val result = strategy.getDiscountValue(quantity, cost, params)

        // Assert
        assertEquals(BigDecimal("0.00"), result)
    }

    @Test
    fun `getDiscountValue should return correct discount for larger orders`() {
        // Arrange
        val strategy = BuyXGetYDiscountStrategy()
        val params = mapOf("buy" to 1, "get" to 1)
        val quantity = 33
        val cost = BigDecimal("0.60")

        // Act
        val result = strategy.getDiscountValue(quantity, cost, params)

        // Assert
        assertEquals(BigDecimal("9.60"), result)
    }
}