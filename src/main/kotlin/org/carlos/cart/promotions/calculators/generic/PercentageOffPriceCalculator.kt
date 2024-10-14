package org.carlos.cart.promotions.calculators.generic

import org.carlos.cart.entities.*
import org.carlos.cart.promotions.calculators.products.ProductPromotionCalculator

class PercentageOffPriceCalculator : ProductPromotionCalculator {
     override fun calculate(
         productListing: ProductListing,
         quantity: Int,
         promotion: Promotion
    ): PromotionDiscount? {
        if(promotion !is PercentageOffPriceDiscount || areZero(productListing, quantity, promotion)) return null
        val priceForQuantity = productListing.priceFor(quantity)
        val afterDiscount =  priceForQuantity.reduceBy(promotion.percentage)
        return PromotionDiscount(
            newPrice = afterDiscount.clampedToZero,
            originalPrice = priceForQuantity,
        )
    }

    private fun areZero(productListing: ProductListing, quantity: Int, promotion: PercentageOffPriceDiscount) =
        quantity == 0 || productListing.pricePerUnit.isZero() || promotion.percentage.value == 0.0
}