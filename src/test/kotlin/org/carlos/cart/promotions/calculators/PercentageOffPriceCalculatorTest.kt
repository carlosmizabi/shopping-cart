package org.carlos.cart.promotions.calculators

import org.carlos.cart.entities.*
import org.carlos.cart.promotions.calculators.generic.PercentageOffPriceCalculator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PercentageOffPriceCalculatorTest {

    private lateinit var calculator: PercentageOffPriceCalculator

    @BeforeEach
    fun setup() {
        calculator = PercentageOffPriceCalculator()
    }

    @Test
    fun `calculate promotion discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.3.asMoney,
        )
        val promotion = PercentageOffPriceDiscount(Percentage(.3))
        assertEquals(
            PromotionDiscount(
                newPrice = 2.73.asMoney,
                originalPrice = 3.9.asMoney
            ),
            calculator.calculate(product, 3, promotion),
        )
    }

    @Test
    fun `when the product costs zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 0.0.asMoney,
        )
        val promotion = PercentageOffPriceDiscount(Percentage(.4))
        assertEquals(
            null,
            calculator.calculate(product, 2, promotion),
        )
    }

    @Test
    fun `when the promotion percentage is zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.2.asMoney,
        )
        val promotion = PercentageOffPriceDiscount(Percentage(.0))
        assertEquals(
            null,
            calculator.calculate(product, 2, promotion),
        )
    }

    @Test
    fun `when the quantity of items is zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.2.asMoney,
        )
        val promotion = PercentageOffPriceDiscount(Percentage(.2))
        assertEquals(
            null,
            calculator.calculate(product, 0, promotion),
        )
    }


    @Test
    fun `when the promotion discount is zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.2.asMoney,
        )
        val promotion = PercentageOffPriceDiscount(Percentage(0.0))
        assertEquals(
            null,
            calculator.calculate(product, 2, promotion),
        )
    }
}