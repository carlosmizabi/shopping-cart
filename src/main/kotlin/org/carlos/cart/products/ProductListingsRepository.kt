package org.carlos.cart.products

import org.carlos.cart.data.SimpleRepository
import org.carlos.cart.entities.ProductListing
import java.util.*

class ProductListingsRepository: SimpleRepository<ProductListing, UUID>() {
    override fun find(identifier: UUID): ProductListing? = items.firstOrNull { it.id == identifier }
}