package uk.henrytwist.androidbasics.navigationmenuview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import uk.henrytwist.androidbasics.R

// TODO Finish
class NavigationMenuView(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    private val menuAdapter = Adapter()

    init {

        val itemBackgroundDrawable = MaterialShapeDrawable(context, attributeSet, 0, R.style.NavigationMenuView)
        itemBackgroundDrawable.tintList = ContextCompat.getColorStateList(context, R.color.navigation_menu_view_item_background)
        menuAdapter.itemBackgroundDrawable = itemBackgroundDrawable

        layoutManager = LinearLayoutManager(context)
        adapter = menuAdapter
    }

    inline fun buildMenu(builderAction: Builder.() -> Unit) {

        val builder = Builder(context)
        builderAction(builder)

        setMenuItems(builder.items)
    }

    fun setMenuItems(items: List<MenuItem>) {

        menuAdapter.items = items
        menuAdapter.notifyDataSetChanged()
    }

    class Builder(private val context: Context) {

        val items = mutableListOf<MenuItem>()

        fun addItem(@StringRes nameRes: Int, @DrawableRes iconRes: Int?, onItemClicked: () -> Unit) {

            val name = context.getString(nameRes)
            val icon = iconRes?.let { ContextCompat.getDrawable(context, it) }
            addItem(name, icon, onItemClicked)
        }

        fun addItem(name: String, icon: Drawable?, onItemClicked: () -> Unit) {

            items.add(MenuItem.Item(name, icon, false, onItemClicked))
        }

        fun addItem(item: MenuItem.Item) {

            items.add(item)
        }

        fun addDivider() {

            items.add(MenuItem.Divider)
        }
    }

    sealed class MenuItem(val viewType: Int) {

        class Item(val name: String, val icon: Drawable?, val checked: Boolean, val onItemClicked: () -> Unit) : MenuItem(VIEW_TYPE_ITEM)

        object Divider : MenuItem(VIEW_TYPE_DIVIDER)

        companion object {

            const val VIEW_TYPE_ITEM = 0
            const val VIEW_TYPE_DIVIDER = 1
        }
    }

    internal class Adapter : RecyclerView.Adapter<ViewHolder>() {

        var itemBackgroundDrawable: MaterialShapeDrawable? = null
        var items = listOf<MenuItem>()

        override fun getItemCount(): Int {

            return items.size
        }

        override fun getItemViewType(position: Int): Int {

            return items[position].viewType
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val inflater = LayoutInflater.from(parent.context)
            return when (viewType) {

                MenuItem.VIEW_TYPE_ITEM -> ItemHolder(inflater.inflate(R.layout.navigation_menu_view_item, parent, false), itemBackgroundDrawable)
                MenuItem.VIEW_TYPE_DIVIDER -> DividerHolder(inflater.inflate(R.layout.navigation_menu_view_divider, parent, false))
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val item = items[position]
            when {

                holder is ItemHolder && item is MenuItem.Item -> holder.bind(item)
                holder is DividerHolder && item is MenuItem.Divider -> holder.bind(item)
                else -> throw IllegalArgumentException("Invalid view and item combination")
            }
        }

        class ItemHolder(itemView: View, backgroundDrawable: MaterialShapeDrawable?) : ViewHolder(itemView) {

            private val textView = itemView as CheckedTextView

            init {

                textView.background = backgroundDrawable?.mutate()
            }

            fun bind(item: MenuItem.Item) {

                textView.isChecked = true

                textView.setOnClickListener { item.onItemClicked() }

                item.icon?.let {

                    val iconSize = textView.resources.getDimensionPixelSize(R.dimen.navigation_item_icon_size)
                    it.setBounds(0, 0, iconSize, iconSize)
                    textView.setCompoundDrawablesRelative(it, null, null, null)
                }

                textView.text = item.name
            }
        }

        class DividerHolder(itemView: View) : ViewHolder(itemView) {

            fun bind(menuItem: MenuItem.Divider) {}
        }
    }
}