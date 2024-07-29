package com.testott.fruitstand.service

import com.ninjasquad.springmockk.MockkBean
import com.testott.fruitstand.controller.CartItem
import com.testott.fruitstand.model.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class OrderServiceTest {

    private lateinit var orderService: OrderService

    @MockkBean
    private lateinit var fruitRepository: FruitRepository

    @MockkBean
    private lateinit var discountService: DiscountService

    @MockkBean
    private lateinit var orderRepository: OrderRepository

    private val fruitApple = Fruit(
        name = "apple",
        value = BigDecimal("0.60"),
        offer = Offer(
            fruit = Fruit(
                name = "apple",
                value = BigDecimal("0.60")
            ),
            discount = Discount(
                discountType = DiscountType.BUY_GET_DISCOUNT,
                params = mapOf("buy" to 1, "get" to 1)
            )
        )
    )
    private val fruitOrange = Fruit(
        name = "orange",
        value = BigDecimal("0.25"),
        offer = Offer(
            fruit = Fruit(
                name = "orange",
                value = BigDecimal("0.25")
            ),
            discount = Discount(
                discountType = DiscountType.BUY_GET_DISCOUNT,
                params = mapOf("buy" to 2, "get" to 1)
            )
        )
    )

    @BeforeEach
    fun setup() {
        fruitRepository = mockk()
        discountService = mockk()
        orderRepository = mockk()
        orderService = OrderService(fruitRepository, discountService, orderRepository)

        // Mocking the repository responses
        every { fruitRepository.findByName("apple") } returns fruitApple
        every { fruitRepository.findByName("orange") } returns fruitOrange
    }

    @Test
    fun `generateInvoice should calculate correct total cost`() {
        // Arrange
        val cart = listOf(
            CartItem(name = "apple", quantity = 3),
            CartItem(name = "orange", quantity = 4)
        )

        // Mocking the discount service responses
        every { discountService.getDiscount(eq(fruitApple), 3) } returns BigDecimal("0.60")
        every { discountService.getDiscount(eq(fruitOrange), 4) } returns BigDecimal("0.25")

        // Act
        val invoice = orderService.generateInvoice(cart, 1)

        // Assert
        val expectedTotalCost = BigDecimal("1.95").toPlainString()
        val expectedTotalDiscount = BigDecimal("-0.85").toPlainString()
        assertEquals(expectedTotalCost, invoice.totalCost)
        assertEquals(expectedTotalDiscount, invoice.totalDiscount)

        val expectedLineItems = listOf(
            LineItem(name = "apple", quantity = 3, itemCost = "0.60", discount = "-0.60", lineCost = "1.20"),
            LineItem(name = "orange", quantity = 4, itemCost = "0.25", discount = "-0.25", lineCost = "0.75")
        )
        assertEquals(expectedLineItems, invoice.lineItems)

        // Verify that the discount service was called with the correct parameters
        verify { discountService.getDiscount(eq(fruitApple), eq(3)) }
        verify { discountService.getDiscount(eq(fruitOrange), eq(4)) }
    }

    @Test
    fun `generateInvoice should throw exception when fruit doesn't exist`() {
        // Arrange
        val cart = listOf(
            CartItem(name = "missingFruit", quantity = 1)
        )

        // Mocking the repository response for missing fruit
        every { fruitRepository.findByName("missingFruit") } returns null

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            orderService.generateInvoice(cart, 1)
        }
    }

    @Test
    fun `createOrder should save and return order with correct invoice`() {
        // Arrange
        val cartItems = listOf(CartItem("apple", 3), CartItem("orange", 4))
        val invoice = Invoice(1, emptyList(), "0.00", "0.00")
        val order = Order(1, totalAmount = BigDecimal("1.95"), invoice = invoice)

        every { orderRepository.save(any()) } returns order
        every { fruitRepository.findByName("apple") } returns fruitApple
        every { fruitRepository.findByName("orange") } returns fruitOrange
        every { discountService.getDiscount(any(), any()) } returns BigDecimal.ZERO

        // Act
        val result = orderService.createOrder(cartItems)

        // Assert
        assertEquals(order, result)
        verify { orderRepository.save(any()) }
    }

    @Test
    fun `getOrderById should return order with correct invoice`() {
        // Arrange
        val order = Order(1, totalAmount = BigDecimal("1.95"), invoice = Invoice(1, emptyList(), "0.00", "1.95"))

        every { orderRepository.findById(1) } returns java.util.Optional.of(order)

        // Act
        val result = orderService.getOrderById(1)

        // Assert
        assertEquals(order, result)
    }

    @Test
    fun `getAllOrders should return list of orders with correct invoices`() {
        // Arrange
        val orders = listOf(Order(1, totalAmount = BigDecimal("1.95"), invoice = Invoice(1, emptyList(), "0.00", "1.95")))

        every { orderRepository.findAll() } returns orders

        // Act
        val result = orderService.getAllOrders()

        // Assert
        assertEquals(orders, result)
    }
}