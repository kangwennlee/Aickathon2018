package hal.aickathon.com.myapplication

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hal.aickathon.com.myapplication.model.Item
import kotlinx.android.synthetic.main.view_main_item.view.*

class ItemAdapter(
    val items: ArrayList<Item>,
    val context: Context,
    private val onRecyclerListener: OnRecyclerListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    interface OnRecyclerListener {
        fun onClicked(item: Item, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.view_main_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        val item = items[position]
        holderItem.ivImage.setImageResource(item.image)
        holderItem.tvHeader.text = item.title
        holderItem.tvDesc.text = item.name
        holderItem.tvPrice.text = item.price
        holderItem.itemView.setOnClickListener { onRecyclerListener.onClicked(item, position) }
    }

}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ivImage = view.ivImage
    val tvHeader = view.tvHeader
    val tvDesc = view.tvDesc
    val tvPrice = view.tvPrice
}