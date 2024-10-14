package org.carlos.cart.promotions.calculators.products

import org.carlos.cart.entities.*

class BuySomeGetSomeFreeCalculator : ProductPromotionCalculator {
    override fun calculate(productListing: ProductListing, quantity: Int, promotion: Promotion): PromotionDiscount? {
        if (promotion !is BuySomeGetSomeFreePromotion || areZero(productListing, quantity, promotion)) return null
        return if (quantity >= promotion.buy && promotion.buy >= promotion.free) {
            val sets = quantity.floorDiv(promotion.buy)
            val setsPrice = productListing.pricePerUnit * (sets * (promotion.buy - promotion.free))
            val remainingItems = quantity % sets
            val remainingPrice = productListing.pricePerUnit * remainingItems
            val newPrice = setsPrice + remainingPrice
            PromotionDiscount(
                newPrice = newPrice,
                originalPrice = productListing.priceFor(quantity)
            )
        } else null
    }

    private fun areZero(productListing: ProductListing, quantity: Int, promotion: BuySomeGetSomeFreePromotion) =
        quantity == 0 || productListing.pricePerUnit.isZero() || promotion.buy == 0 || promotion.free == 0
}