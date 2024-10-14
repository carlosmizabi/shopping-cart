package org.carlos.cart

import org.carlos.cart.bill.BillCalculator
import org.carlos.cart.bill.BillRepository
import org.carlos.cart.data.SimpleRepository
import org.carlos.cart.promotions.CartPromotionsRepository
import org.carlos.cart.entities.Bill
import org.carlos.cart.entities.ProductListing
import java.util.*

class Cart(
    private val productListingsRepo: SimpleRepository<ProductListing, UUID>,
    private val cartPromotionsRepo: CartPromotionsRepository,
    private val billRepo: BillRepository,
    private val billCalculator: BillCalculator
): ShoppingCart {

    override fun add(id: UUID) = add(id, 1)

    override fun add(id: UUID, quantity: Int) {
         productListingsRepo.find(id)?.also { listing ->
            billRepo.add(listing, quantity)
        }
    }

    override fun remove(productId: UUID) {
        productListingsRepo.find(productId)?.also { listing ->
            billRepo.remove(listing)
        }
    }

    override fun reduce(productId: UUID) = reduce(productId, 1)

    override fun reduce(id: UUID, quantity: Int) {
        productListingsRepo.find(id)?.also { listing ->
            billRepo.reduce(listing, quantity)
        }
    }

    override fun addCartPromotion(promotionId: UUID) {
        cartPromotionsRepo.find(promotionId)?.also {
            billRepo.add(it)
        }
    }

    override fun bill(): Bill? = billCalculator.calculate(billRepo.billItems(), billRepo.billPromotions())

}
