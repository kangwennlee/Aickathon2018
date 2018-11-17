package hal.aickathon.com.myapplication

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import hal.aickathon.com.myapplication.model.DiffBotItem
import kotlinx.android.synthetic.main.view_social_item.view.*

class DiffBotAdapter(
    val items: ArrayList<DiffBotItem>,
    val context: Context,
    private val onRecyclerListener: OnRecyclerListener
) : RecyclerView.Adapter<DiffViewHolder>() {

    interface OnRecyclerListener {
        fun onClicked(diffBotItem: DiffBotItem, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiffViewHolder {
        return DiffViewHolder(LayoutInflater.from(context).inflate(R.layout.view_social_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holderSocial: DiffViewHolder, position: Int) {
        val item = items[position]
        Picasso.get().load(item.itemURL).into(holderSocial.ivImage)
        holderSocial.tvUser.text = "testUser"
        holderSocial.tvDesc.text = item.itemTitle
        holderSocial.tvLikes.text = "100 likes"
        holderSocial.itemView.setOnClickListener { onRecyclerListener.onClicked(item, position) }
    }

}

class DiffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ivImage = view.ivImage
    val tvUser = view.tvUser
    val tvLikes = view.tvLikes
    val tvDesc = view.tvDesc
}