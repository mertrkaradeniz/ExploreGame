package com.mertrizakaradeniz.exploregame.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.databinding.ViewPagerItemBinding

class ViewPagerAdapter(private val gameList: List<Game>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ViewPagerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewPagerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = gameList[position]

        holder.binding.apply {
            imgSlide.load(currentItem.imageUrl) {
                transformations(RoundedCornersTransformation(24f))
                crossfade(true)
            }

            root.setOnClickListener {
                Log.d("SA", "tiklandi")
                onItemClickListener?.let { it(currentItem) }
            }
        }
    }

    private var onItemClickListener: ((Game) -> Unit)? = null

    fun setOnItemClickListener(listener: (Game) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount() = 3
}