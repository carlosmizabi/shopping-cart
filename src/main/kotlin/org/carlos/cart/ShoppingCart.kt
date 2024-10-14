package org.carlos.cart

import org.carlos.cart.entities.Bill
import java.util.*

interface ShoppingCart {
    fun add(productId: UUID, quantity: Int)
    fun remove(productId: UUID)
    fun reduce(productId: UUID, quantity: Int)
    fun addCartPromotion(promotionId: UUID)
    fun bill(): Bill?
    fun add(id: UUID)
    fun reduce(productId: UUID)
}