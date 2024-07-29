package com.testott.fruitstand.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.testott.fruitstand.service.Invoice
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class InvoiceConverter : AttributeConverter<Invoice, String> {
    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: Invoice): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String): Invoice {
        return objectMapper.readValue(dbData, Invoice::class.java)
    }
}