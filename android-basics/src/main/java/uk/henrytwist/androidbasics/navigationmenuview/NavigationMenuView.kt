package uk.henrytwist.androidbasics.navigationmenuview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.getColorStateListOrThrow
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import uk.henrytwist.androidbasics.R
import uk.henrytwist.androidbasics.getColorAttr

// TODO Support item margins that don't include item background?
class NavigationMenuView(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    private val menuAdapter: Adapter

    init {

        val styledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NavigationMenuView, 0, 0)

        val horizontalPadding = styledAttributes.getDimension(R.styleable.NavigationMenuView_navigationItemHorizontalPadding, 0F)
        val itemPadding = resources.getDimension(R.dimen.navigation_item_horizontal_padding)
        val padding = (horizontalPadding - itemPadding).toInt().coerceAtLeast(0)

        setPadding(padding, paddingTop, padding, paddingBottom)

        val itemBackgroundColor = styledAttributes.getColorStateListOrThrow(R.styleable.NavigationMenuView_navigationItemBackgroundColor)
        val itemForegroundColor = styledAttributes.getColorStateListOrThrow(R.styleable.NavigationMenuView_navigationItemForegroundColor)

        styledAttributes.recycle()

        val shapeAppearanceModel = ShapeAppearanceModel.builder(context, attributeSet, 0, R.style.NavigationMenuView).build()

        val rippleColor = ColorStateList.valueOf(context.getColorAttr(R.attr.colorControlHighlight))
        menuAdapter = Adapter(shapeAppearanceModel, itemBackgroundColor, itemForegroundColor, rippleColor)

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

        fun addItem(@StringRes nameRes: Int, @DrawableRes iconRes: Int?, checked: Boolean, onItemClicked: () -> Unit) {

            val name = context.getString(nameRes)
            val icon = iconRes?.let { ContextCompat.getDrawable(context, it) }
            addItem(name, icon, checked, onItemClicked)
        }

        fun addItem(name: String, icon: Drawable?, checked: Boolean, onItemClicked: () -> Unit) {

            items.add(MenuItem.Item(name, icon, checked, onItemClicked))
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

    internal class Adapter(
            private val shapeAppearanceModel: ShapeAppearanceModel,
            private val itemBackgroundColor: ColorStateList,
            private val itemForegroundColor: ColorStateList,
            private val rippleColor: ColorStateList
    ) : RecyclerView.Adapter<ViewHolder>() {

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

                MenuItem.VIEW_TYPE_ITEM -> ItemHolder(inflater.inflate(R.layout.navigation_menu_view_item, parent, false), shapeAppearanceModel, itemBackgroundColor, itemForegroundColor, rippleColor)
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

        class ItemHolder(
                itemView: View,
                shapeAppearanceModel: ShapeAppearanceModel,
                itemBackgroundColor: ColorStateList,
                itemForegroundColor: ColorStateList,
                rippleColor: ColorStateList
        ) : ViewHolder(itemView) {

            private val textView = itemView as CheckedTextView

            init {

                val itemMaterialShape = MaterialShapeDrawable(shapeAppearanceModel)
                itemMaterialShape.tintList = itemBackgroundColor

                val mask = MaterialShapeDrawable(shapeAppearanceModel)

                val ripple = RippleDrawable(rippleColor, itemMaterialShape, mask)

                textView.background = ripple
                textView.setTextColor(itemForegroundColor)
                TextViewCompat.setCompoundDrawableTintList(textView, itemForegroundColor)
            }

            fun bind(item: MenuItem.Item) {

                textView.isChecked = item.checked
                textView.setOnClickListener { item.onItemClicked() }

                val icon = item.icon ?: ColorDrawable(Color.TRANSPARENT)
                val iconSize = textView.resources.getDimensionPixelSize(R.dimen.navigation_item_icon_size)
                icon.setBounds(0, 0, iconSize, iconSize)
                textView.setCompoundDrawablesRelative(icon, null, null, null)

                textView.text = item.name
            }
        }

        class DividerHolder(itemView: View) : ViewHolder(itemView) {

            fun bind(menuItem: MenuItem.Divider) {}
        }
    }
}