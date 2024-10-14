package org.carlos.cart.promotions.calculators.combos

import org.carlos.cart.entities.*


class SpecialPriceComboDealCalculator : ComboPromotionCalculator {
    override fun calculate(billItems: List<Bill.Item>, promotion: Promotion): List<Bill.Combo>? {
        if (promotion !is SpecialPriceComboDeal) return null
        val categorizedItems = billItems
            .flatMap { item -> item.productListing.product.category.map { it to item } }
            .groupBy({ it.first }, { it.second })
            .filter { promotion.categories.contains(it.key) }
        return buildComboDeals(promotion, categorizedItems).toBillCombo()
    }

    private fun buildComboDeals(
        promotion: SpecialPriceComboDeal,
        categorizedItems: Map<Category, List<Bill.Item>>
    ): List<Combo> {
        val incompleteCombos = mutableListOf<Combo>()
        val eligibleCombos = mutableListOf<Combo>()

        for ((category, billItems) in categorizedItems) {
            for (billItem in billItems) {
                repeat(billItem.quantity) {
                    val missingItemForCatCombo = incompleteCombos.findComboWithoutItemFor(category)
                    if (missingItemForCatCombo != null) {
                        missingItemForCatCombo.items[category] = billItem
                        if (missingItemForCatCombo.isCompleteFor(promotion)) {
                            val totalDiscount = missingItemForCatCombo.calculateTotalDiscount(promotion)
                            val completeCombo = missingItemForCatCombo.copy(totalDiscount = totalDiscount)
                            eligibleCombos.add(completeCombo)
                            incompleteCombos.remove(completeCombo)
                        }
                    } else {
                        incompleteCombos.add(Combo(promotion, mutableMapOf(category to billItem)))
                    }
                }
            }
        }

        return eligibleCombos
    }

    private data class Combo(
        val promotion: Promotion,
        val items: MutableMap<Category, Bill.Item>,
        val totalDiscount: Money = 0.asMoney
    ) {
        fun isCompleteFor(promotion: SpecialPriceComboDeal): Boolean = items.keys.containsAll(promotion.categories)

        fun calculateTotalDiscount(promotion: SpecialPriceComboDeal): Money {
            val totalBeforePromotion = this.items.values.map { it.productListing.pricePerUnit }.reduce { acc, money -> acc + money }
            return totalBeforePromotion - promotion.comboPrice
        }
    }

    private fun List<Combo>.findComboWithoutItemFor(category: Category): Combo? =
        find { !it.items.containsKey(category) }

    private fun List<Combo>.toBillCombo(): List<Bill.Combo> = this.map {
        Bill.Combo(
            promotion = it.promotion,
            items = it.items.values.toList(),
            totalDiscount = it.totalDiscount
        )
    }

}


