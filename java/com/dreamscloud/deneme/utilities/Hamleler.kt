package com.dreamscloud.deneme

import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.dreamscloud.deneme.ai.M
import com.dreamscloud.deneme.utilities.F
import com.dreamscloud.deneme.utilities.T
import com.dreamscloud.deneme.utilities.V
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator
import com.github.bhlangonijr.chesslib.move.MoveList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.system.measureTimeMillis


object GameModum {
    val Normal = "Normal"
    val Online = "Online"
}

object H {

    fun promotionDialog(activity: AppCompatActivity, board: Board, ilk: String, son: String) {
        if (V.moveCount < V.onlineHamlesayisi) {
            promotionHamle(activity, board, ilk, son, V.promotion)
        } else {
            val pieces = arrayOf("Vezir", "Kale", "Fil", "At")
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Pick a piece")
            builder.setItems(pieces) { _, which ->
                when (pieces[which]) {
                    "Vezir" -> promotionHamle(activity, board, ilk, son, 1)
                    "Kale" -> promotionHamle(activity, board, ilk, son, 2)
                    "Fil" -> promotionHamle(activity, board, ilk, son, 3)
                    "At" -> promotionHamle(activity, board, ilk, son, 4)
                    else -> promotionHamle(activity, board, ilk, son, 1)
                }
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    fun promotionHamle(
        activity: AppCompatActivity, board: Board, ilk: String, son: String, int: Int
    ) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val isChecked = sharedPreferences.getBoolean("ses", false)
        if (isChecked) S.seslendir(activity, board, ilk, son, int)

        if (board.sideToMove == Side.WHITE) {
            when (int) {
                1 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.WHITE_QUEEN))
                }
                2 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.WHITE_ROOK))
                }
                3 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.WHITE_BISHOP))
                }
                4 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.WHITE_KNIGHT))
                }
                else -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.WHITE_QUEEN))
                }
            }
        } else {
            when (int) {
                1 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.BLACK_QUEEN))
                }
                2 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.BLACK_ROOK))
                }
                3 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.BLACK_BISHOP))
                }
                4 -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.BLACK_KNIGHT))
                }
                else -> {
                    board.doMove(Move(F.square(ilk), F.square(son), Piece.BLACK_QUEEN))
                }
            }
        }
        //HAMLEYİ TAMAMLAMADAN YAZDIĞIN İÇİN MOVECOUNTU 1 ARTIR DA YAZ.
        if (V.kurucuMail != "" && V.davetliMail != "") {
            val gameName = V.kurucuMail + V.davetliMail
            val hamle = ilk + "-" + son + "-" + int.toString()
            val moveCountArtirilacak = (V.moveCount + 1).toString()
            V.ref.child("Games").child(gameName).child("Hamleler").child(
                moveCountArtirilacak
            )
                .setValue(hamle)
        }
        hamleyiTamamla(activity, board)
    }

    //BÜTÜN HAMLELERİ DOKUNMATİK HAMLE OLARAK YAP. DOKUNMATİK HAMLE YAPMADAN ÖNCE V.SECİLİ_İLKİ AYARLA
    // V.secili_ilk = ilk
    // dokunmatikHamle(activity, son, GameModum.Online)
    fun hamleYap(activity: AppCompatActivity, tag: String, gameModum: String) {
        when (gameModum) {
            GameModum.Normal -> hamle(activity, V.board, V.secili_ilk, tag)
            GameModum.Online -> onlineHamle(activity, V.board, V.secili_ilk, tag)
            //else -> hamle(activity, V.board, V.secili_ilk, tag)
        }

    }

    fun onlineHamle(activity: AppCompatActivity, board: Board, ilk: String, son: String) {
        if (V.moveCount < V.onlineHamlesayisi) {
            oHamle(activity, board, V.onlineHamle.split("-")[0], V.onlineHamle.split("-")[1])
        } else {
            if (V.kurucuMu) {
                if (board.sideToMove.toString() == V.kurucuTarafRenk) {
                    oHamle(activity, board, ilk, son)
                } else T.toast(activity, 3)
            } else {
                if (board.sideToMove.toString() != V.kurucuTarafRenk) {
                    oHamle(activity, board, ilk, son)
                } else T.toast(activity, 3)
            }
        }
    }

    fun oHamle(activity: AppCompatActivity, board: Board, ilk: String, son: String) {
        val gameName = V.kurucuMail + V.davetliMail
        val hamle = ilk + "-" + son
        val moves: MoveList = MoveGenerator.generateLegalMoves(board)
        //Eğer promotion değilse hamleyi internete yaz.Eğer promotion ise yukarda yazıyoz.
        //HAMLEYİ TAMAMLAMADAN YAZDIĞIN İÇİN MOVECOUNTU 1 ARTIR DA YAZ.
        val moveCountArtirilacak = (V.moveCount + 1).toString()
        if (moves.contains(Move(F.square(ilk), F.square(son))))
            V.ref.child("Games").child(gameName).child("Hamleler").child(moveCountArtirilacak).setValue(
                hamle
            )
        hamle(activity, board, ilk, son)
    }

    fun rakipHamle(activity: AppCompatActivity) {
        val gameName = V.kurucuMail + V.davetliMail
        V.ref.child("Games").child(gameName).child("Hamleler").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    V.onlineHamle = ds.value.toString()
                }
                V.onlineHamlesayisi = dataSnapshot.childrenCount
                if (V.moveCount < V.onlineHamlesayisi) {
                    val ilk = V.onlineHamle.split("-")[0]
                    val son = V.onlineHamle.split("-")[1]
                    try {
                        V.promotion = V.onlineHamle.split("-")[2].toInt()
                    } catch (e: Exception) {
                        //promotion yok
                    }
                    V.secili_ilk = ilk
                    hamleYap(activity, son, GameModum.Online)
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {
            }
        })
    }

    fun hamle(activity: AppCompatActivity, board: Board, ilk: String, son: String) {
        val moves: MoveList = MoveGenerator.generateLegalMoves(board)
        if (moves.contains(Move(F.square(ilk), F.square(son)))) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isChecked = sharedPreferences.getBoolean("ses", false)
            if (isChecked) S.seslendir(activity, board, ilk, son)
            board.doMove(Move(F.square(ilk), F.square(son)))
            hamleyiTamamla(activity, board)
        } else {
            //ONCLİCKTE BAKILDI PROMOTİON OLDUĞU İLLEGAL OLMADIĞI BİLİNİYOR.
            H.promotionDialog(activity, board, ilk, son)
        }
    }

    fun hamleyiTamamla(activity: AppCompatActivity, board: Board) {
        V.moveCount++
        //V.computerPlayed = !V.computerPlayed
        F.put_piece(activity, board)
        if (board.isMated) {
            val winner = if (board.sideToMove == Side.WHITE) Side.BLACK.toString()
            else Side.WHITE.toString()
            F.oyunBitti(activity, winner).show()
        }
        if (board.isDraw) {
            F.oyunBitti(activity, "").show()
        }
        V.secili_ilk = ""

        if (V.vsComputer && !board.isDraw && !board.isMated) {
            if (!V.computerPlayed) {
                bilgisayar(activity, board)
            }
        }
    }

    fun bilgisayar(activity: AppCompatActivity, board: Board) {
        if (V.vsComputer) {
            //Eğer derinlik 2 ise 2 sn beklemesine gerek yok zaten düşünmesü 2 snyi geçiyor.
            val delay = if (V.zorluk == 2) 300L else 2000L
            object : CountDownTimer(delay, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    //2 SN SONRA HALA BİLGİSAYAR OYNAMADIYSA OYNA
                    //BU IF KOMUTU HAMLEYİ GERİ ALMALARDA İŞE YARIYOR.
                    if (!V.computerPlayed) {
                        //val moves: MoveList = MoveGenerator.generateLegalMoves(board)
                        //val random = (0 until moves.size).random()
                        //val move = moves[random]
                        var move: Move? = null
                        val passedTime = measureTimeMillis {
                            move = M.bestMove(board)
                        }.toString()
                        Log.e("Geçen Süre",passedTime)

                        //val move = E.bestMove(board)
                        val moveString = move.toString().toUpperCase()
                        if (moveString.length == 4) {
                            val ilk = moveString[0].toString() + moveString[1].toString()
                            val son = moveString[2].toString() + moveString[3].toString()
                            V.secili_ilk = ilk
                            V.computerPlayed = true
                            hamleYap(activity, son, GameModum.Normal)
                            //V.computerPlayed = true

                        } else {
                            val ilk = moveString[0].toString() + moveString[1].toString()
                            val son = moveString[2].toString() + moveString[3].toString()
                            V.secili_ilk = ilk
                            V.onlineHamlesayisi = (V.moveCount + 1).toLong()
                            V.promotion = promotionSayi(moveString[4].toString())
                            V.computerPlayed = true
                            hamleYap(activity, son, GameModum.Normal)
                            //V.computerPlayed = true
                        }
                    }
                }
            }.start()
        }
    }

    fun promotionSayi(harf: String): Int {
        return when (harf) {
            "Q" -> 1
            "R" -> 2
            "B" -> 3
            "N" -> 4
            else -> 1
        }
    }
}