package com.testott.fruitstand.service

import com.testott.fruitstand.model.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class DiscountServiceTest {

    private lateinit var discountService: DiscountService
    private val offerRepository: OfferRepository = mockk()

    @BeforeEach
    fun setup() {
        discountService = DiscountService(offerRepository)
    }

    @Test
    fun `getDiscount should return correct discount given discountType`() {
        // Arrange
        val fruit = Fruit(name = "apple", value = BigDecimal("0.60"))
        val discount = Discount(discountType = DiscountType.BUY_GET_DISCOUNT, params = mapOf("buy" to 1, "get" to 1))
        val offer = Offer(fruit = fruit, discount = discount)

        every { offerRepository.findByFruit(fruit) } returns offer

        // Act
        val result = discountService.getDiscount(fruit, 2)

        // Assert
        assertEquals(BigDecimal("0.60"), result)
    }

    @Test
    fun `getDiscount should return zero when no offer is found`() {
        // Arrange
        val fruit = Fruit(name = "missingFruit", value = BigDecimal("0.50"))

        every { offerRepository.findByFruit(fruit) } returns null

        // Act
        val result = discountService.getDiscount(fruit, 2)

        // Assert
        assertEquals(BigDecimal.ZERO, result)
    }
}