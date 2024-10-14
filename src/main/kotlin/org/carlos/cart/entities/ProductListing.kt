package org.carlos.cart.entities

import java.util.UUID

data class ProductListing(
    val id: UUID = UUID.randomUUID(),
    val product: Product,
    val pricePerUnit: Money,
    val promotion: Promotion? = null,
    val comboPromotions: List<Promotion>? = null
) {
    fun priceFor(quantity: Int): Money = pricePerUnit * quantity
}

