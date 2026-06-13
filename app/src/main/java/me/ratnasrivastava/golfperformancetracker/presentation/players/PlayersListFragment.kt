package me.ratnasrivastava.golfperformancetracker.presentation.players

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.ratnasrivastava.golfperformancetracker.R
import me.ratnasrivastava.golfperformancetracker.databinding.FragmentPlayersListBinding


@AndroidEntryPoint
class PlayersListFragment : Fragment(R.layout.fragment_players_list) {

    private var _binding: FragmentPlayersListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayersViewModel by viewModels()

    private val adapter by lazy {
        PlayerAdapter(onClick = ::navigateToDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayersListBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupSwipeRefresh()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.recyclerPlayers.adapter = adapter
        binding.recyclerPlayers.setHasFixedSize(true)
    }

    private fun setupSearch() {
        binding.inputSearch.doAfterTextChanged { text ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun collect(function: Any) {}

    private fun render(state: PlayersUiState) {
        binding.swipeRefresh.isRefreshing = state.isLoading && state.players.isNotEmpty()
        binding.progressBar.visibility =
            if (state.isLoading && state.players.isEmpty()) View.VISIBLE else View.GONE

        adapter.submitList(state.players)

        // Empty state: nothing to show and not loading.
        binding.textEmpty.visibility = if (state.isEmpty) View.VISIBLE else View.GONE
        binding.textEmpty.text = if (state.query.isNotBlank()) {
            getString(R.string.players_empty_for_query, state.query)
        } else {
            getString(R.string.players_empty)
        }

        // Error message (shown alongside any cached data).
        state.errorMessage?.let { message ->
            binding.textError.visibility = View.VISIBLE
            binding.textError.text = message
        } ?: run {
            binding.textError.visibility = View.GONE
        }
    }

    private fun navigateToDetail(playerId: String) {
        val args = Bundle().apply { putString("playerId", playerId) }
        // TODO: write navigation
    }

    private fun navigateToDetail(player: me.ratnasrivastava.golfperformancetracker.domain.model.Player) {
        navigateToDetail(player.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid leaking the RecyclerView/binding across view recreation.
        binding.recyclerPlayers.adapter = null
        _binding = null
    }
}