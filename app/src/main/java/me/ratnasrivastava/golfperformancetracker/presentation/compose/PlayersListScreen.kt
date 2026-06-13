package me.ratnasrivastava.golfperformancetracker.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import me.ratnasrivastava.golfperformancetracker.R
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.presentation.players.PlayersViewModel
import androidx.compose.ui.res.painterResource

@Composable
fun PlayersListScreen(
    onPlayerClick: (String) -> Unit,
    viewModel: PlayersViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val players = viewModel.players.collectAsLazyPagingItems()

    PlayersListContent(
        query = query,
        players = players,
        onQueryChange = viewModel::onQueryChanged,
        onPlayerClick = onPlayerClick
    )
}

@Composable
fun PlayersListContent(
    query: String,
    players: LazyPagingItems<Player>,
    onQueryChange: (String) -> Unit,
    onPlayerClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = stringResource(R.string.players_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text(stringResource(R.string.players_search_hint)) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag(TestTags.SEARCH_FIELD)
        )

        val refresh = players.loadState.refresh
        val isInitialLoading = refresh is LoadState.Loading && players.itemCount == 0
        val isEmpty = refresh !is LoadState.Loading && players.itemCount == 0

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isInitialLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(TestTags.LOADING)
                    )
                }
                isEmpty -> {
                    Text(
                        text = if (query.isNotBlank())
                            stringResource(R.string.players_empty_for_query, query)
                        else stringResource(R.string.players_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp)
                            .testTag(TestTags.EMPTY)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        modifier = Modifier.testTag(TestTags.LIST)
                    ) {
                        items(
                            count = players.itemCount,
                            key = players.itemKey { it.id }
                        ) { index ->
                            val player = players[index]
                            if (player != null) {
                                PlayerRow(
                                    player = player,
                                    onClick = { onPlayerClick(player.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerRow(
    player: Player,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = player.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_player_placeholder),
                error = painterResource(R.drawable.ic_player_placeholder),
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = player.club.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.player_avg_speed_value, player.avgSpeed),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.label_avg_speed),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

object TestTags {
    const val SEARCH_FIELD = "players_search_field"
    const val LIST = "players_list"
    const val EMPTY = "players_empty"
    const val LOADING = "players_loading"
}