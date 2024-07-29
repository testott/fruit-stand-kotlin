package com.testott.fruitstand.model

import com.testott.fruitstand.service.Invoice
import com.testott.fruitstand.service.LineItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class InvoiceConverterTest {

    private val objectMapper = jacksonObjectMapper()
    private val invoiceConverter = InvoiceConverter()

    @Test
    fun `convertToDatabaseColumn should return correct JSON string`() {
        // Arrange
        val invoice = Invoice(
            orderId = 1,
            lineItems = listOf(
                LineItem("apple", 3, "0.60", "0.00", "1.80"),
                LineItem("orange", 4, "0.25", "0.00", "1.00")
            ),
            totalDiscount = "0.00",
            totalCost = "2.80"
        )

        // Act
        val json = invoiceConverter.convertToDatabaseColumn(invoice)

        // Assert
        val expectedJson = objectMapper.writeValueAsString(invoice)
        assertEquals(expectedJson, json)
    }

    @Test
    fun `convertToEntityAttribute should return correct Invoice`() {
        // Arrange
        val json = """
            {
                "orderId": 1,
                "lineItems": [
                    {"name": "apple", "quantity": 3, "itemCost": "0.60", "discount": "0.00", "lineCost": "1.80"},
                    {"name": "orange", "quantity": 4, "itemCost": "0.25", "discount": "0.00", "lineCost": "1.00"}
                ],
                "totalDiscount": "0.00",
                "totalCost": "2.80"
            }
        """.trimIndent()

        // Act
        val invoice = invoiceConverter.convertToEntityAttribute(json)

        // Assert
        val expectedInvoice = objectMapper.readValue<Invoice>(json)
        assertEquals(expectedInvoice, invoice)
    }
}