package com.testott.fruitstand.service

import com.testott.fruitstand.model.*
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DiscountService(private val offerRepository: OfferRepository) {

    fun getDiscount(fruit: Fruit, quantity: Int): BigDecimal {
        val offer = offerRepository.findByFruit(fruit) ?: return BigDecimal.ZERO

        val discount = when (offer.discount.discountType) {
            DiscountType.BUY_GET_DISCOUNT -> BuyXGetYDiscountStrategy()
        }

        return discount.getDiscountValue(quantity, fruit.value, offer.discount.params)
    }
}