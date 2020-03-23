package com.dreamscloud.deneme.utilities

import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import com.dreamscloud.deneme.H
import com.dreamscloud.deneme.R
import com.dreamscloud.deneme.SquareButton
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator

object O {
    fun myOnClick(activity: AppCompatActivity, gameMod: String, tag: String) {
        if (!V.vsComputer || V.computerPlayed) {
            val buton = activity.findViewById<TableLayout>(R.id.table_layout)
                .findViewWithTag<SquareButton>(tag)
            val possibleMoves = MoveGenerator.generateLegalMoves(V.board)
            val piece = V.board.getPiece(F.square(tag))
            if (piece.toString() != "NONE" || V.secili_ilk != "") {
                F.paintBoard(activity)
                if (piece.toString() != "NONE") {
                    if (piece.pieceSide == V.board.sideToMove) {
                        buton.setBackgroundResource(R.drawable.my_button_selected)
                        B.showPosMoves(activity, V.board, tag)
                        V.secili_ilk = tag
                    } else {
                        if (V.secili_ilk != "") {
                            val move = Move(F.square(V.secili_ilk), F.square(tag))
                            if (possibleMoves.contains(move)) {
                                V.computerPlayed = false
                                H.hamleYap(activity, tag, gameMod)
                            } else {
                                val promotionWhite =
                                    Move(F.square(V.secili_ilk), F.square(tag), Piece.WHITE_QUEEN)
                                val promotionBlack = Move(F.square(V.secili_ilk), F.square(tag), Piece.BLACK_QUEEN)
                                if (possibleMoves.contains(promotionWhite) || possibleMoves.contains(promotionBlack)) {
                                    V.computerPlayed = false
                                    H.hamleYap(activity, tag, gameMod)
                                }
                            }
                        } else T.toast(activity, 1)
                    }
                } else {
                    val move = Move(F.square(V.secili_ilk), F.square(tag))
                    if (possibleMoves.contains(move)) {
                        V.computerPlayed = false
                        H.hamleYap(activity, tag, gameMod)
                    } else {
                        //Hamle normal hamle değil ama, Hamle beyaz ya da siyah promotion içeriyorsa.
                        val promotionWhite = Move(F.square(V.secili_ilk), F.square(tag), Piece.WHITE_QUEEN)
                        val promotionBlack = Move(F.square(V.secili_ilk), F.square(tag), Piece.BLACK_QUEEN)
                        if (possibleMoves.contains(promotionWhite) || possibleMoves.contains(promotionBlack)) {
                            V.computerPlayed = false
                            H.hamleYap(activity, tag, gameMod)
                        }
                    }
                }
            }
        } else T.toast(activity, 2)
    }
}