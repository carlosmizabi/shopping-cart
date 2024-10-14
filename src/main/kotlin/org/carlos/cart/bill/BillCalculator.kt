package org.carlos.cart.bill

import org.carlos.cart.entities.*
import org.carlos.cart.promotions.CartPromotionsRepository
import org.carlos.cart.promotions.calculators.products.ProductPromotionCalculator
import org.carlos.cart.promotions.calculators.combos.CartComboPromotionsCalculator

interface BillCalculator {
    fun calculate(billItems: List<Bill.Item>, promotions: List<Promotion>): Bill?
}

class BillCalculatorImpl(
    private val cartPromotionsRepo: CartPromotionsRepository,
    private val productPromotionsCalculator: ProductPromotionCalculator,
    private val comboPromotionsCalculator: CartComboPromotionsCalculator
) : BillCalculator  {

    override fun calculate(billItems: List<Bill.Item>, promotions: List<Promotion>): Bill? {
        if (billItems.isEmpty()) return null
        val cartPromotion = promotions.firstOrNull()
        return if (cartPromotion != null) {
            billItems.calculateBillWithCartPromotion(cartPromotion)
        } else {
            val items = billItems.map { it.applyProductPromotion() }
            var comboDeals = emptyList<Bill.Combo>()
            cartPromotionsRepo.getCombos()?.let { comboPromotions ->
                val itemsWithoutPromotion = items.filter { it.appliedProductPromotion == null }
                comboDeals = comboPromotionsCalculator.calculate(itemsWithoutPromotion, comboPromotions) ?: emptyList()
            }
            Bill(
                items = items,
                summary = Bill.Summary(
                    totalCost = items.calculateCurrentCost(comboDeals),
                    totalBeforeDiscounts = items.calculateCostWithoutDiscounts()
                ),
                comboDeals = comboDeals
            )
        }
    }

    private fun Bill.Item.applyProductPromotion(): Bill.Item {
        val promotion = productListing.promotion
        return if (promotion == null) this
        else {
            productPromotionsCalculator.calculate(
                productListing = productListing,
                quantity = quantity,
                promotion = promotion
            )?.let {
                copy(
                    totalCost = it.newPrice,
                    discounted = it.discount,
                    appliedProductPromotion = promotion
                )
            } ?: this
        }
    }

    private fun List<Bill.Item>.calculateCurrentCost(comboDeals: List<Bill.Combo> = emptyList()): Money =
        map { it.totalCost }.let {
            if (it.isNotEmpty()) {
                val total = it.reduce { acc, money -> acc + money }
                val combosDiscount =
                    comboDeals.map { it.totalDiscount }.takeIf { it.isNotEmpty() }?.reduce { acc, money -> acc + money }
                total - (combosDiscount ?: 0.asMoney)
            } else 0.asMoney
        }

    private fun List<Bill.Item>.calculateCostWithoutDiscounts(): Money = map { it.priceBeforePromos }.let {
        if (it.isNotEmpty()) it.reduce { acc, money -> acc + money }
        else 0.asMoney
    }

    private fun List<Bill.Item>.calculateBillWithCartPromotion(promotion: Promotion): Bill {
        val totalBeforePromotions = calculateCurrentCost()
        val finalTotal = promotion.calculateTotalAfterDiscount(totalBeforePromotions)
        return Bill(
            items = this,
            summary = Bill.Summary(
                totalCost = finalTotal,
                totalBeforeDiscounts = totalBeforePromotions,
                cartPromotion = promotion
            )
        )
    }

    private fun Promotion.calculateTotalAfterDiscount(totalBeforeDiscounts: Money): Money = when (this) {
        is AmountOffPriceDiscount -> totalBeforeDiscounts - discountAmount
        is PercentageOffPriceDiscount -> totalBeforeDiscounts.reduceBy(percentage)
        else -> totalBeforeDiscounts
    }
}






