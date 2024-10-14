package org.carlos.cart.promotions.calculators

import org.carlos.cart.entities.*
import org.carlos.cart.promotions.calculators.products.BuySomeGetSomeFreeCalculator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BuySomeGetSomeFreeCalculatorTest {
    private lateinit var calculator: BuySomeGetSomeFreeCalculator

    @BeforeEach
    fun setup() {
        calculator = BuySomeGetSomeFreeCalculator()
    }

    @Test
    fun `calculate promotion discount`() {
        validPromotionTestCases.forEach { case ->
            val product = ProductListing(
                product = Product(name = "water", category = listOf(Category("beverage"))),
                pricePerUnit = case.pricePerUnit,
            )
            assertEquals(
                case.discount,
                calculator.calculate(product, case.quantity, case.promotion),
                case.description
            )
        }
    }

    @Test
    fun `reject promotion discount`() {
        invalidPromotionTestCases.forEach { case ->
            val product = ProductListing(
                product = Product(name = "water", category = listOf(Category("beverage"))),
                pricePerUnit = case.pricePerUnit,
            )
            assertEquals(
                null,
                calculator.calculate(product, case.quantity, case.promotion),
                case.description
            )
        }
    }

    private data class TestCase(
        val description: String,
        val promotion: BuySomeGetSomeFreePromotion,
        val discount: PromotionDiscount,
        val pricePerUnit: Money,
        val quantity: Int,
    )

    private data class InvalidPromotionTestCase(
        val description: String,
        val promotion: BuySomeGetSomeFreePromotion,
        val pricePerUnit: Money,
        val quantity: Int,
    )

    companion object {
        private val validPromotionTestCases = listOf(
            TestCase(
                description = "Buy 1000 get 1000 free",
                BuySomeGetSomeFreePromotion(buy = 1000, free = 1000),
                PromotionDiscount(
                    newPrice = 0.asMoney,
                    originalPrice = 2500.asMoney
                ),
                quantity = 1000,
                pricePerUnit = 2.5.asMoney,
            ),
            TestCase(
                description = "Buy 10 get 2 free",
                BuySomeGetSomeFreePromotion(buy = 10, free = 2),
                PromotionDiscount(
                    newPrice = 9.6.asMoney,
                    originalPrice = 12.asMoney
                ),
                quantity = 10,
                pricePerUnit = 1.2.asMoney,
            ),
            TestCase(
                description = "Buy 8 get 3 free",
                BuySomeGetSomeFreePromotion(buy = 8, free = 3),
                PromotionDiscount(
                    newPrice = 65.asMoney,
                    originalPrice = 101.asMoney
                ),
                quantity = 101,
                pricePerUnit = 1.asMoney,
            ),
            TestCase(
                description = "Buy 2 get 1 free",
                BuySomeGetSomeFreePromotion(buy = 2, free = 1),
                PromotionDiscount(
                    newPrice = 2816.asMoney,
                    originalPrice = 5632.asMoney
                ),
                quantity = 44,
                pricePerUnit = 128.asMoney,
            ),
        )
        private val invalidPromotionTestCases = listOf(
            InvalidPromotionTestCase(
                description = "Can't get more Free than the buy requirement",
                BuySomeGetSomeFreePromotion(buy = 1, free = 1000),
                quantity = 1000,
                pricePerUnit = 2.5.asMoney,
            ),
            InvalidPromotionTestCase(
                description = "Quantity can't be ZERO",
                BuySomeGetSomeFreePromotion(buy = 10, free = 2),
                quantity = 0,
                pricePerUnit = 1.2.asMoney,
            ),
            InvalidPromotionTestCase(
                description = "Promotion with buy ZERO is not valid",
                BuySomeGetSomeFreePromotion(buy = 0, free = 3),
                quantity = 10,
                pricePerUnit = 1.asMoney,
            ),
            InvalidPromotionTestCase(
                description = "Promotion with free ZERO is not valid",
                BuySomeGetSomeFreePromotion(buy = 10, free = 0),
                quantity = 10,
                pricePerUnit = 1.asMoney,
            ),
            InvalidPromotionTestCase(
                description = "A promotion with zero price is not valid",
                BuySomeGetSomeFreePromotion(buy = 2, free = 1),
                quantity = 44,
                pricePerUnit = 0.asMoney,
            ),
        )
    }
//
//    @Test
//    fun `when the product costs zero there is no discount`() {
//        val product = ProductListing(
//            product = Product(name = "water", category = listOf(Category("beverage"))),
//            pricePerUnit = 0.0.asMoney,
//        )
//        val promotion = AmountOffPriceDiscount(.4.asMoney)
//        assertEquals(
//            null,
//            calculator.calculate(product, 2, promotion),
//        )
//    }
//
//    @Test
//    fun `when the promotion discount is zero there is no discount`() {
//        val product = ProductListing(
//            product = Product(name = "water", category = listOf(Category("beverage"))),
//            pricePerUnit = 1.2.asMoney,
//        )
//        val promotion = AmountOffPriceDiscount(0.asMoney)
//        assertEquals(
//            null,
//            calculator.calculate(product, 2, promotion),
//        )
//    }
//
//    @Test
//    fun `when the product quantity is zero there is no discount`() {
//        val product = ProductListing(
//            product = Product(name = "water", category = listOf(Category("beverage"))),
//            pricePerUnit = 1.2.asMoney,
//        )
//        val promotion = AmountOffPriceDiscount(.2.asMoney)
//        assertEquals(
//            null,
//            calculator.calculate(product, 0, promotion),
//        )
//    }
}