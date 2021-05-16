package com.mertrizakaradeniz.exploregame.ui.fragments.favorite

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
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.adapters.GameListAdapter
import com.mertrizakaradeniz.exploregame.databinding.FragmentFavouriteGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteGameFragment : Fragment(R.layout.fragment_favourite_game),
    SearchView.OnQueryTextListener {

    private var _binding: FragmentFavouriteGameBinding? = null
    private val binding get() = _binding!!

    private val favoriteGamesAdapter by lazy { GameListAdapter() }
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
        setHasOptionsMenu(true)
        setupRecyclerView()
        setupObservers()
        handleClickEvent()
        setupItemTouchEvent()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
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

    private fun setupRecyclerView() {
        binding.rvFavoriteGame.apply {
            adapter = favoriteGamesAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.getAllFavoriteGames().observe(viewLifecycleOwner, { favoriteGames ->
            favoriteGamesAdapter.differ.submitList(favoriteGames)
        })
    }

    private fun handleClickEvent() {
        favoriteGamesAdapter.setOnItemClickListener { game ->
            val bundle = Bundle().apply {
                putSerializable("game", game)
            }
            findNavController().navigate(
                R.id.action_favouriteGameFragment_to_gameDetailFragment, bundle
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
        viewModel.searchDatabase(searchQuery).observe(this) { list ->
            list?.let {
                favoriteGamesAdapter.differ.submitList(list)
            }
        }
    }

}