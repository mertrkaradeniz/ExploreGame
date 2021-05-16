package com.mertrizakaradeniz.exploregame.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.adapters.GameLoadStateAdapter
import com.mertrizakaradeniz.exploregame.adapters.GamePagingAdapter
import com.mertrizakaradeniz.exploregame.adapters.ViewPagerAdapter
import com.mertrizakaradeniz.exploregame.databinding.FragmentGameListBinding
import com.mertrizakaradeniz.exploregame.ui.main.MainActivity
import com.mertrizakaradeniz.exploregame.utils.Constant
import com.mertrizakaradeniz.exploregame.utils.Constant.DEFAULT_SEARCH_QUERY
import com.mertrizakaradeniz.exploregame.utils.Constant.MIN_QUERY_SEARCH_LENGTH
import com.mertrizakaradeniz.exploregame.utils.Constant.VIEW_PAGER_ITEM_SIZE
import com.mertrizakaradeniz.exploregame.utils.Data.gameList
import com.mertrizakaradeniz.exploregame.utils.Resource
import com.mertrizakaradeniz.exploregame.utils.Utils.Companion.logEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameListFragment : Fragment(R.layout.fragment_game_list) {

    @Inject
    lateinit var firebaseInstance: FirebaseAnalytics

    private val viewModel: GameListViewModel by viewModels()
    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!
    private val gameListAdapter by lazy { GamePagingAdapter() }
    private lateinit var viewPagerAdapter: ViewPagerAdapter

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
        handleClickEvent()
        setupSearch()
        sendEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendEvent() {
        val bundle = Bundle().apply {
            putString(Constant.VIEW_NAME, TAG)
        }
        logEvent(firebaseInstance, Constant.ENTERED_VIEW_EVENT, bundle)
    }

    private fun setupSearch() {
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    clearFocus()
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    if (query != null) {
                        if (query.length >= MIN_QUERY_SEARCH_LENGTH) {
                            hideViewPager()
                            viewModel.searchGames(query)
                        }
                        if (query.length < 3) {
                            showViewPager()
                            binding.rvGameList.scrollToPosition(0)
                            viewModel.searchGames(DEFAULT_SEARCH_QUERY)
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun hideViewPager() {
        binding.apply {
            viewPager.visibility = View.GONE
            dotsIndicator.visibility = View.GONE
        }
    }

    private fun showViewPager() {
        binding.apply {
            viewPager.visibility = View.VISIBLE
            dotsIndicator.visibility = View.VISIBLE
        }
    }

    private fun setupObservers() {
        viewModel.getGameList(requireContext())

        viewModel.games.observe(viewLifecycleOwner) {
            gameListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        viewModel.gameList.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    (requireActivity() as MainActivity).hideProgressBar()
                    gameList = response.data!!.subList(0, VIEW_PAGER_ITEM_SIZE)
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
        viewPagerAdapter = ViewPagerAdapter(gameList)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        viewPagerClickEvent()
        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun viewPagerClickEvent() {
        viewPagerAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putParcelable("game", game)
            }
            findNavController().navigate(R.id.action_gameListFragment_to_gameDetailFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        binding.rvGameList.apply {
            adapter = gameListAdapter.withLoadStateHeaderAndFooter(
                header = GameLoadStateAdapter { gameListAdapter.retry() },
                footer = GameLoadStateAdapter { gameListAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    private fun handleClickEvent() {
        gameListAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putParcelable("game", game)
            }
            findNavController().navigate(R.id.action_gameListFragment_to_gameDetailFragment, bundle)
        }
    }

    companion object {
        private const val TAG = "GameListFragment"
    }
}