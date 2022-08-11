package edu.upi.ttsGisel.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.upi.ttsGisel.R

class ListMateriAdapter(internal var recyclerViewItemClickListener: RecyclerViewItemClickListener, private val context: Context): RecyclerView.Adapter<ListMateriAdapter.ListMateriViewHolder>() {

    private var mDataset: List<MateriModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ListMateriViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_row_materi, parent, false)

        return ListMateriViewHolder(v)
    }

    override fun onBindViewHolder(listMateriViewHolder: ListMateriViewHolder, i: Int) {
        listMateriViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(materiList: List<MateriModel>) {
        this.mDataset = materiList
        notifyDataSetChanged()
    }

    inner class ListMateriViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var judulMateri: TextView = v.findViewById(R.id.tv_item_judul)
        private var idMateri: TextView = v.findViewById(R.id.tv_id_materi)
        private var materiIcon: ImageView = v.findViewById(R.id.img_item_icon)
        private var lock: FrameLayout = v.findViewById(R.id.lock)

        fun bindItem(materies: MateriModel){
            judulMateri.text = materies.judul
            idMateri.text = materies.idMateri

            Glide.with(itemView.context)
                    .load(materies.icon)
                    .apply(RequestOptions().override(150, 150))
                    .placeholder(R.drawable.ic_baseline_image_search)
                    .error(R.drawable.ic_broken_image_black)
                    .into(materiIcon)

            if (materies.score != "null") lock.visibility = View.GONE
        }

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (lock.visibility == View.VISIBLE){
                messageLock()
                return
            }
            recyclerViewItemClickListener.clickOnItem(mDataset[this.adapterPosition])
        }
    }

    private fun messageLock(){
        val builder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
        builder.setMessage(context.getString(R.string.message_lock_lesson))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok)) { dialogInterface, _ ->
                    dialogInterface.cancel()
                }.show()
    }

    interface RecyclerViewItemClickListener {
        fun clickOnItem(data: MateriModel)
    }

}