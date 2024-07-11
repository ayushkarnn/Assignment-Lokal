package com.ayush.lokalapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayush.lokalapp.R
import com.ayush.lokalapp.datamodels.JobUIModel
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

class JobAdapter(
    private var listOfJobs: MutableList<JobUIModel>,
    private val onClick: OnClickListener
) : RecyclerView.Adapter<JobAdapter.JobDataViewHolder>() {

    private lateinit var context: Context

    class JobDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyImageView: RoundedImageView = itemView.findViewById(R.id.companyImageView)
        val companyName: TextView = itemView.findViewById(R.id.companyNameTv)
        val jobTitle: TextView = itemView.findViewById(R.id.jobTitleTv)
        val jobLocation: TextView = itemView.findViewById(R.id.jobLocationTv)
        val jobPhone: TextView = itemView.findViewById(R.id.phoneTv)
        val jobSalary: TextView = itemView.findViewById(R.id.salaryRangeTv)
        val jobIsOpen: TextView = itemView.findViewById(R.id.openingTv)
        val datePosted: TextView = itemView.findViewById(R.id.datePostedTv)
        val saveJob: ImageView = itemView.findViewById(R.id.SaveImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobDataViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.jobs_layout, parent, false)
        return JobDataViewHolder(view)
    }

    override fun getItemCount(): Int = listOfJobs.size

    fun getItem(position: Int): JobUIModel {
        return listOfJobs[position]
    }

    fun updateJobs(newJobs: List<JobUIModel>) {
        val uniqueJobs = newJobs.distinctBy { it.id }
        listOfJobs.clear()
        listOfJobs.addAll(uniqueJobs)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: JobDataViewHolder, position: Int) {
        val currJob = getItem(position)

        if (currJob != null && !currJob.companyName.isNullOrEmpty()) {
            holder.companyName.text = currJob.companyName
            holder.jobTitle.text = currJob.title
            holder.jobLocation.text = currJob.location
            holder.jobPhone.text = "Phone Number:- ${currJob.phone}"
            holder.jobSalary.text = currJob.salaryRange

            if (currJob.isOpen) {
                holder.jobIsOpen.text = "Accepting Responses"
                holder.jobIsOpen.setTextColor(context.getColor(R.color.green))
            } else {
                holder.jobIsOpen.text = "Not Accepting Responses"
                holder.jobIsOpen.setTextColor(context.getColor(R.color.red))
            }

            if (currJob.isBookmarked) {
                holder.saveJob.setImageResource(R.drawable.ic_saved_1)
            } else {
                holder.saveJob.setImageResource(R.drawable.bookmark)
            }

            holder.datePosted.text = currJob.datePosted

            Glide.with(context)
                .load(currJob.imageUrl)
                .into(holder.companyImageView)

            holder.companyImageView.setOnClickListener {
                onClick.onCardClick(position)
            }

            holder.saveJob.setOnClickListener {
                onClick.onSaveClick(position)
            }

            holder.itemView.visibility = View.VISIBLE
        }
    }

    interface OnClickListener {
        fun onSaveClick(position: Int)
        fun onCardClick(position: Int)
    }
}
