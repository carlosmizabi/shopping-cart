package org.carlos.cart.entities

import java.util.UUID

open class Promotion(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val description: String = "",
    val type: PromotionType
)

data class PercentageOffPriceDiscount(
    val percentage: Percentage
) : Promotion(
    name = "Percentage Discount",
    description = "Applies a percentage discount to the item",
    type = PromotionType.Generic
)

data class AmountOffPriceDiscount(
    val discountAmount: Money
) : Promotion(
    name = "Money Discount",
    description = "Applies a fixed amount discount to the item",
    type = PromotionType.Generic
)

data class BuySomeGetSomeFreePromotion(
    val buy: Int,
    val free: Int,
) : Promotion(
    name = "Buy Some Get Some Free",
    description = "Buy some quantity of product and get additional quantity free",
    type = PromotionType.Product
)

data class SpecialPriceComboDeal(
    val comboPrice: Money,
    val categories: List<Category>,
): Promotion(
    name = "Buy one of Each",
    description = "Buy one product of each category for the promotion price",
    type = PromotionType.Combo
)
