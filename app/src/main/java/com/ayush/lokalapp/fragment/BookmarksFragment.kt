package com.ayush.lokalapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayush.lokalapp.R
import com.ayush.lokalapp.adapters.BookMarkAdapter
import com.ayush.lokalapp.data.local.BookMarkDatabase
import com.ayush.lokalapp.data.local.BookMarkedJobsEntity
import com.ayush.lokalapp.data.network.JobApiService
import com.ayush.lokalapp.data.network.JobClient
import com.ayush.lokalapp.databinding.FragmentBookmarksBinding
import com.ayush.lokalapp.repository.JobDataRepository
import com.ayush.lokalapp.viewmodel.JobDataViewModel
import com.ayush.lokalapp.viewmodel.JobViewModelFactory

/**
 * A Fragment representing the bookmarked jobs list. It displays jobs that the user has saved/bookmarked.
 * It allows users to view details of bookmarked jobs and remove them from bookmarks.
 */
class BookmarksFragment : Fragment(), BookMarkAdapter.OnClickListener {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: JobDataViewModel
    private lateinit var bookMarkAdapter: BookMarkAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()
        setupRecyclerView()
        observeViewModel()
    }

    /**
     * Initializes the ViewModel for this fragment.
     */
    private fun initializeViewModel() {
        val apiService = JobClient.getInstance().create(JobApiService::class.java)
        val bookMarkDao = BookMarkDatabase.getDatabase(requireContext()).bookMarkDao()
        val factory = JobViewModelFactory(apiService, bookMarkDao)
        viewModel = ViewModelProvider(this, factory).get(JobDataViewModel::class.java)
    }

    /**
     * Sets up the RecyclerView and its adapter.
     */
    private fun setupRecyclerView() {
        bookMarkAdapter = BookMarkAdapter(mutableListOf(), this)
        binding.bookmarkRecyclerView.apply {
            adapter = bookMarkAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


    private fun observeViewModel() {
        viewModel.bookmarkedJobsLiveData.observe(viewLifecycleOwner) { bookmarkedJobs ->
            binding.progessSaved.visibility = View.GONE
            if (bookmarkedJobs.isNullOrEmpty()) {
                binding.noDataFound.visibility = View.VISIBLE
                binding.bookmarkRecyclerView.visibility = View.GONE
            } else {
                binding.noDataFound.visibility = View.GONE
                binding.bookmarkRecyclerView.visibility = View.VISIBLE
                bookMarkAdapter.updateJobs(bookmarkedJobs)
            }
        }
        binding.progessSaved.visibility = View.VISIBLE
        viewModel.fetchBookmarkedJobs()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Handles the click event for removing a job from bookmarks.
     * @param position The position of the clicked item in the RecyclerView
     */
    override fun onUnSaveClick(position: Int) {
        val job = bookMarkAdapter.getItem(position)
        viewModel.deleteBookMark(job.id)
        bookMarkAdapter.notifyItemRemoved(position)
        viewModel.fetchBookmarkedJobs()
    }

    /**
     * Handles the click event for viewing job details.
     * @param position The position of the clicked item in the RecyclerView
     */
    override fun onClick(position: Int) {
        val currJob = bookMarkAdapter.getItem(position)
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
        findNavController().navigate(R.id.action_bookmarksFragment_to_jobDetailsFragment, bundle)
    }
}
