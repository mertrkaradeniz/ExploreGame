package com.mertrizakaradeniz.exploregame.ui.detail

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.mertrizakaradeniz.exploregame.R
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.databinding.FragmentGameDetailBinding
import com.mertrizakaradeniz.exploregame.ui.main.MainActivity
import com.mertrizakaradeniz.exploregame.utils.Constant.ENTERED_VIEW_EVENT
import com.mertrizakaradeniz.exploregame.utils.Constant.GAME_KEY
import com.mertrizakaradeniz.exploregame.utils.Constant.REMOVE_GAME_EVENT
import com.mertrizakaradeniz.exploregame.utils.Constant.SAVE_GAME_EVENT
import com.mertrizakaradeniz.exploregame.utils.Constant.VIEW_NAME
import com.mertrizakaradeniz.exploregame.utils.Resource
import com.mertrizakaradeniz.exploregame.utils.Utils.Companion.logEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameDetailFragment : Fragment(R.layout.fragment_game_detail) {

    @Inject
    lateinit var firebaseInstance: FirebaseAnalytics

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameDetailViewModel by viewModels()
    private lateinit var game: Game
    private var isFavorite: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        game = arguments?.get(GAME_KEY) as Game
        setupObservers()
        sendEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendEvent() {
        val bundle = Bundle().apply {
            putString(VIEW_NAME, TAG)
        }
        logEvent(firebaseInstance, ENTERED_VIEW_EVENT, bundle)
    }

    private fun setupObservers() {
        viewModel.fetchGameDetail(requireContext(), game.id.toString())

        viewModel.checkGameIsFavorite(game.id).observe(viewLifecycleOwner) { list ->
            list?.let {
                Log.d("GameDetailFragment", list.isEmpty().toString())
                isFavorite = list.isNotEmpty()
                setupFab()
            }
        }

        viewModel.gameDetail.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    (requireActivity() as MainActivity).hideProgressBar()
                    if (response.data != null) {
                        setGameDetail(response.data)
                        binding.clRoot.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    (requireActivity() as MainActivity).hideProgressBar()
                    AlertDialog.Builder(requireContext())
                        .setMessage(response.message)
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            viewModel.fetchGameDetail(requireContext(), game.id.toString())
                        }.show()
                }
                is Resource.Loading -> {
                    (requireActivity() as MainActivity).showProgressBar()
                    binding.clRoot.visibility = View.GONE
                }
            }
        }
    }

    private fun setupFab() {
        if (isFavorite) {
            binding.fabAddFavourite.setImageResource(R.drawable.ic_delete)
        } else {
            binding.fabAddFavourite.setImageResource(R.drawable.ic_favorite_filled)
        }
        handleClickEvent()
    }

    private fun handleClickEvent() {
        binding.fabAddFavourite.apply {
            val bundle = Bundle()
            bundle.putParcelable("game", game)
            setOnClickListener {
                if (isFavorite) {
                    setImageResource(R.drawable.ic_delete)
                    game.isFavorite = false
                    isFavorite = false
                    viewModel.saveFavoriteGame(game)
                    Snackbar.make(binding.root, "Game unsaved successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    logEvent(firebaseInstance, SAVE_GAME_EVENT, bundle)
                } else {
                    setImageResource(R.drawable.ic_favorite_filled)
                    game.isFavorite = true
                    isFavorite = true
                    viewModel.saveFavoriteGame(game)
                    Snackbar.make(binding.root, "Game saved successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    logEvent(firebaseInstance, REMOVE_GAME_EVENT, bundle)
                }
            }
        }
    }

    private fun setGameDetail(gameDetailResponse: Game) {
        binding.apply {
            imgDetailGame.load(gameDetailResponse.imageUrl) {
                crossfade(true)
            }
            tvDetailName.text = gameDetailResponse.name
            "Release data: ${gameDetailResponse.released}".also {
                tvDetailReleased.text = it
            }
            "Metacritic date: ${gameDetailResponse.metacritic}".also {
                tvDetailMetacritic.text = it
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvDetailDescription.text = Html.fromHtml(
                    gameDetailResponse.description,
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                tvDetailDescription.text =
                    gameDetailResponse.description?.let {
                        HtmlCompat.fromHtml(
                            it,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    }
            }
        }
    }

    companion object {
        private const val TAG = "GameDetailFragment"
    }
}