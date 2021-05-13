package com.mertrizakaradeniz.exploregame.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.databinding.GameItemBinding

class GamePagedAdapter : PagingDataAdapter<Game, GamePagedAdapter.ViewHolder>(diffCallback) {

    inner class ViewHolder(val binding: GameItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.binding.apply {
            tvName.text = "${currentItem?.name}"
            tvReleased.text = "Released at: ${currentItem?.released}"
            tvRating.text = "Rating: ${currentItem?.rating}"

            val imageLink = currentItem?.imageUrl
            imgGame.load(imageLink) {
                crossfade(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GameItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
