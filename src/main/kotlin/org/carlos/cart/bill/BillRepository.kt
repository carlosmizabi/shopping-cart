package org.carlos.cart.bill

import org.carlos.cart.entities.Bill
import org.carlos.cart.entities.ProductListing
import org.carlos.cart.entities.Promotion

interface BillRepository {
    fun add(item: ProductListing, quantity: Int)
    fun addToBill(item: ProductListing, quantity: Int)
    fun add(promotion: Promotion)
    fun remove(item: ProductListing): Boolean
    fun reduce(item: ProductListing, quantity: Int)
    fun clear()
    fun billItems(): List<Bill.Item>
    fun billPromotions(): List<Promotion>
}
