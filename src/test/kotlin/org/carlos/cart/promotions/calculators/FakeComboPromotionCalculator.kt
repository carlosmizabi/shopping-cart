package org.carlos.cart.promotions.calculators

import org.carlos.cart.entities.Bill
import org.carlos.cart.entities.Promotion
import org.carlos.cart.promotions.calculators.combos.ComboPromotionCalculator

class FakeComboPromotionCalculator(private val supportedPromotion: Promotion): ComboPromotionCalculator {
    var returnForTest: List<Bill.Combo>? = null
    override fun calculate(billItems: List<Bill.Item>, promotion: Promotion): List<Bill.Combo>? {
        if (promotion != supportedPromotion) return null
       return returnForTest
    }
}