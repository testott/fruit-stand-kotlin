package com.testott.fruitstand

import com.testott.fruitstand.model.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@Configuration
class DataInitializer {

    @Bean
    fun initDatabase(
        fruitRepository: FruitRepository,
        discountRepository: DiscountRepository,
        offerRepository: OfferRepository
    ) = CommandLineRunner {

        val fruits = fruitRepository.saveAll(
            listOf(
                Fruit(name = "apple", value = BigDecimal("0.60")),
                Fruit(name = "orange", value = BigDecimal("0.25"))
            )
        )

        val appleDiscount = discountRepository.save(
            Discount(
                discountType = DiscountType.BUY_GET_DISCOUNT,
                params = mapOf("buy" to 1, "get" to 1)
            )
        )

        val orangeDiscount = discountRepository.save(
            Discount(
                discountType = DiscountType.BUY_GET_DISCOUNT,
                params = mapOf("buy" to 2, "get" to 1)
            )
        )

        offerRepository.saveAll(
            listOf(
                Offer(fruit = fruits[0], discount = appleDiscount),
                Offer(fruit = fruits[1], discount = orangeDiscount)
            )
        )
    }
}
