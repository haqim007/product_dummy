package dev.haqim.productdummy.feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.haqim.productdummy.R
import dev.haqim.productdummy.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAboutBinding.inflate(layoutInflater, container, false)

        requireActivity().title = requireContext().getString(R.string.about)
        
        return binding.root
    }
}