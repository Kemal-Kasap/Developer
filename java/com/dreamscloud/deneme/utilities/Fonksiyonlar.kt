package com.dreamscloud.deneme.utilities

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.dreamscloud.deneme.R
import com.dreamscloud.deneme.SquareButton
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_offline_oyun.*

object V {
    //Sil deneme için
    var zorluk = 0
    var bestMove: Move? = null

    var tahtaDuzMu = true
    var ihtimallerLegal = ArrayList<String>()
    var ihtimallerKalan = ArrayList<String>()
    var board: Board = Board()
    var secili_ilk = ""
    var moveCount = 0
    val ref = FirebaseDatabase.getInstance().reference

    //Aşağıdakiler online variable lar
    var onlineGame = false
    var kurucuMu = false
    var kurucu = ""
    var davetli = ""
    var kurucuMail = ""
    var davetliMail = ""
    var onlineHamlesayisi: Long = 0
    var onlineHamle = ""
    var promotion = 0
    var teklifduzenleyici = 0
    var teklifedenoynabaslamis = ""
    var kurucuTaraf = "Beyaz"
    // kurucu taraf internete yazmak için, kurucu tarafın rangi intenetten almak için kullanıldı
    var kurucuTarafRenk = ""
    // seslendirmedeki var
    var sayi = 1

    // Bilgisayarla oynamak için
    var vsComputer = false
    var computerPlayed = false

    fun resetValues() {
        V.apply {
            tahtaDuzMu = true
            ihtimallerLegal = ArrayList<String>()
            ihtimallerKalan = ArrayList<String>()
            board = Board()
            secili_ilk = ""
            moveCount = 0

            //Aşağıdakiler online variable lar
            onlineGame = false
            kurucuMu = false
            kurucu = ""
            davetli = ""
            kurucuMail = ""
            davetliMail = ""
            onlineHamlesayisi = 0
            onlineHamle = ""
            promotion = 0
            teklifduzenleyici = 0
            teklifedenoynabaslamis = ""
            kurucuTaraf = "Beyaz"
            kurucuTarafRenk = ""

        }
    }
}

object F {

    fun oyunBitti(activity: AppCompatActivity, winner: String): AlertDialog.Builder {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.game_over))
        val message = if (winner == "") "Game is draw"
        else "$winner win"
        builder.setMessage(message)
        builder.setPositiveButton(activity.getString(R.string.new_game)) { _, _ ->
            V.computerPlayed = true
            V.board = Board()
            put_piece(activity, V.board)
        }
        builder.setNeutralButton("Main Menu") { _, _ ->
            activity.finish()
        }

        object : CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (winner == "") MediaPlayer.create(activity, R.raw.berabere).start()
                else {
                    if (winner == Side.WHITE.toString()) MediaPlayer.create(
                        activity,
                        R.raw.beyaz
                    ).start()
                    else MediaPlayer.create(activity, R.raw.siyah).start()
                    object : CountDownTimer(300, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            MediaPlayer.create(activity, R.raw.kazandi).start()
                        }
                    }.start()
                }
            }
        }.start()
        return builder
    }

    fun tahta_ciz(activity: AppCompatActivity, duz: Boolean) {
        yenilenler(activity, top = true)
        yenilenler(activity, top = false)
        //YENİ TAHTA ÇİZMEDEN ÖNCE ESKİSİNİ KALDIR
        activity.table_layout.removeAllViews()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val isChecked = sharedPreferences.getBoolean("cerceve", false)
        if (isChecked) activity.table_layout.addView(harfCerceve(activity, duz))
        val sayilar = if (duz) 8 downTo 1 else 1..8
        for (i in sayilar) {
            val tableRow = TableRow(activity)
            tableRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
            )

            if (isChecked) tableRow.addView(rakamCerceve(activity, i.toString()))
            val liste = if (duz) listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')
            else listOf('H', 'G', 'F', 'E', 'D', 'C', 'B', 'A')
            for (j in liste) {
                val buton = SquareButton(activity)
                val butonParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                butonParams.weight = 1F
                buton.layoutParams = butonParams
                buton.tag = "$j$i"
                tableRow.addView(buton)
            }
            if (isChecked) tableRow.addView(rakamCerceve(activity, i.toString()))
            activity.table_layout.addView(tableRow)
        }
        if (isChecked) activity.table_layout.addView(harfCerceve(activity, duz))
        activity.table_layout.setBackgroundResource(R.drawable.my_button_cerceve)
    }

    fun put_piece(activity: AppCompatActivity, board: Board) {
        val boardStr = board.toString().replace("\n", "")
        var k = 0
        for (i in 8 downTo 1) {
            for (j in listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')) {
                val tag = "$j$i"
                val buton =
                    activity.findViewById<TableLayout>(R.id.table_layout)
                        .findViewWithTag<SquareButton>(tag)
                val isim = boardStr[k].toString()
                val icon = icon(activity, isim)
                buton.setImageDrawable(icon)
                buton.scaleType = ImageView.ScaleType.CENTER_CROP
                k++
            }
        }
        putYenilenler(activity, board)
        paintBoard(activity)
    }

    fun yenilenler(activity: AppCompatActivity, top: Boolean){
        if(top) activity.yenen_top.removeAllViews()
        else activity.yenen_bottom.removeAllViews()
        for (i in 1..15) {
            val buton = SquareButton(activity)
            val Params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            Params.weight = 1F
            buton.layoutParams = Params
            val onEk = if(top) "t" else "b"
            buton.tag = onEk + i.toString()
            buton.setBackgroundResource(R.drawable.my_button_yenen)
            buton.scaleType = ImageView.ScaleType.CENTER_CROP
            if(top) activity.yenen_top.addView(buton)
            else activity.yenen_bottom.addView(buton)
        }
    }

    fun putYenilenler(activity: AppCompatActivity, board: Board){
        val wp = 8 - board.getPieceLocation(Piece.WHITE_PAWN).size
        val wn = 2 - board.getPieceLocation(Piece.WHITE_KNIGHT).size
        val wb = 2 - board.getPieceLocation(Piece.WHITE_BISHOP).size
        val wr = 2 - board.getPieceLocation(Piece.WHITE_ROOK).size
        val wq = 1 - board.getPieceLocation(Piece.WHITE_QUEEN).size
        val bp = 8 - board.getPieceLocation(Piece.BLACK_PAWN).size
        val bn = 2 - board.getPieceLocation(Piece.BLACK_KNIGHT).size
        val bb = 2 - board.getPieceLocation(Piece.BLACK_BISHOP).size
        val br = 2 - board.getPieceLocation(Piece.BLACK_ROOK).size
        val bq = 1 - board.getPieceLocation(Piece.BLACK_QUEEN).size
        for (i in 1..wp){
            val tag = "b" + (i+7).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_bottom)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"P"))
        }
        for (i in 1..wn){
            val tag = "b" + (i+5).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_bottom)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"N"))
        }
        for (i in 1..wb){
            val tag = "b" + (i+3).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_bottom)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"B"))
        }
        for (i in 1..wr){
            val tag = "b" + (i+1).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_bottom)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"R"))
        }
        for (i in 1..wq){
            val tag = "b" + i.toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_bottom)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"Q"))
        }
        for (i in 1..bp){
            val tag = "t" + (i+7).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_top)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"p"))
        }
        for (i in 1..bn){
            val tag = "t" + (i+5).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_top)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"n"))
        }
        for (i in 1..bb){
            val tag = "t" + (i+3).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_top)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"b"))
        }
        for (i in 1..br){
            val tag = "t" + (i+1).toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_top)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"r"))
        }
        for (i in 1..bq){
            val tag = "t" + i.toString()
            val buton = activity.findViewById<LinearLayout>(R.id.yenen_top)
                .findViewWithTag<SquareButton>(tag)
            buton.setImageDrawable(icon(activity,"q"))
        }
    }

    fun paintBoard(activity: AppCompatActivity){
        for (i in 8 downTo 1) {
            for (j in listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')) {
                val tag = "$j$i"
                val buton = activity.findViewById<TableLayout>(R.id.table_layout)
                        .findViewWithTag<SquareButton>(tag)
                if (isDark("$j$i")) buton.setBackgroundResource(R.drawable.my_button_koyu)
                else buton.setBackgroundResource(R.drawable.my_button)
            }
        }
        //Buradan burayayı boyadık
        if (V.moveCount>0){
            activity.findViewById<TableLayout>(R.id.table_layout)
                .findViewWithTag<SquareButton>(lastMove()[0].toString() + lastMove()[1])
                .setBackgroundResource(R.drawable.my_button_buradan)
            activity.findViewById<TableLayout>(R.id.table_layout)
                .findViewWithTag<SquareButton>(lastMove()[2].toString() + lastMove()[3])
                .setBackgroundResource(R.drawable.my_button_buraya)
        }
        //Şah tehlikede ise kırmızı boya
        if (V.board.isKingAttacked) {
            if (V.board.sideToMove.toString() == "WHITE") {
                val konum: String = V.board.getPieceLocation(Piece.WHITE_KING)[0].toString()
                val buton = activity.findViewById<TableLayout>(R.id.table_layout)
                    .findViewWithTag<SquareButton>(konum)
                if (isDark(konum)) buton.setBackgroundResource(R.drawable.my_button_tehlike_koyu)
                else buton.setBackgroundResource(R.drawable.my_button_tehlike)
            } else {
                val konum: String = V.board.getPieceLocation(Piece.BLACK_KING)[0].toString()
                val buton = activity.findViewById<TableLayout>(R.id.table_layout)
                    .findViewWithTag<SquareButton>(konum)
                if (isDark(konum)) buton.setBackgroundResource(R.drawable.my_button_tehlike_koyu)
                else buton.setBackgroundResource(R.drawable.my_button_tehlike)
            }
        }
    }

    fun lastMove(): String {
        val move: Move = V.board.undoMove()
        V.board.doMove(move)
        return move.toString().toUpperCase()
    }

    fun isDark(tag: String): Boolean{
        val k = if (listOf('G', 'E', 'C', 'A').contains(tag[0])) 1 else 0
        return (k + tag[1].toInt()) % 2 == 0
    }

    fun icon(context: Context, isim: String): Drawable? {
        return when (isim) {
            "p" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.bp, null
            )
            "n" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.bn, null
            )
            "b" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.bb, null
            )
            "r" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.br, null
            )
            "q" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.bq, null
            )
            "k" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.bk, null
            )
            "P" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.wp, null
            )
            "N" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.wn, null
            )
            "B" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.wb, null
            )
            "R" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.wr, null
            )
            "Q" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.wq, null
            )
            "K" -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.wk, null
            )
            else -> null
        }
    }

    fun square(tag: String): Square? {
        return when (tag) {
            "A1" -> Square.A1; "A2" -> Square.A2; "A3" -> Square.A3; "A4" -> Square.A4
            "A5" -> Square.A5; "A6" -> Square.A6; "A7" -> Square.A7; "A8" -> Square.A8
            "B1" -> Square.B1; "B2" -> Square.B2; "B3" -> Square.B3; "B4" -> Square.B4
            "B5" -> Square.B5; "B6" -> Square.B6; "B7" -> Square.B7; "B8" -> Square.B8
            "C1" -> Square.C1; "C2" -> Square.C2; "C3" -> Square.C3; "C4" -> Square.C4
            "C5" -> Square.C5; "C6" -> Square.C6; "C7" -> Square.C7; "C8" -> Square.C8
            "D1" -> Square.D1; "D2" -> Square.D2; "D3" -> Square.D3; "D4" -> Square.D4
            "D5" -> Square.D5; "D6" -> Square.D6; "D7" -> Square.D7; "D8" -> Square.D8
            "E1" -> Square.E1; "E2" -> Square.E2; "E3" -> Square.E3; "E4" -> Square.E4
            "E5" -> Square.E5; "E6" -> Square.E6; "E7" -> Square.E7; "E8" -> Square.E8
            "F1" -> Square.F1; "F2" -> Square.F2; "F3" -> Square.F3; "F4" -> Square.F4
            "F5" -> Square.F5; "F6" -> Square.F6; "F7" -> Square.F7; "F8" -> Square.F8
            "G1" -> Square.G1; "G2" -> Square.G2; "G3" -> Square.G3; "G4" -> Square.G4
            "G5" -> Square.G5; "G6" -> Square.G6; "G7" -> Square.G7; "G8" -> Square.G8
            "H1" -> Square.H1; "H2" -> Square.H2; "H3" -> Square.H3; "H4" -> Square.H4
            "H5" -> Square.H5; "H6" -> Square.H6; "H7" -> Square.H7; "H8" -> Square.H8
            else -> null
        }
    }

    fun piece(taraf: String, tas: String): Piece {
        return if (taraf == "WHITE") {
            when (tas) {
                "PIYON" -> Piece.WHITE_PAWN
                "AT" -> Piece.WHITE_KNIGHT
                "FIL" -> Piece.WHITE_BISHOP
                "KALE" -> Piece.WHITE_ROOK
                "VEZIR" -> Piece.WHITE_QUEEN
                "ŞAH" -> Piece.WHITE_KING
                "SAH" -> Piece.WHITE_KING
                else -> Piece.NONE
            }
        } else {
            when (tas) {
                "PIYON" -> Piece.BLACK_PAWN
                "AT" -> Piece.BLACK_KNIGHT
                "FIL" -> Piece.BLACK_BISHOP
                "KALE" -> Piece.BLACK_ROOK
                "VEZIR" -> Piece.BLACK_QUEEN
                "ŞAH" -> Piece.BLACK_KING
                "SAH" -> Piece.BLACK_KING
                else -> Piece.NONE
            }
        }
    }

    fun harfCerceve(context: Context, duz: Boolean): TableRow {
        val tableRow = TableRow(context)
        tableRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )
        val isim = TextView(context)
        isim.setBackgroundResource(R.drawable.my_button_cerceve)
        isim.text = "x"
        isim.setTextColor(context.resources.getColor(R.color.cerceve))
        tableRow.addView(isim)
        val liste = if (duz) listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')
        else listOf('H', 'G', 'F', 'E', 'D', 'C', 'B', 'A')
        for (j in liste) {
            val text = TextView(context)
            val butonParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply { weight = 1F }
            text.layoutParams = butonParams
            text.setBackgroundResource(R.drawable.my_button_cerceve)
            text.text = j.toString()
            text.setTypeface(null, Typeface.BOLD)
            text.textSize = 20F
            text.gravity = Gravity.CENTER
            tableRow.addView(text)
        }
        val isim2 = TextView(context)
        isim2.setBackgroundResource(R.drawable.my_button_cerceve)
        isim2.text = "x"
        isim2.setTextColor(context.resources.getColor(R.color.cerceve))
        tableRow.addView(isim2)
        return tableRow
    }

    fun rakamCerceve(activity: AppCompatActivity, i: String): TextView {
        val isim = TextView(activity)
        val isimParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        isimParams.setMargins(10, 10, 10, 10)
        isim.layoutParams = isimParams
        isim.setBackgroundResource(R.drawable.my_button_cerceve)
        isim.text = i
        isim.setTypeface(null, Typeface.BOLD)
        isim.textSize = 20F
        return isim
    }

}