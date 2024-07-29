package com.testott.fruitstand.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class RepositoryTest {

    @Autowired
    private lateinit var offerRepository: OfferRepository

    @Autowired
    private lateinit var discountRepository: DiscountRepository

    @Autowired
    private lateinit var fruitRepository: FruitRepository

    @Test
    fun `findByFruit should return offer when fruit exists`() {
        // Arrange
        val fruit = Fruit(name = "apple", value = BigDecimal("0.60"))
        fruitRepository.save(fruit)
        val discount = Discount(discountType = DiscountType.BUY_GET_DISCOUNT, params = mapOf("buy" to 1, "get" to 1))
        discountRepository.save(discount)
        val offer = Offer(fruit = fruit, discount = discount)
        offerRepository.save(offer)

        // Act
        val result = offerRepository.findByFruit(fruit)

        // Assert
        assertEquals(offer, result)
    }

    @Test
    fun `findByFruit should return null when fruit does not exist`() {
        // Arrange
        val fruit = Fruit(name = "missingFruit", value = BigDecimal("0.50"))
        fruitRepository.save(fruit)

        // Act
        val result = offerRepository.findByFruit(fruit)

        // Assert
        assertNull(result)
    }

    @Test
    fun `findAll should return all correct fruit values`() {
        // Arrange
        val apple = Fruit(name = "apple", value = BigDecimal("0.60"))
        val orange = Fruit(name = "orange", value = BigDecimal("0.25"))
        fruitRepository.saveAll(listOf(apple, orange))

        // Act
        val fruits = fruitRepository.findAll()

        // Assert
        assertEquals(2, fruits.size)
        assertEquals("apple", fruits[0].name)
        assertEquals("0.60", fruits[0].value.toString())
        assertEquals("orange", fruits[1].name)
        assertEquals("0.25", fruits[1].value.toString())
    }
}