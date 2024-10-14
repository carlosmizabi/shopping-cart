package org.carlos.cart.promotions.calculators.combos

import org.carlos.cart.entities.Bill
import org.carlos.cart.entities.Promotion

interface ComboPromotionCalculator {
    fun calculate(billItems: List<Bill.Item>, promotion: Promotion): List<Bill.Combo>?
}

