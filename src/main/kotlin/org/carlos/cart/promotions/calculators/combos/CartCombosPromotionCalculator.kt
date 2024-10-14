package org.carlos.cart.promotions.calculators.combos

import org.carlos.cart.entities.Bill
import org.carlos.cart.entities.Promotion

interface CartComboPromotionsCalculator {
    fun calculate(billItems: List<Bill.Item>, promotions: List<Promotion>): List<Bill.Combo>?
}

class CartCombosPromotionsCalculatorImpl(
    private val calculators: List<ComboPromotionCalculator>
) : CartComboPromotionsCalculator {
    override fun calculate(billItems: List<Bill.Item>, promotions: List<Promotion>): List<Bill.Combo>? {
        if (billItems.isEmpty() || calculators.isEmpty()) return null
        val tally = billItems.associateWith { it.quantity }.toMutableMap() // (Bill.Item to quantity)
        val completeCombos = mutableListOf<Bill.Combo>()
        for (promotion in promotions) {
            for (calculator in calculators) {
                if (tally.isEmpty()) break
                calculator.calculate(tally.keys.toList(), promotion)?.also { newCombos ->
                    completeCombos.addAll(newCombos)
                    newCombos.flatMap { it.items }.forEach { billItem ->
                        tally[billItem]?.let {
                            val quantityLeft = it - 1
                            if (quantityLeft < 1) {
                                tally.remove(billItem)
                            } else {
                                tally[billItem] = quantityLeft
                            }
                        }
                    }
                }
            }
        }
        return completeCombos.ifEmpty { null }
    }
}