package com.testott.fruitstand.service

import com.testott.fruitstand.controller.CartItem
import com.testott.fruitstand.model.FruitRepository
import java.math.BigDecimal
import org.springframework.stereotype.Service

data class LineItem(
    val name: String,
    val quantity: Int,
    val itemCost: String,
    val lineCost: String
)

data class Invoice(
    val lineItems: List<LineItem>,
    val totalCost: String
)

@Service
class OrderService(private val fruitRepository: FruitRepository) {

    fun generateInvoice(cartItems: List<CartItem>): Invoice {
        val lineItems = mutableListOf<LineItem>()
        var totalCost = BigDecimal.ZERO

        for (cartItem in cartItems) {
            val fruit = fruitRepository.findByName(cartItem.name)
                ?: throw IllegalArgumentException("Fruit ${cartItem.name} not found")

            val costPerItem = fruit.value
            val totalLineCost = costPerItem.multiply(BigDecimal(cartItem.quantity))

            lineItems.add(
                LineItem(
                    name = fruit.name,
                    quantity = cartItem.quantity,
                    itemCost = costPerItem.toPlainString(),
                    lineCost = totalLineCost.toPlainString()
                )
            )

            totalCost = totalCost.add(totalLineCost)
        }

        return Invoice(
            lineItems = lineItems,
            totalCost = totalCost.toPlainString()
        )
    }
}
