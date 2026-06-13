package me.ratnasrivastava.golfperformancetracker.presentation.detail

import me.ratnasrivastava.golfperformancetracker.domain.model.Shot
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.ratnasrivastava.golfperformancetracker.R
import me.ratnasrivastava.golfperformancetracker.databinding.ItemShotBinding
import java.util.Locale

class ShotAdapter : ListAdapter<Shot, ShotAdapter.ShotViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShotViewHolder {
        val binding = ItemShotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShotViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class ShotViewHolder(
        private val binding: ItemShotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(shot: Shot, shotNumber: Int) {
            val ctx = binding.root.context
            binding.textShotNumber.text = ctx.getString(R.string.shot_number, shotNumber)
            binding.textClubType.text = shot.clubType.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.textBallSpeed.text = ctx.getString(R.string.metric_speed, shot.ballSpeed)
            binding.textLaunchAngle.text = ctx.getString(R.string.metric_angle, shot.launchAngle)
            binding.textCarry.text = ctx.getString(R.string.metric_distance, shot.carryDistance)
            binding.textSpinRate.text = ctx.getString(R.string.metric_spin, shot.spinRate)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Shot>() {
            override fun areItemsTheSame(oldItem: Shot, newItem: Shot): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Shot, newItem: Shot): Boolean =
                oldItem == newItem
        }
    }
}