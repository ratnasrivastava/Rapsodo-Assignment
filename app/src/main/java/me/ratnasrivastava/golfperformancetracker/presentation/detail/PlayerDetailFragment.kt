package me.ratnasrivastava.golfperformancetracker.presentation.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlinx.coroutines.launch
import me.ratnasrivastava.golfperformancetracker.R
import me.ratnasrivastava.golfperformancetracker.databinding.FragmentPlayerDetailBinding

@AndroidEntryPoint
class PlayerDetailFragment : Fragment(R.layout.fragment_player_detail) {

    private var _binding: FragmentPlayerDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerDetailViewModel by viewModels()

    private val shotAdapter by lazy { ShotAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerDetailBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.recyclerShots.adapter = shotAdapter
        binding.recyclerShots.setHasFixedSize(true)

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { render(it) }
            }
        }
    }

    private fun render(state: PlayerDetailUiState) {
        binding.progressBar.visibility =
            if (state.isLoading && state.player == null) View.VISIBLE else View.GONE

        state.player?.let { player ->
            binding.collapsingToolbar.title = player.name
            binding.textClub.text = player.club.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.textAvgSpeed.text = getString(R.string.metric_speed, player.avgSpeed)
            binding.textAvgDistance.text = getString(R.string.metric_distance, player.avgDistance)

            Glide.with(binding.imagePlayer)
                .load(player.imageUrl)
                .placeholder(R.drawable.ic_player_placeholder)
                .error(R.drawable.ic_player_placeholder)
                .circleCrop()
                .into(binding.imagePlayer)
        }

        binding.textTopSpeed.text = getString(R.string.metric_speed, state.topBallSpeed)
        binding.textLongestCarry.text = getString(R.string.metric_distance, state.longestCarry)
        binding.textAvgLaunch.text = getString(R.string.metric_angle, state.avgLaunchAngle)

        binding.textShotsHeader.text =
            getString(R.string.shots_header, state.shots.size)

        shotAdapter.submitList(state.shots)

        binding.textShotsEmpty.visibility =
            if (!state.isLoading && state.shots.isEmpty()) View.VISIBLE else View.GONE

        state.errorMessage?.let {
            binding.textError.visibility = View.VISIBLE
            binding.textError.text = it
        } ?: run { binding.textError.visibility = View.GONE }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerShots.adapter = null
        _binding = null
    }
}