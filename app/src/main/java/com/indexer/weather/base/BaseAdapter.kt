package com.indexer.weather.base

import android.view.ViewGroup

import android.support.v7.widget.RecyclerView
import java.util.ArrayList

abstract class BaseAdapter<VH : BaseViewHolder, T> : RecyclerView.Adapter<VH> {

    var mItems: MutableList<T>

    /**
     * @param items new array list for adapter
     */
    var items: List<T>?
        get() = mItems
        set(items) {
            this.mItems = items as MutableList<T>
            notifyDataSetChanged()
        }

    constructor() {
        mItems = ArrayList()
    }

    constructor(items: MutableList<T>) {
        this.mItems = items
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract override fun onBindViewHolder(holder: VH, position: Int)

    override fun getItemCount(): Int {
        return mItems.size
    }

    /**
     * @param position position to return Item
     */
    fun getItem(position: Int): T {
        return mItems[position]
    }

    /**
     * @param position position the lyric_item to be added in the adapter
     * @param item the lyric_item to be added to the list
     */
    fun addItem(position: Int, item: T) {
        var current = position
        if (position < 0) {
            return
        }

        if (position < mItems.size) {
            mItems.add(position, item)
        } else {
            mItems.add(item)
            current = mItems.size
        }

        notifyItemInserted(current)
    }

    /**
     * @param position position to remove the lyric_item from the adapter
     */
    fun removeItem(position: Int) {
        this.mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * remove all items from adapter
     */
    fun removeItems() {
        this.mItems.clear()
        notifyItemRangeRemoved(0, mItems.size)
    }

    /**
     * @param items the list to be added to existing list in the adapter
     */
    fun addItems(items: List<T>) {
        val startIndex = this.mItems.size
        this.mItems.addAll(startIndex, items)
        notifyItemRangeInserted(startIndex, items.size)
    }
}