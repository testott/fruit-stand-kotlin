package com.testott.fruitstand.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Entity
data class Fruit(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val name: String,
    val value: BigDecimal
)

@Repository
interface FruitRepository : JpaRepository<Fruit, Long> {
    fun findByName(name: String): Fruit?
}
