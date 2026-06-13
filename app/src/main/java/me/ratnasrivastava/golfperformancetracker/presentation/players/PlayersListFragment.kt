package me.ratnasrivastava.golfperformancetracker.presentation.players

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.ratnasrivastava.golfperformancetracker.R
import me.ratnasrivastava.golfperformancetracker.databinding.FragmentPlayersListBinding


@AndroidEntryPoint
class PlayersListFragment : Fragment(R.layout.fragment_players_list) {

    private var _binding: FragmentPlayersListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayersViewModel by viewModels()

    private val adapter by lazy {
        PlayerPagingAdapter(onClick = { player -> navigateToDetail(player.id) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayersListBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupSwipeRefresh()
        observePaging()
        observeLoadState()
        observeRefreshError()
    }

    private fun setupRecyclerView() {
        binding.recyclerPlayers.layoutManager = LinearLayoutManager(requireContext())
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
            viewModel.refresh(force = true)
        }
    }

    private fun observePaging() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.players.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    val refresh = loadStates.refresh

                    // Show the swipe spinner only while paging is doing initial load.
                    binding.swipeRefresh.isRefreshing = false
                    binding.progressBar.visibility =
                        if (refresh is LoadState.Loading && adapter.itemCount == 0)
                            View.VISIBLE else View.GONE

                    // Empty state once a load has finished with no items.
                    val nothingToShow =
                        refresh is LoadState.NotLoading && adapter.itemCount == 0
                    binding.textEmpty.visibility =
                        if (nothingToShow) View.VISIBLE else View.GONE
                    binding.textEmpty.text =
                        if (viewModel.query.value.isNotBlank())
                            getString(R.string.players_empty_for_query, viewModel.query.value)
                        else getString(R.string.players_empty)
                }
            }
        }
    }

    private fun observeRefreshError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.refreshError.collectLatest { message ->
                    if (message != null) {
                        binding.textError.visibility = View.VISIBLE
                        binding.textError.text = message
                    } else {
                        binding.textError.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun navigateToDetail(playerId: String) {
        findNavController().navigate(
            R.id.action_players_to_detail,
            bundleOf("playerId" to playerId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerPlayers.adapter = null
        _binding = null
    }
}