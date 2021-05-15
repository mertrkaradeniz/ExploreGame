package com.mertrizakaradeniz.exploregame.ui.fragments.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.adapters.GameListAdapter
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.databinding.FragmentFavouriteGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteGameFragment : Fragment(R.layout.fragment_favourite_game) {

    private var _binding: FragmentFavouriteGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteGamesAdapter: GameListAdapter
    private lateinit var favoriteGamesData: List<Game>
    private val viewModel: FavoriteGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        favoriteGamesAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putSerializable("game", game)
            }
            findNavController().navigate(
                R.id.action_favouriteGameFragment_to_gameDetailFragment, bundle
            )
        }

        setupItemTouch()

        viewModel.getAllFavoriteGames().observe(viewLifecycleOwner, { favoriteGames ->
            favoriteGamesAdapter.differ.submitList(favoriteGames)
        })
    }

    private fun setupItemTouch() {
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
                val position = viewHolder.adapterPosition
                val game = favoriteGamesAdapter.differ.currentList[position]
                viewModel.deleteGame(game)
                Snackbar.make(binding.root, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveGame(game)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteGame)
        }
    }

    private fun setupRecyclerView() {
        favoriteGamesAdapter = GameListAdapter()
        binding.rvFavoriteGame.apply {
            adapter = favoriteGamesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}