package com.mertrizakaradeniz.exploregame.ui.fragments.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.adapters.GameListAdapter
import com.mertrizakaradeniz.exploregame.adapters.GamePagedAdapter
import com.mertrizakaradeniz.exploregame.adapters.ViewPagerAdapter
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.databinding.FragmentGameListBinding
import com.mertrizakaradeniz.exploregame.ui.main.MainActivity
import com.mertrizakaradeniz.exploregame.utils.Constant.QUERY_PAGE_SIZE
import com.mertrizakaradeniz.exploregame.utils.Constant.VIEW_PAGER_ITEM_SIZE
import com.mertrizakaradeniz.exploregame.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameListFragment : Fragment(R.layout.fragment_game_list) {

    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameListAdapter: GamePagedAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var gameListData: List<Game>
    private val viewModel: GameListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclerView()
        loadData()

        viewModel.getGameList(requireContext())
    }

    private fun setupObservers() {

        viewModel.gameList.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    (requireActivity() as MainActivity).hideProgressBar()
                    gameListData = response.data!!
                    setupViewPager()
                }
                is Resource.Error -> {
                    (requireActivity() as MainActivity).hideProgressBar()
                    AlertDialog.Builder(requireContext())
                        .setMessage(response.message)
                        .setCancelable(false)
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            viewModel.getGameList(requireContext())
                        }.show()
                }
                is Resource.Loading -> {
                    (requireActivity() as MainActivity).showProgressBar()
                }
            }
        })
    }

    private fun setupViewPager() {

        viewPagerAdapter = ViewPagerAdapter(gameListData.subList(0,VIEW_PAGER_ITEM_SIZE))
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        viewPagerAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putSerializable("game", game)
            }
            findNavController().navigate(R.id.action_gameListFragment_to_gameDetailFragment, bundle)
        }
        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.listData.collect { pagingData ->
                gameListAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        gameListAdapter = GamePagedAdapter()
        binding.rvGameList.apply {
            adapter = gameListAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
        gameListAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putSerializable("game", game)
            }
            findNavController().navigate(R.id.action_gameListFragment_to_gameDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}