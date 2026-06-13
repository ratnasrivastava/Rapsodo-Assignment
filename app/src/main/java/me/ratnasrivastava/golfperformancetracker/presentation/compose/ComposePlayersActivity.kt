package me.ratnasrivastava.golfperformancetracker.presentation.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import me.ratnasrivastava.golfperformancetracker.presentation.compose.theme.GolfTheme

@AndroidEntryPoint
class ComposePlayersActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GolfTheme {
                PlayersListScreen(
                    onPlayerClick = { playerId ->
                        // For the demo I simply toast the id;
                        // In real app, I'd route into the existing detail screen via an Intent/NavHost.
                        Toast.makeText(
                            this,
                            "Tapped player $playerId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}