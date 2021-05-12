package com.mertrizakaradeniz.exploregame.ui.fragments.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.databinding.FragmentFavouriteGameBinding
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
class FavouriteGameFragment : Fragment(R.layout.fragment_favourite_game) {

    private var _binding: FragmentFavouriteGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}