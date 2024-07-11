package com.ayush.lokalapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayush.lokalapp.R
import com.ayush.lokalapp.adapters.JobAdapter
import com.ayush.lokalapp.data.local.BookMarkDatabase
import com.ayush.lokalapp.data.local.BookMarkedJobsEntity
import com.ayush.lokalapp.data.network.JobApiService
import com.ayush.lokalapp.data.network.JobClient
import com.ayush.lokalapp.databinding.FragmentJobsBinding
import com.ayush.lokalapp.datamodels.JobUIModel
import com.ayush.lokalapp.repository.JobDataRepository
import com.ayush.lokalapp.viewmodel.JobDataViewModel
import com.ayush.lokalapp.viewmodel.JobViewModelFactory
import com.ayush.lokalapp.utilities.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * A Fragment representing the job listings. It interacts with the ViewModel to fetch and display job data.
 * It also handles user interactions like bookmarking and viewing job details.
 */
class JobsFragment : Fragment(), JobAdapter.OnClickListener {

    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobAdapter: JobAdapter
    private lateinit var viewModel: JobDataViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()
        observeViewModel()
        viewModel.fetchCompanyDetails(1)

        jobAdapter = JobAdapter(mutableListOf(), this)
        binding.jobRecyclerView.apply {
            adapter = jobAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Initializes the ViewModel for this fragment.
     */
    private fun initializeViewModel() {
        val apiService = JobClient.getInstance().create(JobApiService::class.java)
        val bookMarkDao = BookMarkDatabase.getDatabase(requireContext()).bookMarkDao()
        val factory = JobViewModelFactory(apiService, bookMarkDao)
        viewModel = ViewModelProvider(this, factory)[JobDataViewModel::class.java]
    }

    /**
     * Observes changes in the ViewModel LiveData and updates the UI accordingly.
     */
    private fun observeViewModel() {
        viewModel.jobResponseLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progessJobs.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progessJobs.visibility = View.GONE
                    resource.data?.let { jobResponse ->
                        lifecycleScope.launch {
                            val jobUIModels = jobResponse.results.mapNotNull { result ->
                                result.primary_details?.let { primaryDetails ->
                                    val isBookmarked = viewModel.isJobBookmarked(result.id)
                                    JobUIModel(
                                        id = result.id,
                                        companyName = result.company_name ?: "",
                                        title = result.title ?: "",
                                        location = primaryDetails.Place ?: "",
                                        phone = result.whatsapp_no ?: "",
                                        salaryRange = "Compensation: ${result.salary_min} - ${result.salary_max}",
                                        isOpen = result.status == 1,
                                        isBookmarked = isBookmarked,
                                        datePosted = getRelativeDate(result.created_on),
                                        imageUrl = result.creatives.firstOrNull()?.thumb_url ?: "",
                                        jobType = primaryDetails.Job_Type ?: "",
                                        jobQualification = primaryDetails.Qualification ?: "",
                                        jobExperience = primaryDetails.Experience ?: "",
                                        totalViews = result.views.toString(),
                                        totalShare = result.shares.toString(),
                                        totalVacancies = result.job_tags.firstOrNull()?.value ?: "",
                                        whatsAppLink = result.contact_preference.whatsapp_link ?: ""
                                    )
                                }
                            }.distinctBy { it.id }
                            jobAdapter.updateJobs(jobUIModels)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progessJobs.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Converts a date string to a relative date string (e.g. "Posted Today", "Posted Yesterday").
     * @param createdOn The date string to convert
     * @return A string representing the relative date
     */
    private fun getRelativeDate(createdOn: String?): String {
        createdOn?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.getDefault())
            val currentDate = Calendar.getInstance().time
            val date = dateFormat.parse(createdOn)

            date?.let {
                val diff = currentDate.time - date.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24

                return when (days) {
                    0L -> "Posted Today"
                    1L -> "Posted Yesterday"
                    in 2..7 -> "Posted more than a week ago"
                    in 8..30 -> "Posted more than a month ago"
                    else -> "Posted more than a month ago"
                }
            }
        }
        return ""
    }

    /**
     * Handles the click event for saving a job.
     * @param position The position of the clicked item in the RecyclerView
     */
    override fun onSaveClick(position: Int) {
        val job = jobAdapter.getItem(position)
        val jobEntity = BookMarkedJobsEntity(
            id = job.id,
            companyName = job.companyName,
            title = job.title,
            location = job.location,
            phone = job.phone,
            salaryRange = job.salaryRange,
            datePosted = job.datePosted,
            jobType = job.jobType,
            jobExperience = job.jobExperience,
            jobQualification = job.jobQualification,
            totalVacancies = job.totalVacancies,
            totalViews = job.totalViews,
            totalShare = job.totalShare,
            whatsAppLink = job.whatsAppLink,
            dateBookMarked = getCurrentDate()
        )

        lifecycleScope.launch {
            val isBookmarked = viewModel.isJobBookmarked(job.id)
            if (isBookmarked) {
                Toast.makeText(requireContext(), "Job is already saved", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertBookMark(jobEntity)
                job.isBookmarked = true
                jobAdapter.notifyItemChanged(position)
            }
        }
    }

    /**
     * Handles the click event for viewing job details.
     * @param position The position of the clicked item in the RecyclerView
     */
    override fun onCardClick(position: Int) {
        val currJob = jobAdapter.getItem(position)
        val bundle = Bundle().apply {
            putString("title", currJob.title)
            putString("phoneNumber", currJob.phone)
            putString("place", currJob.location)
            putString("jobType", currJob.jobType)
            putString("experience", currJob.jobExperience)
            putString("qualification", currJob.jobQualification)
            putString("totalVacancies", currJob.totalVacancies)
            putString("totalViews", currJob.totalViews)
            putString("totalShare", currJob.totalShare)
            putString("whatsapplink", currJob.whatsAppLink)
            putString("datePosted", currJob.datePosted)
            putString("salary", currJob.salaryRange)
        }
        findNavController().navigate(R.id.action_jobsFragment_to_jobDetailsFragment, bundle)
    }

    /**
     * Gets the current date formatted as "dd-MM-yyyy".
     * @return The current date as a string
     */
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
