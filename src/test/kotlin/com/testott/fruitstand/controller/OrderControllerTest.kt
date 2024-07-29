package com.testott.fruitstand.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.testott.fruitstand.model.Fruit
import com.testott.fruitstand.model.FruitRepository
import com.testott.fruitstand.model.Order
import com.testott.fruitstand.service.Invoice
import com.testott.fruitstand.service.LineItem
import com.testott.fruitstand.service.OrderService
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.math.BigDecimal
import java.time.LocalDateTime

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
        val emptyCartPayload = """
            {
                "cart": []
            }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyCartPayload)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `createOrder should return 400 when given invalid cart line items`() {
        // Arrange
        val invalidCartPayload = """
            {
                "cart": [
                    {"name": "apple", "quantity": -1},
                    {"name": "orange", "quantity": 0}
                ]
            }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCartPayload)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `createOrder should return 400 when given invalid cart item name`() {
        // Arrange
        val invalidCartItemNamePayload = """
            {
                "cart": [
                    {"name": "", "quantity": 1}
                ]
            }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCartItemNamePayload)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `createOrder should return 200 when given valid payload`() {
        // Arrange
        val validPayload = """
            {
                "cart": [
                    {"name": "apple", "quantity": 3},
                    {"name": "orange", "quantity": 4}
                ]
            }
        """.trimIndent()

        val mockInvoice = Invoice(
            1,
            listOf(
                LineItem("apple", 3, "0.60", "0.00", "1.80"),
                LineItem("orange", 4, "0.25", "0.00", "1.00")
            ),
            "0.00",
            "2.80"
        )

        every { orderService.createOrder(any()) } returns Order(
            id = 1,
            orderDate = LocalDateTime.of(2022, 2, 2, 2, 2, 2),
            totalAmount = BigDecimal("2.80"),
            invoice = mockInvoice
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.orderId").value(1))
    }

    @Test
    fun `getOrderById should return 200 when given valid ID`() {
        // Arrange
        val orderId = 1L
        val mockOrder = Order(
            id = orderId,
            orderDate = LocalDateTime.of(2022, 2, 2, 2, 2, 2),
            totalAmount = BigDecimal("2.80"),
            invoice = Invoice(
                1,
                listOf(
                    LineItem("apple", 3, "0.60", "0.00", "1.80"),
                    LineItem("orange", 4, "0.25", "0.00", "1.00")
                ),
                "0.00",
                "2.80"
            )
        )

        every { orderService.getOrderById(orderId) } returns mockOrder

        // Act & Assert
        mockMvc.perform(
            get("/api/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderId))
    }

    @Test
    fun `getOrderById should return 400 when given invalid ID`() {
        // Act & Assert
        mockMvc.perform(
            get("/api/orders/{id}", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `getOrderById should return 404 when order not found`() {
        // Arrange
        val orderId = 999999999999999999L
        every { orderService.getOrderById(orderId) } returns null

        // Act & Assert
        mockMvc.perform(
            get("/api/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `getAllOrders should return 200 with list of orders`() {
        // Arrange
        val mockOrders = listOf(
            Order(
                id = 1,
                orderDate = LocalDateTime.of(2022, 2, 2, 2, 2, 2),
                totalAmount = BigDecimal("2.80"),
                invoice = Invoice(
                    1,
                    listOf(
                        LineItem("apple", 3, "0.60", "0.00", "1.80"),
                        LineItem("orange", 4, "0.25", "0.00", "1.00")
                    ),
                    "0.00",
                    "2.80"
                )
            ),
            Order(
                id = 2,
                orderDate = LocalDateTime.of(2022, 2, 3, 2, 2, 2),
                totalAmount = BigDecimal("3.50"),
                invoice = Invoice(
                    2,
                    listOf(
                        LineItem("banana", 5, "0.50", "0.00", "2.50"),
                        LineItem("grape", 2, "0.50", "0.00", "1.00")
                    ),
                    "0.00",
                    "3.50"
                )
            )
        )

        every { orderService.getAllOrders() } returns mockOrders

        // Act & Assert
        mockMvc.perform(
            get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(mockOrders.size))
    }
}