package me.ratnasrivastava.golfperformancetracker.presentation.players

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.ratnasrivastava.golfperformancetracker.R
import me.ratnasrivastava.golfperformancetracker.databinding.ItemPlayerBinding
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import java.util.Locale

class PlayerAdapter(
    private val onClick: (Player) -> Unit
) : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlayerViewHolder(
        private val binding: ItemPlayerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            binding.textPlayerName.text = player.name
            binding.textPlayerClub.text = player.club.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.textPlayerSpeed.text = binding.root.context.getString(
                R.string.player_avg_speed_value,
                player.avgSpeed
            )

            Glide.with(binding.imagePlayer)
                .load(player.imageUrl)
                .placeholder(R.drawable.ic_player_placeholder)
                .error(R.drawable.ic_player_placeholder)
                .circleCrop()
                .into(binding.imagePlayer)

            binding.root.setOnClickListener { onClick(player) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Player>() {
            override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean =
                oldItem == newItem
        }
    }
}