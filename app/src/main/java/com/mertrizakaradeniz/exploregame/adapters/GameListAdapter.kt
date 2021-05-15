package com.mertrizakaradeniz.exploregame.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.databinding.GameItemBinding

class GameListAdapter : RecyclerView.Adapter<GameListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: GameItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GameItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.binding.apply {
            tvName.text = "${currentItem?.name}"
            tvReleased.text = "Released at: ${currentItem?.released}"
            tvRating.text = "Rating: ${currentItem?.rating}"
            val imageLink = currentItem?.imageUrl
            imgGame.load(imageLink) {
                crossfade(true)
            }
            root.setOnClickListener {
                onItemClickListener?.let { it(currentItem!!) }
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    private var onItemClickListener: ((Game) -> Unit)? = null

    fun setOnItemClickListener(listener: (Game) -> Unit) {
        onItemClickListener = listener
    }
}