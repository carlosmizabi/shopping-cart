package org.carlos.cart.promotions.calculators

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.carlos.cart.entities.Bill
import org.carlos.cart.promotions.calculators.combos.CartCombosPromotionsCalculatorImpl
import org.carlos.cart.promotions.calculators.combos.ComboPromotionCalculator
import org.carlos.cart.testData.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CartCombosPromotionCalculatorImplTest {

    private lateinit var calculator: CartCombosPromotionsCalculatorImpl

    @MockK
    private lateinit var subCalculator1: ComboPromotionCalculator

    @MockK
    private lateinit var subCalculator2: ComboPromotionCalculator

    @BeforeEach
    fun setup() {
        calculator = CartCombosPromotionsCalculatorImpl(listOf(subCalculator1, subCalculator2))
    }

    @Test
    fun `calculate should return null when no calculators are provided`() {
        val calculator = CartCombosPromotionsCalculatorImpl(emptyList())
        val billItems = listOf(makeBananasBillItem(), makeJuiceBillItem(), makeSandwichBillItem())
        val result = calculator.calculate(billItems, listOf(lunchMealDeal))
        assertEquals(null, result)
    }

    @Test
    fun `calculate should return null when no combos are found by any calculator`() {
        val calculator = CartCombosPromotionsCalculatorImpl(listOf(subCalculator1, subCalculator2))
        val billItems = listOf<Bill.Item>()
        listOf(subCalculator1, subCalculator2).forEach {
            every { it.calculate(any(), any()) } returns null
        }
        val result = calculator.calculate(billItems, listOf(lunchMealDeal))
        assertEquals(null, result)
    }

    @Test
    fun `calculate should return combos from all calculators`() {
        val sandwich = makeSandwichBillItem(1)
        val juice = makeJuiceBillItem(1)
        val banana = makeBananasBillItem(2)
        val turnip = makeTurnipBillItem(1)
        val lunchMealCombo = Bill.Combo(
            promotion = lunchMealDeal,
            items = listOf(sandwich, juice, banana)
        )
        val healthyFoodDealCombo = Bill.Combo(
            promotion = healthyFoodDeal,
            items = listOf(turnip, banana)
        )
        val subCalculator1 =
            FakeComboPromotionCalculator(lunchMealDeal).apply { returnForTest = listOf(lunchMealCombo) }
        val subCalculator2 =
            FakeComboPromotionCalculator(healthyFoodDeal).apply { returnForTest = listOf(healthyFoodDealCombo) }
        calculator = CartCombosPromotionsCalculatorImpl(listOf(subCalculator1, subCalculator2))
        val billItems = listOf(banana, turnip)

        val result = calculator.calculate(billItems, listOf(lunchMealDeal, healthyFoodDeal))
        assertNotNull(result)
        assertEquals(2, result!!.size, "Expected 2 combos in result")
        assertEquals(healthyFoodDealCombo, result[1])
        assertEquals(lunchMealCombo, result[0])
    }

    @Test
    fun `calculate should not use a product item for combo twice`() {
        val sandwich = makeSandwichBillItem(1)
        val juice = makeJuiceBillItem(1)
        val banana = makeBananasBillItem(1)
        val combo = Bill.Combo(
            promotion = lunchMealDeal,
            items = listOf(sandwich, juice, banana,)
        )
        val subCalculator = FakeComboPromotionCalculator(lunchMealDeal).apply { returnForTest = listOf(combo) }
        calculator = CartCombosPromotionsCalculatorImpl(listOf(subCalculator, subCalculator))
        val billItems = listOf(makeBananasBillItem(), makeSandwichBillItem(), makeJuiceBillItem())

        val result = calculator.calculate(billItems, listOf(lunchMealDeal))

        assertNotNull(result)
        assertEquals(1, result!!.size, "Expected 1 combo in result")
        assertEquals(combo, result[0])
    }
}

