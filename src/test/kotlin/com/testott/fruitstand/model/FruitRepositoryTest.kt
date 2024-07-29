package com.testott.fruitstand.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal

@DataJpaTest
class FruitRepositoryTest {

    @Autowired
    private lateinit var fruitRepository: FruitRepository

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
