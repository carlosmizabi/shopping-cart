package org.carlos.cart.testData

import org.carlos.cart.entities.*

/*
    Improvement --
    Promotional Categories might make more sense
    in the listing rather than in the product.
 */

val Fruit = Category("Fruit")
val Beverage = Category("Beverage")
val LunchDrink = Category("Lunch Drink")
val LunchMeal = Category("Lunch Meal")
val LunchSide = Category("Lunch Side")
val Sandwiches = Category("Sandwich")
val Vegetable = Category("Vegetable")
val Produce = Category("Produce")
val NoodlesPot = Category("Noodles Pot")
val CheapestFree = Category("Cheapest Free")

val juice = ProductListing(
    product = Product(name = "Pineapple Juice", category = listOf(LunchDrink, Beverage, CheapestFree)),
    pricePerUnit = 2.10.asMoney
)

val tunaSweetCornSandwich = ProductListing(
    product = Product(name = "Tuna Sweetcorn Sandwich", category = listOf(LunchMeal, Sandwiches)),
    pricePerUnit = 1.90.asMoney
)

val banana = ProductListing(
    product = Product(name = "banana", category = listOf(Fruit, LunchSide, Produce)),
    pricePerUnit = 1.asMoney
)

val appleHalfPrice = ProductListing(
    product = Product(name = "apple", category = listOf(Fruit),),
    pricePerUnit = 3.asMoney,
    promotion = PercentageOffPriceDiscount(Percentage(.5))
)

val oneBananaBillItem = Bill.Item(
    productListing = banana,
    totalCost = banana.pricePerUnit,
    quantity = 1
)

val orange = ProductListing(
    product = Product(name = "orange", category = listOf(Fruit, Produce, CheapestFree)),
    pricePerUnit = 1.5.asMoney
)
val turnip = ProductListing(
    product = Product(name = "turnip", category = listOf(Vegetable, Produce, CheapestFree)),
    pricePerUnit = 0.2.asMoney
)
val noodlesPot = ProductListing(
    product = Product(name = "noodlesPot", category = listOf(NoodlesPot)),
    pricePerUnit = 10.asMoney
)

val fiftyTwoPenceOff = AmountOffPriceDiscount(
    discountAmount = ".52".asMoney
)
val halfPrice = PercentageOffPriceDiscount(
    Percentage(0.5)
)

val lunchMealDeal = SpecialPriceComboDeal(
    comboPrice = 2.0.asMoney,
    categories = listOf(LunchMeal, LunchDrink, LunchSide)
)
val healthyFoodDeal = SpecialPriceComboDeal(
    comboPrice = 1.asMoney,
    categories = listOf(Vegetable, Fruit)
)

fun makeBananasBillItem(quantity: Int = 1) = Bill.Item(
    productListing = banana,
    totalCost = banana.pricePerUnit * quantity,
    quantity = quantity
)

fun makeOrangeBillItem(quantity: Int = 1) = Bill.Item(
    productListing = orange,
    totalCost = orange.pricePerUnit * quantity,
    quantity = quantity
)

fun makeSandwichBillItem(quantity: Int = 1) = Bill.Item(
    productListing = tunaSweetCornSandwich,
    totalCost = tunaSweetCornSandwich.pricePerUnit * quantity,
    quantity = quantity
)

fun makeJuiceBillItem(quantity: Int = 1) = Bill.Item(
    productListing = juice,
    totalCost = juice.pricePerUnit * quantity,
    quantity = quantity
)

fun makeTurnipBillItem(quantity: Int = 1) = Bill.Item(
    productListing = turnip,
    totalCost = turnip.pricePerUnit * quantity,
    quantity = quantity
)