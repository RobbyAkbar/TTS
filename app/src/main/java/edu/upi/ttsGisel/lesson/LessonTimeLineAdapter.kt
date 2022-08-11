package edu.upi.ttsGisel.lesson

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import edu.upi.ttsGisel.R
import edu.upi.ttsGisel.lesson.menu.QuizActivity
import edu.upi.ttsGisel.lesson.menu.TheoryActivity
import edu.upi.ttsGisel.lesson.menu.VideoActivity
import edu.upi.ttsGisel.ui.home.MateriModel
import edu.upi.ttsGisel.utils.Config
import edu.upi.ttsGisel.utils.VectorDrawableUtils

class LessonTimeLineAdapter (private val data: MateriModel, private var mFeedList: List<LessonTimeLineModel>, private val context: Context) : RecyclerView.Adapter<LessonTimeLineAdapter.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if(!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return TimeLineViewHolder(mLayoutInflater.inflate(R.layout.item_timeline, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        when (timeLineModel.status) {
            Config.INACTIVE -> {
                setMarker(holder, R.drawable.ic_block, android.R.color.holo_red_light)
                holder.status.text = Config.INACTIVE
            }
            Config.ACTIVE -> {
                setMarker(holder, R.drawable.ic_marker_active, android.R.color.holo_blue_light)
                holder.status.text = Config.ACTIVE
            }
            Config.COMPLETED -> {
                setMarker(holder, R.drawable.ic_check_circle, android.R.color.holo_green_light)
                holder.status.text = Config.COMPLETED
            }
        }

        holder.type.text = timeLineModel.type
        holder.message.text = timeLineModel.message
    }

    private fun setMarker(holder: TimeLineViewHolder, drawableResId: Int, color: Int) {
        holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId, ContextCompat.getColor(holder.itemView.context, color))
    }

    fun updateWith(timeLineList: List<LessonTimeLineModel>) {
        this.mFeedList = timeLineList
        notifyDataSetChanged()
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val type: AppCompatTextView = itemView.findViewById(R.id.text_timeline_type)
        val message: AppCompatTextView = itemView.findViewById(R.id.text_timeline_title)
        val status: TextView = itemView.findViewById(R.id.tl_status)
        val timeline: TimelineView = itemView.findViewById(R.id.timeline)

        init {
            timeline.initLine(viewType)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (status.text==Config.INACTIVE) {
                Toast.makeText(context, context.getString(R.string.message_lock_section), Toast.LENGTH_SHORT).show()
                return
            }
            when (type.text) {
                "Video" -> {
                    val video = Intent(context, VideoActivity::class.java)
                    video.putExtra(Config.EXTRA_LESSON, data)
                    context.startActivity(video)
                }
                "Theory" -> {
                    val theory = Intent(context, TheoryActivity::class.java)
                    theory.putExtra(Config.EXTRA_LESSON, data)
                    context.startActivity(theory)
                }
                "Quiz" -> {
                    val quiz = Intent(context, QuizActivity::class.java)
                    quiz.putExtra(Config.EXTRA_LESSON, data)
                    context.startActivity(quiz)
                }
            }
        }
    }
}