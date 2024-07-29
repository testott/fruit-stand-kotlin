package com.testott.fruitstand.model

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Entity
data class Offer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "fruit_id")
    val fruit: Fruit,

    @ManyToOne
    @JoinColumn(name = "discount_id")
    val discount: Discount
)

@Repository
interface OfferRepository : JpaRepository<Offer, Long> {
    fun findByFruit(fruit: Fruit): Offer?
}