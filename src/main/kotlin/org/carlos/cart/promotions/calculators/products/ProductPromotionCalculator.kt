package org.carlos.cart.promotions.calculators.products

import org.carlos.cart.entities.ProductListing
import org.carlos.cart.entities.Promotion
import org.carlos.cart.entities.PromotionDiscount

interface ProductPromotionCalculator {
    fun calculate(productListing: ProductListing, quantity: Int, promotion: Promotion): PromotionDiscount?
}

