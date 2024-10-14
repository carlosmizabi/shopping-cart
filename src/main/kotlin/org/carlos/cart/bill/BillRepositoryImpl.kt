package org.carlos.cart.bill

import org.carlos.cart.entities.*
import org.carlos.cart.promotions.CartPromotionsRepository

class BillRepositoryImpl(
    private val cartPromotionsRepo: CartPromotionsRepository,
) : BillRepository {
    private val billItems = mutableListOf<Bill.Item>()
    private var promotions = mutableListOf<Promotion>()

    override fun add(item: ProductListing, quantity: Int) {
        val existingItemIndex = billItems.indexOfFirst { it.productListing == item }
        if (existingItemIndex != -1) {
            val billItem = billItems[existingItemIndex]
            val newQuantity = billItem.quantity + quantity
            val newTotalCost = billItem.productListing.pricePerUnit * newQuantity
            billItems[existingItemIndex] = billItem.copy(
                totalCost = newTotalCost,
                quantity = newQuantity
            )
        } else {
            addToBill(item, quantity)
        }
    }

    override fun addToBill(item: ProductListing, quantity: Int) {
        val totalCost = item.pricePerUnit * quantity
        billItems.add(
            Bill.Item(
                productListing = item,
                quantity = quantity,
                totalCost = totalCost
            )
        )
    }

    override fun add(promotion: Promotion) {
        if (cartPromotionsRepo.isAllowedPromotion(promotion)) {
            promotions.remove(promotion)
            promotions.add(0, promotion)
        }
    }

    override fun remove(item: ProductListing) =
        billItems.removeIf { it.productListing == item }

    override fun reduce(item: ProductListing, quantity: Int) {
        val index = billItems.indexOfFirst { it.productListing == item }
        if (index == -1) return
        billItems[index].let {
            val newQuantity = (it.quantity - quantity).coerceAtLeast(0)
            if (newQuantity == 0) {
                remove(it.productListing)
            } else {
                val newTotal = it.productListing.pricePerUnit * newQuantity
                billItems[index] = it.copy(
                    quantity = newQuantity,
                    totalCost = newTotal,
                )
            }
        }
    }

    override fun clear() {
        promotions.clear()
        billItems.clear()
    }

    override fun billItems(): List<Bill.Item> = billItems

    override fun billPromotions(): List<Promotion> = promotions
}






