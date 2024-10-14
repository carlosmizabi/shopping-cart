package org.carlos.cart

import org.carlos.cart.bill.BillCalculator
import org.carlos.cart.bill.BillCalculatorImpl
import org.carlos.cart.bill.BillRepository
import org.carlos.cart.bill.BillRepositoryImpl
import org.carlos.cart.entities.*
import org.carlos.cart.products.ProductListingsRepository
import org.carlos.cart.promotions.*
import org.carlos.cart.promotions.calculators.*
import org.carlos.cart.promotions.calculators.combos.CartComboPromotionsCalculator
import org.carlos.cart.promotions.calculators.combos.CartCombosPromotionsCalculatorImpl
import org.carlos.cart.promotions.calculators.combos.SpecialPriceComboDealCalculator
import org.carlos.cart.promotions.calculators.generic.AmountOffPriceCalculator
import org.carlos.cart.promotions.calculators.generic.PercentageOffPriceCalculator
import org.carlos.cart.promotions.calculators.products.BuySomeGetSomeFreeCalculator
import org.carlos.cart.testData.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CartTest {

    private lateinit var cart: Cart
    private lateinit var productListingsRepo: ProductListingsRepository
    private lateinit var cartPromotionsRepo: CartPromotionsRepositoryImpl
    private lateinit var productsPromotionsCalculator: CartProductsPromotionsCalculator
    private lateinit var cartComboPromotionsCalculator: CartComboPromotionsCalculator
    private lateinit var billRepo: BillRepository
    private lateinit var billCalculator: BillCalculator

    @BeforeEach
    fun setup() {
        productListingsRepo = ProductListingsRepository().apply {
            add(listOf(banana, orange, juice, tunaSweetCornSandwich, appleHalfPrice, turnip, noodlesPot))
        }
        cartPromotionsRepo = CartPromotionsRepositoryImpl().apply {
            add(listOf(halfPrice, fiftyTwoPenceOff))
        }
        productsPromotionsCalculator = CartProductsPromotionsCalculator(
            listOf(
                PercentageOffPriceCalculator(),
                AmountOffPriceCalculator(),
                BuySomeGetSomeFreeCalculator()
            )
        )
        cartComboPromotionsCalculator = CartCombosPromotionsCalculatorImpl(
            listOf(
                SpecialPriceComboDealCalculator()
            )
        )
        billRepo = BillRepositoryImpl(
            cartPromotionsRepo = cartPromotionsRepo,
        )
        billCalculator = BillCalculatorImpl(
            cartPromotionsRepo = cartPromotionsRepo,
            productPromotionsCalculator = productsPromotionsCalculator,
            comboPromotionsCalculator = cartComboPromotionsCalculator,
        )
        cart = Cart(
            productListingsRepo,
            cartPromotionsRepo,
            billRepo,
            billCalculator
        )
    }

    @Test
    fun `should add product to cart`() {
        cart.add(banana.id, 1)
        assertEqualsCartBill(
            Bill(
                items = listOf(oneBananaBillItem),
                summary = Bill.Summary(
                    totalCost = banana.pricePerUnit,
                    totalBeforeDiscounts = banana.pricePerUnit
                )
            )
        )
    }

    @Test
    fun `should increase product quantity in cart when the same product is added`()  {
        cart.add(banana.id)
        cart.add(orange.id)
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    oneBananaBillItem,
                    makeOrangeBillItem(1)
                ),
                summary = Bill.Summary(
                    totalCost = 2.5.asMoney,
                    totalBeforeDiscounts = 2.5.asMoney,
                )
            )
        )
        cart.add(banana.id)
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    makeBananasBillItem(2),
                    makeOrangeBillItem(1)
                ),
                summary = Bill.Summary(
                    totalCost = 3.5.asMoney,
                    totalBeforeDiscounts = 3.5.asMoney,
                )
            )
        )
    }

    @Test
    fun `should increase product in cart with a specific amount`()  {
        val quantityOfOranges = 100
        val orangesPrice = orange.pricePerUnit * quantityOfOranges
        val expectedTotal = orangesPrice + banana.pricePerUnit
        cart.add(banana.id)
        cart.add(orange.id, quantityOfOranges)
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    oneBananaBillItem,
                    makeOrangeBillItem(100)
                ),
                summary = Bill.Summary(
                    totalCost = expectedTotal,
                    totalBeforeDiscounts = expectedTotal,
                )
            )
        )
    }

    @Test
    fun `should remove from cart altogether when reducing and only one is left`() {
        cart.add(banana.id)
        cart.add(orange.id, 2)
        cart.reduce(banana.id)
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    makeOrangeBillItem(2)
                ),
                summary = Bill.Summary(
                    totalCost = orange.pricePerUnit * 2,
                    totalBeforeDiscounts = orange.pricePerUnit * 2,
                )
            )
        )
    }

    @Test
    fun `should reduce product quantity in cart`() {
        val billQuantity = 50
        val finalCost = orange.pricePerUnit * billQuantity
        cart.add(orange.id, 100)
        cart.reduce(orange.id, billQuantity)
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    makeOrangeBillItem(billQuantity)
                ),
                summary = Bill.Summary(
                    totalCost = finalCost,
                    totalBeforeDiscounts = finalCost,
                )
            )
        )
    }

    @Test
    fun `should remove the product altogether from the cart`() {
        cart.add(banana.id)
        cart.add(orange.id, 10)
        cart.remove(orange.id)
        assertEqualsCartBill(
            Bill(
                items = listOf(oneBananaBillItem),
                summary = Bill.Summary(
                    totalCost = oneBananaBillItem.totalCost,
                    totalBeforeDiscounts = oneBananaBillItem.totalCost,
                )
            )
        )
    }


    @Test
    fun `should apply percentage off cart discount total when discount is available`() {
        cartPromotionsRepo.add(listOf(halfPrice, fiftyTwoPenceOff))
        val totalBeforeDiscount = banana.pricePerUnit + orange.pricePerUnit
        val finalCost = totalBeforeDiscount / 2
        cart.addCartPromotion(halfPrice.id)
        cart.add(banana.id) // 1
        cart.add(orange.id) // 1.5
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    makeBananasBillItem(1),
                    makeOrangeBillItem(1),
                ),
                summary = Bill.Summary(
                    totalCost = finalCost,
                    totalBeforeDiscounts = totalBeforeDiscount,
                    cartPromotion = halfPrice
                )
            )
        )
        cart.addCartPromotion(fiftyTwoPenceOff.id)
    }

    @Test
    fun `should apply amount off cart discount total when discount is available`() {
        cartPromotionsRepo.add(listOf(halfPrice, fiftyTwoPenceOff))
        val totalBeforeDiscounts = banana.pricePerUnit + orange.pricePerUnit
        val finalCost = totalBeforeDiscounts - 0.52.asMoney
        cart.addCartPromotion(fiftyTwoPenceOff.id)
        cart.add(banana.id) // 1
        cart.add(orange.id) // 1.5
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    makeBananasBillItem(1),
                    makeOrangeBillItem(1),
                ),
                summary = Bill.Summary(
                    totalCost = finalCost,
                    totalBeforeDiscounts = totalBeforeDiscounts,
                    cartPromotion = fiftyTwoPenceOff
                )
            )
        )
        cart.addCartPromotion(fiftyTwoPenceOff.id)
    }

    @Test
    fun `should always apply the last cart promotion added`() {
        val totalBeforeDiscount = banana.pricePerUnit + orange.pricePerUnit
        val finalCost = totalBeforeDiscount / 2
        cart.addCartPromotion(fiftyTwoPenceOff.id)
        cart.addCartPromotion(halfPrice.id)
        cart.addCartPromotion(fiftyTwoPenceOff.id)
        cart.addCartPromotion(halfPrice.id)

        cart.add(banana.id) // 1
        cart.add(orange.id) // 1.5
        assertEqualsCartBill(
            Bill(
                items = listOf(
                    makeBananasBillItem(),
                    makeOrangeBillItem(),
                ),
                summary = Bill.Summary(
                    totalCost = finalCost,
                    totalBeforeDiscounts = totalBeforeDiscount,
                    cartPromotion = halfPrice
                )
            )
        )
    }

    @Test
    fun `should apply specific product discount when product has discount`() {
        val promotion = PercentageOffPriceDiscount(Percentage(.5))
        val bananaOnPromotion = ProductListing(
            product = banana.product,
            pricePerUnit = 1.asMoney,
            promotion = promotion
        )
        productListingsRepo.add(bananaOnPromotion)
        cart.add(bananaOnPromotion.id, 3)
        assertEqualsCartBill(
            Bill(
                summary = Bill.Summary(
                    totalCost = 1.5.asMoney,
                    totalBeforeDiscounts = 3.asMoney
                ),
                items = listOf(
                    Bill.Item(
                        productListing = bananaOnPromotion,
                        quantity = 3,
                        totalCost = 1.5.asMoney,
                        appliedProductPromotion = bananaOnPromotion.promotion,
                        discounted = 1.5.asMoney
                    )
                ),
            ),
        )
    }

    @Test
    fun `should apply buy two get one free promotion when eligible products are added`() {
        val promotion = BuySomeGetSomeFreePromotion(10, 5)
        val bananaOnPromotion = ProductListing(
            product = banana.product,
            pricePerUnit = 1.asMoney,
            promotion = promotion
        )
        productListingsRepo.add(bananaOnPromotion)
        cart.add(bananaOnPromotion.id, 10)
        assertEqualsCartBill(
            Bill(
                summary = Bill.Summary(
                    totalBeforeDiscounts = 10.asMoney,
                    totalCost = 5.asMoney
                ),
                items = listOf(
                    Bill.Item(
                        productListing = bananaOnPromotion,
                        quantity = 10,
                        totalCost = 5.asMoney,
                        appliedProductPromotion = bananaOnPromotion.promotion,
                        discounted = 5.asMoney
                    )
                ),
            ),
        )
    }

    @Test
    fun `should apply combo promotion when required products are in the cart`() {
        cartPromotionsRepo.add(lunchMealDeal)
        listOf(banana, juice, tunaSweetCornSandwich).forEach {
            productListingsRepo.add(it)
            cart.add(it.id)
        }
        val bill = cart.bill()!!
        assertEquals(
            Bill.Summary(
                totalCost = lunchMealDeal.comboPrice,
                totalBeforeDiscounts = 5.asMoney
            ),
            bill.summary
        )
        assertEquals(1, bill.comboDeals.size)
        val actualComboPromotionApplied = bill.comboDeals.first().promotion
        assertEquals(lunchMealDeal, actualComboPromotionApplied)
        assertEquals(3, bill.items.size)
        val bananaBillItem = bill.items.first { it.productListing == banana }
        val sandwichBillItem = bill.items.first { it.productListing == tunaSweetCornSandwich }
        val juiceBillItem = bill.items.first { it.productListing == juice }
        assertNotNull(bananaBillItem)
        assertNotNull(sandwichBillItem)
        assertNotNull(juiceBillItem)
    }

    @Test
    fun `big shopping multiple promotions`() {
        cartPromotionsRepo.add(lunchMealDeal)
        // Lunch Deal $5 -> $2.0
        cart.add(banana.id)
        cart.add(tunaSweetCornSandwich.id)
        cart.add(juice.id)
        // Apples Half Price $30 -> $15
        cart.add(appleHalfPrice.id, 10)
        // Random Items
        cart.add(turnip.id, 10) // $2
        cart.add(orange.id) // $1.5
        cart.add(juice.id) // $2.10
        cart.add(banana.id) // $1

        val bill = cart.bill()!!
        assertEquals(6, bill.items.size)
        assertEquals(
            Bill.Summary(
                totalCost = 23.6.asMoney,
                totalBeforeDiscounts = 41.6.asMoney
            ), bill.summary
        )
    }

    @Test
    fun `apply all eligible combos on same products when enough quantities`() {
        val weirdLunchMealDeal = SpecialPriceComboDeal(
            comboPrice = 5.0.asMoney,
            categories = listOf(NoodlesPot, LunchDrink, LunchSide)
        )
        cartPromotionsRepo.add(lunchMealDeal)
        cartPromotionsRepo.add(weirdLunchMealDeal)
        // Lunch Deal $5 -> $2.0
        cart.add(banana.id)
        cart.add(tunaSweetCornSandwich.id)
        cart.add(juice.id)
        // Weird Lunch Deal $13.10 -> 5
        cart.add(banana.id)
        cart.add(juice.id)
        cart.add(noodlesPot.id)
        // Random
        cart.add(turnip.id) // $0.2

        val bill = cart.bill()!!
        assertEquals(5, bill.items.size)
        assertEquals(2, bill.comboDeals.size)
        assertEquals(
            Bill.Summary(
                totalCost = 7.2.asMoney,
                totalBeforeDiscounts = 18.3.asMoney
            ), bill.summary
        )
    }

    private fun assertEqualsCartBill(bill: Bill) {
        assertEquals(
            bill,
            cart.bill()
        )
    }
}