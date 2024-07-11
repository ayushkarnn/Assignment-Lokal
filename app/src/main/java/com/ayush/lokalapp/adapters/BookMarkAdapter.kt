package com.ayush.lokalapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayush.lokalapp.R
import com.ayush.lokalapp.data.local.BookMarkedJobsEntity
import com.ayush.lokalapp.datamodels.JobUIModel

class BookMarkAdapter(
    private var listOfBookMarkJobs: MutableList<BookMarkedJobsEntity>,
    private val onClick: OnClickListener
) : RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder>() {

    private lateinit var context: Context

    class BookMarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookmarkLinear: LinearLayout = itemView.findViewById(R.id.bookmarkLinear)
        val jobTitle: TextView = itemView.findViewById(R.id.wholeTitle)
        val jobLocation: TextView = itemView.findViewById(R.id.locationTitle)
        val jobSalary: TextView = itemView.findViewById(R.id.salaryTitle)
        val dateBookMarked: TextView = itemView.findViewById(R.id.bookMarkedOn)
        val UnBookMarkJob: LinearLayout = itemView.findViewById(R.id.unBookMarkImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMarkViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.saved_jobs_layout, parent, false)
        return BookMarkViewHolder(view)
    }

    override fun getItemCount(): Int = listOfBookMarkJobs.size

    fun getItem(position: Int): BookMarkedJobsEntity {
        return listOfBookMarkJobs[position]
    }

    fun updateJobs(newJobs: List<BookMarkedJobsEntity>) {
        listOfBookMarkJobs.clear()
        listOfBookMarkJobs.addAll(newJobs)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BookMarkViewHolder, position: Int) {
        val currJob = getItem(position)
        holder.jobTitle.text = currJob.title
        holder.jobLocation.text = currJob.location
        holder.jobSalary.text = currJob.salaryRange
        holder.dateBookMarked.text = "Date BookMarked- ${currJob.dateBookMarked}"

        holder.UnBookMarkJob.setOnClickListener {
            onClick.onUnSaveClick(position)
        }
        holder.bookmarkLinear.setOnClickListener {
            onClick.onClick(position)
        }
    }

    interface OnClickListener {
        fun onUnSaveClick(position: Int)
        fun onClick(position: Int)
    }
}
