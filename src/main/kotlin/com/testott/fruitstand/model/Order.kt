package com.testott.fruitstand.model

import com.testott.fruitstand.service.Invoice
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val orderDate: LocalDateTime = LocalDateTime.now(),

    val totalAmount: BigDecimal,

    @Lob
    @Column(name = "invoice", columnDefinition = "TEXT")
    @Convert(converter = InvoiceConverter::class)
    val invoice: Invoice
)

@Repository
interface OrderRepository : JpaRepository<Order, Long>