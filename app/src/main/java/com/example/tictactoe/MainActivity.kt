package com.example.tictactoe

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.tictactoe.viewmodel.TicTacToeViewModel
import com.example.tictactoe.logic.GameResult
import com.example.tictactoe.logic.CellState

class MainActivity : AppCompatActivity() {

    private val viewModel: TicTacToeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI elements from the XML layout buttons array - the game board
        val buttonsArray = arrayOf(
            arrayOf(
                findViewById<Button>(R.id.button11),
                findViewById<Button>(R.id.button12),
                findViewById<Button>(R.id.button13)
            ),
            arrayOf(
                findViewById<Button>(R.id.button21),
                findViewById<Button>(R.id.button22),
                findViewById<Button>(R.id.button23)
            ),
            arrayOf(
                findViewById<Button>(R.id.button31),
                findViewById<Button>(R.id.button32),
                findViewById<Button>(R.id.button33)
            )
        )

        // Winner or turn banner
        val bannerText = findViewById<TextView>(R.id.banner)
        val playAgainButton = findViewById<Button>(R.id.playButton)

        // Observer to update the game board buttons
        viewModel.board.observe(this) { board ->
            for (row in 0..2) {
                for (col in 0..2) {
                    val cellState = board[row][col]
                    val button = buttonsArray[row][col]
                    button.text = when (cellState) {
                        CellState.PLAYER_X -> "X"
                        CellState.PLAYER_O -> "O"
                        else -> ""
                    }

                    // After a button has a value (X or O) it should not be enabled for a second click
                    button.isEnabled = (cellState == CellState.EMPTY)
                }
            }
        }

        viewModel.gameResult.observe(this) { gameState ->
            // Update the banner text based on the game state
            bannerText.text = when (gameState) {
                GameResult.PLAYER_X_WINS -> "Player X Wins!"
                GameResult.PLAYER_O_WINS -> "Player O Wins!"
                GameResult.DRAW -> "It's a Draw!"
                GameResult.ONGOING -> {
                    // Defer to the current player's turn if the game is ongoing
                    val player = viewModel.currentPlayer.value
                    "${if (player == CellState.PLAYER_X) "X" else "O"}'s Turn"
                }
                null -> "Game state unavailable"
            }

            // The play again button should only be enabled when the game is over
            playAgainButton.isVisible = (gameState != GameResult.ONGOING)
            playAgainButton.isEnabled = (gameState != GameResult.ONGOING)
        }

        viewModel.currentPlayer.observe(this) { player ->
            // Only update the turn banner when the game is ongoing
            if (viewModel.gameResult.value == GameResult.ONGOING) {
                if (player == CellState.PLAYER_X ){
                    bannerText.text = "X's Turn"
                }
                else{
                    bannerText.text = "O's Turn"
                }
            }
        }


        // Set click listeners for game board buttons
        for (row in 0..2) {
            for (col in 0..2) {
                buttonsArray[row][col].setOnClickListener {
                    viewModel.makeMove(row, col)
                }
            }
        }

        // Play Again button resets the game
        playAgainButton.setOnClickListener {
            viewModel.resetGame()
        }
    }
}
