package com.testott.fruitstand.model

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

enum class DiscountType {
    BUY_GET_DISCOUNT
}

@Entity
data class Discount(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    val discountType: DiscountType,

    @Type(JsonType::class)
    @Column(columnDefinition = "json")
    val params: Map<String, Any>
)

@Repository
interface DiscountRepository : JpaRepository<Discount, Long>