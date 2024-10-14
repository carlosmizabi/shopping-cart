package org.carlos.cart.entities

import java.math.BigDecimal
import java.math.RoundingMode

data class Money(val value: BigDecimal) {

    operator fun plus(other: Money): Money = Money((this.value + other.value).setMoneyScale())

    operator fun minus(other: Money): Money = Money((this.value - other.value).setMoneyScale())

    operator fun times(quantity: Int): Money =
        Money((this.value * BigDecimal(quantity)).setMoneyScale())

    operator fun times(percentage: Percentage): Money =
        Money((this.value * BigDecimal(percentage.value)).setMoneyScale())

    operator fun times(quantity: Double): Money = times(quantity.toInt())

    fun reduceBy(percentage: Percentage): Money {
        val reduction = this * percentage
        return this - reduction
    }

    operator fun div(divisor: Int): Money =
        Money((this.value.divide(BigDecimal(divisor).setMoneyScale())).setMoneyScale())

    operator fun div(divisor: BigDecimal): Money =
        Money((this.value.divide(divisor.setMoneyScale())).setMoneyScale())

    fun isZero(): Boolean = value == BigDecimal.ZERO.setMoneyScale()

    val clampedToZero: Money
        get() = if (value < BigDecimal.ZERO) Money(BigDecimal.ZERO) else this
}

fun BigDecimal.setMoneyScale(): BigDecimal = this.setScale(2, RoundingMode.HALF_UP)
val Int.asMoney get(): Money = this.toString().asMoney
val Double.asMoney get(): Money = this.toString().asMoney
val String.asMoney get(): Money = Money(BigDecimal(this).setMoneyScale())
val BigDecimal.asMoney get(): Money = Money(this.setMoneyScale())