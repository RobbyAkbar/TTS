package edu.upi.ttsGisel.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.upi.ttsGisel.R

class ListLeadAdapter : RecyclerView.Adapter<ListLeadAdapter.ListViewHolder>() {

    private var listLead: List<LeadModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_leader, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, point, photo) = listLead[position]
        Glide.with(holder.itemView.context)
                .load(photo)
                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.ic_baseline_image_search)
                .error(R.drawable.ic_broken_image_black)
                .into(holder.imgPhoto)
        holder.tvName.text = name
        holder.tvPoint.text = point
        holder.tvRank.text = (position+1).toString()
    }

    override fun getItemCount(): Int {
        return listLead.size
    }

    fun updateWith(leadList: List<LeadModel>) {
        this.listLead = leadList
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvPoint: TextView = itemView.findViewById(R.id.tv_item_point)
        var tvRank: TextView = itemView.findViewById(R.id.tvRank)
    }
}