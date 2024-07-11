package com.ayush.lokalapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ayush.lokalapp.activity.MainActivity
import com.ayush.lokalapp.databinding.FragmentJobDetailsBinding


class JobDetailsFragment : Fragment() {

    private val args: JobDetailsFragmentArgs by navArgs()

    private lateinit var binding: FragmentJobDetailsBinding

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.companyTitleDetails.text=args.title
        binding.phoneNumberDetails.text=args.phoneNumber
        binding.placeDetails.text=args.place
        binding.jobTypeDetails.text=args.jobType
        binding.experienceDetails.text=args.experience
        binding.qualificationDetails.text=args.qualification
        binding.dateVacaniesDetails.text=args.totalVacancies
        binding.totalViewsDetails.text=args.totalViews
        binding.shareDetails.text=args.totalShare
        binding.salaryDetails.text=args.salary
        binding.datePostedDetails.text=args.datePosted

        binding.chatOnWhatsapp.setOnClickListener {
            val whatsappLink = args.whatsapplink
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappLink))
            startActivity(intent)
        }

        activity?.let { act ->
            if (act is MainActivity) {
                mainActivity = act
            } else {
                throw IllegalStateException("Activity must be MainActivity")
            }
        }
        mainActivity.setBottomNavigationVisibility(false)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity.setBottomNavigationVisibility(true)
    }


}
