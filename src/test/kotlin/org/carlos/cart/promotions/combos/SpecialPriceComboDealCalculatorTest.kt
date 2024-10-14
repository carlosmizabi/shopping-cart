package org.carlos.cart.promotions.combos

import org.carlos.cart.entities.Bill
import org.carlos.cart.entities.asMoney
import org.carlos.cart.promotions.calculators.combos.SpecialPriceComboDealCalculator
import org.carlos.cart.testData.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class SpecialPriceComboDealCalculatorTest {

    private lateinit var calculator: SpecialPriceComboDealCalculator

    @BeforeEach
    fun setup() {
        calculator = SpecialPriceComboDealCalculator()
    }

    @Test
    fun `apply combo deal for one of each`() {
        val billItems = listOf(bananaBillItem, sandwichBillItem, juiceBillItem)
        val expected = listOf(
            Bill.Combo(
                promotion = lunchMealDeal,
                items = listOf(bananaBillItem, sandwichBillItem, juiceBillItem),
                totalDiscount = 3.asMoney
            ),
        )
        val actual = calculator.calculate(billItems, lunchMealDeal)!!
        assertEquals(expected, actual)
    }

    @Test
    fun `item missing can't apply combo`() {
        val billItems = listOf(bananaBillItem, juiceBillItem)
        assertEquals(
            emptyList(),
            calculator.calculate(billItems, lunchMealDeal)!!
        )
    }

    @Test
    fun `apply combo deal 3 times`() {
        val banana = bananaBillItem.copy(quantity = 3)
        val sandwich = sandwichBillItem.copy(quantity = 3)
        val juice = juiceBillItem.copy(quantity = 3)
        val billItems = listOf(banana, sandwich, juice)
        val actual = calculator.calculate(billItems, lunchMealDeal)!!
        assertEquals(3,actual.size)
        actual.forEach { combo ->
            assertEquals(3, combo.items.size)
            assertEquals(lunchMealDeal, combo.promotion)
            val items = combo.items
            assertEquals(banana, items[0])
            assertEquals(sandwich, items[1])
            assertEquals(juice, items[2])
        }
    }

    @Test
    fun `apply combo deal only twice when all products but one has enough quantity to apply it 3 times`() {
        val banana = bananaBillItem.copy(quantity = 3)
        val sandwich = sandwichBillItem.copy(quantity = 3)
        val juice = juiceBillItem.copy(quantity = 2)
        val billItems = listOf(banana, sandwich, juice)
        val combos = calculator.calculate(billItems, lunchMealDeal)!!
        assertEquals(2, combos.size, "Number of Eligible Combos")
        combos.forEach { combo ->
            assertEquals(3, combo.items.size, "Items in the Combo")
            assertEquals(lunchMealDeal, combo.promotion)
            assertEquals(banana, combo.items[0])
            assertEquals(sandwich, combo.items[1])
            assertEquals(juice, combo.items[2])
        }
    }

    companion object {
        private val bananaBillItem = Bill.Item(
            productListing = banana,
            quantity = 1,
            totalCost = banana.priceFor(1)
        )
        private val sandwichBillItem = Bill.Item(
            productListing = tunaSweetCornSandwich,
            quantity = 1,
            totalCost = tunaSweetCornSandwich.priceFor(1)
        )
        private val juiceBillItem = Bill.Item(
            productListing = juice,
            quantity = 1,
            totalCost = juice.priceFor(1)
        )
    }
}