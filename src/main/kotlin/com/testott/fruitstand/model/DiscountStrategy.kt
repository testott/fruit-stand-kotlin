package com.testott.fruitstand.model

import java.math.BigDecimal
import java.math.RoundingMode

abstract class DiscountStrategy {

    abstract fun getDiscountValue(quantity: Int, cost: BigDecimal, params: Map<String, Any>): BigDecimal

}

class BuyXGetYDiscountStrategy : DiscountStrategy() {

    override fun getDiscountValue(quantity: Int, cost: BigDecimal, params: Map<String, Any>): BigDecimal {
        val target = params["buy"] as Int
        val discount = params["get"] as Int
        val discountMultiplier = BigDecimal(quantity).divide(BigDecimal(target + discount), 0, RoundingMode.FLOOR)

        return discountMultiplier.multiply(cost).multiply(BigDecimal(discount))
    }

}