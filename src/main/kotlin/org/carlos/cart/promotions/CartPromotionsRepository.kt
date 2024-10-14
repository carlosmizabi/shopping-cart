package org.carlos.cart.promotions

import org.carlos.cart.data.SimpleRepository
import org.carlos.cart.entities.Promotion
import org.carlos.cart.entities.PromotionType
import java.util.*

interface CartPromotionsRepository {
    fun isAllowedPromotion(promotion: Promotion): Boolean
    fun find(promotionId: UUID): Promotion?
    fun getCombos(): List<Promotion>?
}

class CartPromotionsRepositoryImpl():  SimpleRepository<Promotion, UUID>(), CartPromotionsRepository {
    override fun isAllowedPromotion(promotion: Promotion): Boolean = items.contains(promotion)
    override fun find(promotionId: UUID): Promotion? = items.firstOrNull { it.id == promotionId }
    override fun getCombos(): List<Promotion>?
        = items.filter { it.type == PromotionType.Combo }.ifEmpty { null }
}