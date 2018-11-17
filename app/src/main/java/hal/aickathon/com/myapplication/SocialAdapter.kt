package hal.aickathon.com.myapplication

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hal.aickathon.com.myapplication.model.Social
import kotlinx.android.synthetic.main.view_social_item.view.*

class SocialAdapter(
    val items: ArrayList<Social>,
    val context: Context,
    private val onRecyclerListener: OnRecyclerListener
) : RecyclerView.Adapter<SocialViewHolder>() {

    interface OnRecyclerListener {
        fun onClicked(social: Social, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialViewHolder {
        return SocialViewHolder(LayoutInflater.from(context).inflate(R.layout.view_social_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holderSocial: SocialViewHolder, position: Int) {
        val item = items[position]
        holderSocial.ivImage.setImageResource(item.image)
        holderSocial.tvUser.text = item.user
        holderSocial.tvDesc.text = item.desc
        holderSocial.tvLikes.text = item.likes
        holderSocial.itemView.setOnClickListener { onRecyclerListener.onClicked(item, position) }
    }

}

class SocialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ivImage = view.ivImage
    val tvUser = view.tvUser
    val tvLikes = view.tvLikes
    val tvDesc = view.tvDesc
}