package org.carlos.cart.entities

data class PromotionDiscount(
    val newPrice: Money,
    val originalPrice: Money,
) {
    val discount: Money = (originalPrice - newPrice).clampedToZero
}