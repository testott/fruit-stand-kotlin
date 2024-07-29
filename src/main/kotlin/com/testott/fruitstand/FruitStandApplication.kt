package com.testott.fruitstand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FruitStandApplication

fun main(args: Array<String>) {
	runApplication<FruitStandApplication>(*args)
}
