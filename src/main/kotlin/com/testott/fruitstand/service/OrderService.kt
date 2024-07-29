package com.testott.fruitstand.service

import com.testott.fruitstand.controller.CartItem
import com.testott.fruitstand.model.FruitRepository
import java.math.BigDecimal
import org.springframework.stereotype.Service

data class LineItem(
    val name: String,
    val quantity: Int,
    val itemCost: String,
    val discount: String,
    val lineCost: String
)

data class Invoice(
    val lineItems: List<LineItem>,
    val totalDiscount: String,
    val totalCost: String
)

@Service
class OrderService(
    private val fruitRepository: FruitRepository,
    private val discountService: DiscountService
) {

    fun generateInvoice(cartItems: List<CartItem>): Invoice {
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
            lineItems = lineItems,
            totalDiscount = totalDiscount.negate().toPlainString(),
            totalCost = totalCost.toPlainString()
        )
    }
}