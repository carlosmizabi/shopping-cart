package org.carlos.cart.promotions.calculators

import org.carlos.cart.entities.*
import org.carlos.cart.promotions.calculators.generic.AmountOffPriceCalculator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AmountOffPriceCalculatorTest {

    private lateinit var calculator: AmountOffPriceCalculator

    @BeforeEach
    fun setup() {
        calculator = AmountOffPriceCalculator()
    }

    @Test
    fun `calculate promotion discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.2.asMoney,
        )
        val promotion = AmountOffPriceDiscount(.4.asMoney)
        assertEquals(
            PromotionDiscount(
                newPrice = 1.6.asMoney,
                originalPrice = 2.4.asMoney
            ),
            calculator.calculate(product, 2, promotion),
        )
    }

    @Test
    fun `when the product costs zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 0.0.asMoney,
        )
        val promotion = AmountOffPriceDiscount(.4.asMoney)
        assertEquals(
            null,
            calculator.calculate(product, 2, promotion),
        )
    }

    @Test
    fun `when the promotion discount is zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.2.asMoney,
        )
        val promotion = AmountOffPriceDiscount(0.asMoney)
        assertEquals(
            null,
            calculator.calculate(product, 2, promotion),
        )
    }

    @Test
    fun `when the product quantity is zero there is no discount`() {
        val product = ProductListing(
            product = Product(name = "water", category = listOf(Category("beverage"))),
            pricePerUnit = 1.2.asMoney,
        )
        val promotion = AmountOffPriceDiscount(.2.asMoney)
        assertEquals(
            null,
            calculator.calculate(product, 0, promotion),
        )
    }

}