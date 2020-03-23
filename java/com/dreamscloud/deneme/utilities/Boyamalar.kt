package com.dreamscloud.deneme.utilities

import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import com.dreamscloud.deneme.R
import com.dreamscloud.deneme.SquareButton
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.MoveGenerator
import java.util.*
import kotlin.collections.ArrayList

/**
 * This object contains fun which make button's color
 */
object B {
    fun showPosMoves(activity: AppCompatActivity, board: Board, tag: String){
        val movesLegal = MoveGenerator.generateLegalMoves(board)
        val moves = ArrayList<String>()
        for (move in movesLegal) {
            val hamle = move.toString().toUpperCase(Locale.US)
            val hamleFrom = hamle[0].toString() + hamle[1].toString()
            val hamleTo = hamle[2].toString() + hamle[3].toString()
            if (hamleFrom == tag) {
                possibleBoya(
                    activity,
                    hamleTo
                )
                moves.add(hamleTo)
            }
        }
    }

    fun possibleBoya(activity: AppCompatActivity, possibleKonum: String) {
        val buton = activity.findViewById<TableLayout>(R.id.table_layout)
            .findViewWithTag<SquareButton>(possibleKonum)
        if (F.isDark(possibleKonum)) buton.setBackgroundResource(
            R.drawable.my_button_possible_koyu
        )
        else buton.setBackgroundResource(R.drawable.my_button_possible)
    }

}