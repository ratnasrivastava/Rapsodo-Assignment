package me.ratnasrivastava.golfperformancetracker.presentation.compose

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.presentation.compose.theme.GolfTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import androidx.compose.ui.test.onAllNodesWithTag
import org.junit.Rule
import org.junit.Test

class PlayersListContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val samplePlayers = listOf(
        Player("1", "Jordan Spieth", "driver", 150.0, 280.0, ""),
        Player("2", "Rory McIlroy", "iron", 145.0, 270.0, "")
    )

    @Test
    fun showsPlayers_whenDataPresent() {
        composeRule.setContent {
            GolfTheme {
                val players = flowOf(PagingData.from(samplePlayers))
                    .collectAsLazyPagingItems()
                PlayersListContent(
                    query = "",
                    players = players,
                    onQueryChange = {},
                    onPlayerClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Jordan Spieth").assertIsDisplayed()
        composeRule.onNodeWithText("Rory McIlroy").assertIsDisplayed()
    }

    @Test
    fun tappingPlayer_invokesCallbackWithPlayerId() {
        var clickedId: String? = null

        composeRule.setContent {
            GolfTheme {
                val players = flowOf(PagingData.from(samplePlayers))
                    .collectAsLazyPagingItems()
                PlayersListContent(
                    query = "",
                    players = players,
                    onQueryChange = {},
                    onPlayerClick = { clickedId = it }
                )
            }
        }

        composeRule.onNodeWithText("Jordan Spieth").performClick()
        assertEquals("1", clickedId)
    }

    @Test
    fun typingInSearch_emitsQueryChanges() {
        val typed = StringBuilder()

        composeRule.setContent {
            GolfTheme {
                val queryState = remember { MutableStateFlow("") }
                val query by queryState.collectAsState()
                val players = flowOf(PagingData.from(samplePlayers))
                    .collectAsLazyPagingItems()
                PlayersListContent(
                    query = query,
                    players = players,
                    onQueryChange = {
                        typed.append(it)
                        queryState.value = it
                    },
                    onPlayerClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(TestTags.SEARCH_FIELD).performTextInput("x")
        assert(typed.contains("x"))
    }
}