package com.testott.fruitstand.controller

import com.testott.fruitstand.service.OrderService
import com.testott.fruitstand.model.FruitRepository
import com.testott.fruitstand.model.Fruit
import com.testott.fruitstand.service.Invoice
import com.testott.fruitstand.service.LineItem
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(OrderController::class)
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var orderService: OrderService

    @MockkBean
    private lateinit var fruitRepository: FruitRepository

    @BeforeEach
    fun setup() {
        every { fruitRepository.findAll() } returns listOf(
            Fruit(name = "apple", value = BigDecimal("0.60")),
            Fruit(name = "orange", value = BigDecimal("0.25"))
        )
    }

    @Test
    fun `createOrder should return 400 when given empty cart`() {
        // Arrange
        val invalidPayload = """
            {
                "cart": []
            }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `createOrder should return 400 when given invalid cart line items`() {
        // Arrange
        val invalidPayload = """
            {
                "cart": [
                    {
                        "name": "",
                        "quantity": 0
                    }
                ]
            }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `createOrder should return 400 when given invalid cart item name`() {
        // Arrange
        val invalidPayload = """
            {
                "cart": [
                    {
                        "name": "invalidFruit",
                        "quantity": 2
                    }
                ]
            }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `createOrder should return 200 when given valid payload`() {
        // Arrange
        val validPayload = """
            {
                "cart": [
                    {
                        "name": "apple",
                        "quantity": 2
                    }
                ]
            }
        """.trimIndent()

        val mockInvoice = Invoice(
            lineItems = listOf(
                LineItem(name = "apple", quantity = 2, itemCost = "0.60", discount = "0.00", lineCost = "1.20")
            ),
            totalDiscount = "0.00",
            totalCost = "1.20"
        )

        every { orderService.generateInvoice(any()) } returns mockInvoice

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload)
        ).andExpect(status().isOk)
    }
}