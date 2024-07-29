package com.testott.fruitstand.service

import com.testott.fruitstand.controller.CartItem
import com.testott.fruitstand.model.Fruit
import com.testott.fruitstand.model.FruitRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.math.BigDecimal
import kotlin.test.assertEquals

class OrderServiceTest {

    private lateinit var orderService: OrderService
    private lateinit var fruitRepository: FruitRepository

    @BeforeEach
    fun setup() {
        fruitRepository = Mockito.mock(FruitRepository::class.java)
        orderService = OrderService(fruitRepository)

        // Mocking the repository responses
        Mockito.`when`(fruitRepository.findByName("apple"))
            .thenReturn(Fruit(name = "apple", value = BigDecimal("0.60")))
        Mockito.`when`(fruitRepository.findByName("orange"))
            .thenReturn(Fruit(name = "orange", value = BigDecimal("0.25")))
    }

    @Test
    fun `generateInvoice should calculate correct total cost`() {
        // Arrange
        val cart = listOf(
            CartItem(name = "apple", quantity = 2),
            CartItem(name = "orange", quantity = 3)
        )

        // Act
        val invoice = orderService.generateInvoice(cart)

        // Assert
        val expectedTotalCost = BigDecimal("1.95").toPlainString()
        assertEquals(expectedTotalCost, invoice.totalCost)

        val expectedLineItems = listOf(
            LineItem(name = "apple", quantity = 2, itemCost = "0.60", lineCost = "1.20"),
            LineItem(name = "orange", quantity = 3, itemCost = "0.25", lineCost = "0.75")
        )
        assertEquals(expectedLineItems, invoice.lineItems)
    }

    @Test
    fun `generateInvoice should throw exception when fruit doesn't exist`() {
        // Arrange
        val cart = listOf(
            CartItem(name = "missingFruit", quantity = 1)
        )

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            orderService.generateInvoice(cart)
        }
    }
}
