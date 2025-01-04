package com.example.tictactoe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tictactoe.logic.CellState
import com.example.tictactoe.logic.GameLogic
import com.example.tictactoe.logic.GameResult

class TicTacToeViewModel : ViewModel() {

    private val gameLogic = GameLogic()

    // LiveData for observing the game board in the model
    private val _board = MutableLiveData(gameLogic.getBoard())

    // Public observable immutable variable exposing the game board for observation by the view
    val board: LiveData<Array<Array<CellState>>> = _board

    // LiveData for observing the current player
    private val _currentPlayer = MutableLiveData(gameLogic.getCurrentPlayer())
    val currentPlayer: LiveData<CellState> = _currentPlayer

    // LiveData for observing the game result
    private val _gameResult = MutableLiveData(GameResult.ONGOING)
    val gameResult: LiveData<GameResult> = _gameResult


    fun makeMove(row: Int, col: Int) {
        if (gameResult.value == GameResult.ONGOING && gameLogic.makeMove(row, col)) {
            val result = gameLogic.checkGameState()
            _gameResult.value = result

            // Update the board
            _board.value = gameLogic.getBoard()

            if (result == GameResult.ONGOING) {
                gameLogic.switchPlayer()
                _currentPlayer.value = gameLogic.getCurrentPlayer()
            }
        }
    }

    fun resetGame() {
        gameLogic.resetGame()
        _board.value = gameLogic.getBoard()
        _currentPlayer.value = gameLogic.getCurrentPlayer()
        _gameResult.value = GameResult.ONGOING
    }
}
