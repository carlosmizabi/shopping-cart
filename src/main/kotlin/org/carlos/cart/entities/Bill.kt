package org.carlos.cart.entities

data class Bill(
    val items: List<Item> = emptyList(),
    val summary: Summary = Summary.EMPTY,
    val comboDeals: List<Combo> = emptyList(),
) {

    data class Summary(
        val totalCost: Money = 0.asMoney,
        val totalBeforeDiscounts: Money,
        val cartPromotion: Promotion? = null
    ) {
        companion object {
            val EMPTY = Summary(totalBeforeDiscounts = 0.asMoney)
        }
    }

    data class Item(
        val productListing: ProductListing,
        val quantity: Int,
        val totalCost: Money,
        val discounted: Money? = null,
        val appliedProductPromotion: Promotion? = null,
    ) {
        val priceBeforePromos: Money = productListing.pricePerUnit * quantity
    }

    data class Combo(
        val promotion: Promotion,
        val items: List<Item>,
        val totalDiscount: Money = 0.asMoney
    )
}



