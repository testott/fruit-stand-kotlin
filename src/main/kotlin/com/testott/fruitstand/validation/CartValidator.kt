package com.testott.fruitstand.validation

import com.testott.fruitstand.controller.CartItem
import com.testott.fruitstand.model.FruitRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CartValidator : ConstraintValidator<ValidCart, List<CartItem>> {

    @Autowired
    private lateinit var fruitRepository: FruitRepository

    override fun isValid(cartItems: List<CartItem>?, context: ConstraintValidatorContext): Boolean {
        if (cartItems == null) {
            return false
        }

        val validFruitNames = fruitRepository.findAll().map { it.name }.toSet()
        return cartItems.all { it.name in validFruitNames }
    }
}
