package com.testott.fruitstand.service

import com.testott.fruitstand.controller.CartItem
import com.testott.fruitstand.model.FruitRepository
import com.testott.fruitstand.model.Order
import com.testott.fruitstand.model.OrderRepository
import java.math.BigDecimal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class LineItem(
    val name: String,
    val quantity: Int,
    val itemCost: String,
    val discount: String,
    val lineCost: String
) {
    // No-argument constructor for Jackson
    constructor() : this("", 0, "", "", "")
}

data class Invoice(
    val orderId: Long,
    val lineItems: List<LineItem>,
    val totalDiscount: String,
    val totalCost: String
) {
    // No-argument constructor for Jackson
    constructor() : this(0, emptyList(), "", "")
}

@Service
class OrderService(
    private val fruitRepository: FruitRepository,
    private val discountService: DiscountService,
    private val orderRepository: OrderRepository
) {

    @Transactional
    fun createOrder(cartItems: List<CartItem>): Order {
        // Initial order object to get the generated id
        val order = Order(
            totalAmount = BigDecimal.ZERO,
            invoice = Invoice()
        )
        val savedOrder = orderRepository.save(order)

        val invoice = generateInvoice(cartItems, savedOrder.id)
        val totalAmount = BigDecimal(invoice.totalCost)

        val updatedOrder = savedOrder.copy(
            totalAmount = totalAmount,
            invoice = invoice
        )

        return orderRepository.save(updatedOrder)
    }

    fun getOrderById(id: Long): Order? {
        return orderRepository.findById(id).orElse(null)
    }

    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    fun generateInvoice(cartItems: List<CartItem>, orderId: Long): Invoice {
        val lineItems = mutableListOf<LineItem>()
        var totalCost = BigDecimal.ZERO
        var totalDiscount = BigDecimal.ZERO

        for (cartItem in cartItems) {
            val fruit = fruitRepository.findByName(cartItem.name)
                ?: throw IllegalArgumentException("Fruit ${cartItem.name} not found")

            val costPerItem = fruit.value
            val totalLineCost = costPerItem.multiply(BigDecimal(cartItem.quantity))

            val discount = fruit.offer?.let {
                discountService.getDiscount(fruit, cartItem.quantity)
            } ?: BigDecimal.ZERO

            val finalCost = totalLineCost.subtract(discount)

            lineItems.add(
                LineItem(
                    name = fruit.name,
                    quantity = cartItem.quantity,
                    itemCost = costPerItem.toPlainString(),
                    discount = discount.negate().toPlainString(),
                    lineCost = finalCost.toPlainString()
                )
            )

            totalCost = totalCost.add(finalCost)
            totalDiscount = totalDiscount.add(discount)
        }

        return Invoice(
            orderId = orderId,
            lineItems = lineItems,
            totalDiscount = totalDiscount.negate().toPlainString(),
            totalCost = totalCost.toPlainString()
        )
    }
}