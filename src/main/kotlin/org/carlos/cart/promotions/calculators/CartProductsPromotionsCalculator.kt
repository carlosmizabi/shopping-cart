package org.carlos.cart.promotions.calculators

import org.carlos.cart.entities.ProductListing
import org.carlos.cart.entities.Promotion
import org.carlos.cart.entities.PromotionDiscount
import org.carlos.cart.promotions.calculators.products.ProductPromotionCalculator

class CartProductsPromotionsCalculator (
    private val calculators: List<ProductPromotionCalculator>
): ProductPromotionCalculator {

    override fun calculate(productListing: ProductListing, quantity: Int, promotion: Promotion): PromotionDiscount? {
        for (calculator in calculators) {
            val discount = calculator.calculate(productListing, quantity, promotion)
            if (discount != null) {
                return discount
            }
        }
        return null
    }
}