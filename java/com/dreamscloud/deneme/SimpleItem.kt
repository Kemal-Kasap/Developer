package com.dreamscloud.deneme

import android.view.View
import android.widget.Button
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

open class SimpleItem : AbstractItem<SimpleItem.ViewHolder>() {
    var name: String? = null
    var mail: String? = null
    var online: String? = null
    var playing: String? = null
    var teklifeden: String? = null

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.recylerViewim

    /** defines the layout which will be used for this item in the list  */
    override val layoutRes: Int
        get() = R.layout.list_item

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<SimpleItem>(view) {
        var name: Button = view.findViewById(R.id.buton)
        var status: Button = view.findViewById(R.id.buton2)

        override fun bindView(item: SimpleItem, payloads: MutableList<Any>) {
            name.text = item.name
            if (item.online == "true") {
                if (item.playing == "true") {
                    status.setBackgroundResource(R.drawable.color_yellow)
                } else {
                    status.setBackgroundResource(R.drawable.color_green)
                }
            } else {
                status.setBackgroundResource(R.drawable.color_red)
            }
        }

        override fun unbindView(item: SimpleItem) {
            name.text = null
            status.text = null
            status.setBackgroundResource(0)
        }
    }
}