package com.testott.fruitstand.controller

import com.testott.fruitstand.service.Invoice
import com.testott.fruitstand.service.OrderService
import com.testott.fruitstand.validation.ValidCart
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class OrderRequest(
    @field:NotEmpty(message = "Cart must not be empty")
    @field:Valid
    @field:ValidCart
    val cart: List<@Valid CartItem>
)

data class CartItem(
    @field:NotBlank(message = "Fruit name must not be blank")
    val name: String,

    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(@RequestBody @Valid orderRequest: OrderRequest): ResponseEntity<Invoice> {
        val invoice = orderService.generateInvoice(orderRequest.cart)

        return ResponseEntity.ok(invoice)
    }
}
