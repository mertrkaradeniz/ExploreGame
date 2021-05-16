package com.mertrizakaradeniz.exploregame.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mertrizakaradeniz.exploregame.databinding.GameLoadStateFooterBinding

class GameLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<GameLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(private val binding: GameLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

            fun bind(loadState: LoadState) {
                binding.apply {
                    progressBar.isVisible = loadState is LoadState.Loading
                    btnRetry.isVisible = loadState !is LoadState.Loading
                    tvError.isVisible = loadState !is LoadState.Loading
                }
            }
        }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            GameLoadStateFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}