package org.carlos.cart.entities

/**
 * Negative numbers will be interpreted as ZERO
 * Numbers above 1 will be interpreted as One
 */
data class Percentage(private var _value: Double) {
    val value: Double
        get() = when {
            _value < 0.0 -> 0.0
            _value > 1.0 -> 1.0
            else -> _value
        }
}