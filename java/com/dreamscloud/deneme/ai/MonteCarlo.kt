package com.dreamscloud.deneme.ai

import com.dreamscloud.deneme.utilities.V
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator

class MoveWithValue {
    var nextMove: ArrayList<MoveWithValue> = ArrayList()
    var move: Move? = null
    var totalValue: Int = 0
    var totalVisit: Int = 0
    //Ucbyi tek tek eklemek yerine burda nasıl hesaplatırım ona bak
    var UCB: Float = 0F
}

object M {
    /**
     * This val used in bestMove
     */
    private val firstMoves: ArrayList<MoveWithValue> = ArrayList()

    /**
     * //Cation: BestMove can be null
     */
    fun bestMove(board: Board): Move? {
        if (firstMoves.size == 0) {
            for (move in MoveGenerator.generateLegalMoves(board)) {
                board.doMove(move)
                val value = E.evaluate(board)
                board.undoMove()
                val newMove = MoveWithValue().apply {
                    this.move = move; this.totalValue += value; this.totalVisit++
                }
                newMove.UCB = newMove.totalValue.toFloat() / newMove.totalVisit
                firstMoves.add(newMove)
            }
        }
        val best = firstMoves.maxBy { moveWithValue -> moveWithValue.UCB }

        board.doMove(best!!.move)
        val possibleSecondMoves = MoveGenerator.generateLegalMoves(board)
        //Eğer eşitse olası bütün ihtimaller denenmiştir. Sonrakilere derinliğe geç
        if (best.nextMove.size == possibleSecondMoves.size) {
            //board.undoMove()
            //return best.move
            //Eğer best move dan sonra oyun bitiyorsa yani matsa return et
            if (possibleSecondMoves.size == 0) {
                board.undoMove()
                return best.move
            }
            val secondMoves = best.nextMove
            val bestSecond = secondMoves.minBy { moveWithValue -> moveWithValue.UCB }

            board.doMove(bestSecond!!.move)
            val possibleThirdMoves = MoveGenerator.generateLegalMoves(board)
            if (bestSecond.nextMove.size == possibleThirdMoves.size) {
                board.undoMove()
                board.undoMove()
                V.bestMove = best.move
                return best.move
            } else {
                val moveThird = possibleThirdMoves[bestSecond.nextMove.size]
                board.doMove(moveThird)
                val valueThird = E.evaluate(board)
                board.undoMove()
                val newMoveThird = MoveWithValue().apply {
                    this.move = moveThird
                    this.totalValue += valueThird
                    this.totalVisit++
                }
                newMoveThird.UCB = newMoveThird.totalValue.toFloat() / newMoveThird.totalVisit
                bestSecond.nextMove.add(newMoveThird)
                bestSecond.totalValue += valueThird
                bestSecond.totalVisit++
                bestSecond.UCB = bestSecond.totalValue.toFloat() / bestSecond.totalVisit
                best.totalValue += valueThird
                best.totalVisit++
                best.UCB = best.totalValue.toFloat() / best.totalVisit
            }
            board.undoMove()
        } else {
            val moveSecond = possibleSecondMoves[best.nextMove.size]
            board.doMove(moveSecond)
            val valueSecond = E.evaluate(board)
            board.undoMove()
            val newMoveSecond = MoveWithValue().apply {
                this.move = moveSecond
                this.totalValue += valueSecond
                this.totalVisit++
            }
            newMoveSecond.UCB = newMoveSecond.totalValue.toFloat() / newMoveSecond.totalVisit
            best.nextMove.add(newMoveSecond)
            best.totalValue += valueSecond
            best.totalVisit++
            best.UCB = best.totalValue.toFloat() / best.totalVisit
        }
        board.undoMove()

        //return değerini bestMove(board) ile deiştir.
        //sürekli kendini tekrar edecek taa ki 3. hamlede return edene kadar
        M.bestMove(board)
        //Buraya gelemicek çünkü sürekli tekrar edecek.
        firstMoves.clear()
        return V.bestMove
    }
}