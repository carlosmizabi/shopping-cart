package org.carlos.cart.data

abstract class SimpleRepository<ITEM, ID> {
    abstract fun find(identifier: ID): ITEM?
    protected var items: MutableList<ITEM> = mutableListOf()
    open fun add(item: ITEM): Boolean = items.add(item)
    open fun add(items: List<ITEM>) = this.items.addAll(items)
    open fun remove(item: ITEM): Boolean = items.remove(item)
    open fun clear() {
        items = mutableListOf()
    }
}