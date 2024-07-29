package com.testott.fruitstand

import com.testott.fruitstand.model.Fruit
import com.testott.fruitstand.model.FruitRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@Configuration
class DataInitializer {

    @Bean
    fun initDatabase(fruitRepository: FruitRepository) = CommandLineRunner {
        fruitRepository.saveAll(
            listOf(
                Fruit(name = "apple", value = BigDecimal("0.60")),
                Fruit(name = "orange", value = BigDecimal("0.25"))
            )
        )
    }
}
