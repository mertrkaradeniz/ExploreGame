package com.mertrizakaradeniz.exploregame.ui.favorite

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.adapters.GameListAdapter
import com.mertrizakaradeniz.exploregame.databinding.FragmentFavoriteGameBinding
import com.mertrizakaradeniz.exploregame.ui.main.MainActivity
import com.mertrizakaradeniz.exploregame.utils.Constant
import com.mertrizakaradeniz.exploregame.utils.Constant.REMOVE_GAME_EVENT
import com.mertrizakaradeniz.exploregame.utils.Utils.Companion.logEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteGameFragment : Fragment(R.layout.fragment_favorite_game),
    SearchView.OnQueryTextListener {

    @Inject
    lateinit var firebaseInstance: FirebaseAnalytics

    private val viewModel: FavoriteGameViewModel by viewModels()
    private var _binding: FragmentFavoriteGameBinding? = null
    private val binding get() = _binding!!
    private val favoriteGamesAdapter by lazy { GameListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).hideProgressBar()
        setHasOptionsMenu(true)
        setupObservers()
        setupRecyclerView()
        handleClickEvent()
        setupItemTouchEvent()
        sendEvent()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.maxWidth = Integer.MAX_VALUE
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
        }
        return super.onOptionsItemSelected(item)
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

    private fun setupRecyclerView() {
        binding.rvFavoriteGame.apply {
            adapter = favoriteGamesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupObservers() {
        viewModel.getAllFavoriteGames().observe(viewLifecycleOwner, { favoriteGames ->
            favoriteGamesAdapter.differ.submitList(favoriteGames)
            if (favoriteGames.isEmpty()) {
                binding.apply {
                    lottieAnimationView.setAnimation("gameboy-advance.json")
                    lottieAnimationView.playAnimation()
                    lottieAnimationView.visibility = View.VISIBLE
                    "There is no favorite game".also { TvWarning.text = it }
                    TvWarning.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    lottieAnimationView.visibility = View.GONE
                    "".also { TvWarning.text = it }
                    TvWarning.visibility = View.GONE
                }
            }
        })
    }

    private fun handleClickEvent() {
        favoriteGamesAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putParcelable("game", game)
            }
            findNavController().navigate(
                R.id.action_favoriteGameFragment_to_gameDetailFragment, bundle
            )
        }
    }

    private fun setupItemTouchEvent() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val game = favoriteGamesAdapter.differ.currentList[position]
                viewModel.deleteGame(game)
                Snackbar.make(binding.root, "Successfully deleted article", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Undo") {
                            viewModel.saveGame(game)
                        }
                        show()
                    }
                val bundle = Bundle().apply {
                    putParcelable("game", game)
                }
                logEvent(firebaseInstance, REMOVE_GAME_EVENT, bundle)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteGame)
        }
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAll()
            Toast.makeText(
                requireContext(),
                "Successfully Removed everything!",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchFavoriteGames(searchQuery).observe(this) { list ->
            list?.let {
                favoriteGamesAdapter.differ.submitList(list)
                if (list.isEmpty()) {
                    binding.apply {
                        lottieAnimationView.setAnimation("c-bot.json")
                        lottieAnimationView.visibility = View.VISIBLE
                        "We could not found any result!".also { TvWarning.text = it }
                        TvWarning.visibility = View.VISIBLE
                    }
                } else {
                    binding.apply {
                        lottieAnimationView.visibility = View.GONE
                        "".also { TvWarning.text = it }
                        TvWarning.visibility = View.GONE
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "FavoriteGameFragment"
    }

}