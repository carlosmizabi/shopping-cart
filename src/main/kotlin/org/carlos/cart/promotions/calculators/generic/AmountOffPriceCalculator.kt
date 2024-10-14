package org.carlos.cart.promotions.calculators.generic

import org.carlos.cart.entities.AmountOffPriceDiscount
import org.carlos.cart.entities.ProductListing
import org.carlos.cart.entities.Promotion
import org.carlos.cart.entities.PromotionDiscount
import org.carlos.cart.promotions.calculators.products.ProductPromotionCalculator


class AmountOffPriceCalculator : ProductPromotionCalculator {
    override fun calculate(productListing: ProductListing, quantity: Int, promotion: Promotion): PromotionDiscount? {
        return if (promotion !is AmountOffPriceDiscount || areZero(productListing, quantity, promotion)) null
        else {
            val newPrice = (productListing.pricePerUnit - promotion.discountAmount) * quantity
            PromotionDiscount(
                newPrice = newPrice,
                originalPrice = productListing.priceFor(quantity)
            )
        }
    }

    private fun areZero(productListing: ProductListing, quantity: Int, promotion: AmountOffPriceDiscount) =
        quantity == 0 || productListing.pricePerUnit.isZero() || promotion.discountAmount.isZero()

}

