
TestCard Technical Test Outline
Build domain objects (UI not required) to model a shopping cart 
system with the following acceptance criteria:

    Multiple products can be purchased in a single basket. 
    (the products can be whatever you wish)
    Products should belong to a Category

We also want to support the following discounts/promotions:

    A general basket % discount (eg 10% off total basket)
    A discount for a specific product (eg 20% off all apples)
    A buy 2 get 1 free promotion (eg buy 2 apples, get 3rd apple free)
    A combination purchase deal (eg buy 1 sandwich and 
    1 apple and 1 drink for £X price)


Only one discount can be applied to a product.
For example: if a product is included in a meal deal or 
buy 2 get 1 free promotion then it should not also be
discounted with a general basket discount.
Approaches to priority are up to you.

Discounts should apply as many times as they are valid.
For example: in the above combination deal a basket
with 3 sandwiches, 3 apples and 4 drinks would 
apply 3 times.

Please use Kotlin as the programming language and 
include unit tests, please also try to be aware 
of edge cases you might need to consider.

This is a general engineering test so giving thought
to maintainability and extensibility is key. We will 
consider some possible extensions 
to the functionality in the technical interview.

We do not expect you to spend longer than 2 - 4 hours.