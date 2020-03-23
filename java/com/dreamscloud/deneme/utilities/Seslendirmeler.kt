package com.dreamscloud.deneme

import android.content.Intent
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.dreamscloud.deneme.utilities.F
import com.dreamscloud.deneme.utilities.V
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator
import com.github.bhlangonijr.chesslib.move.MoveList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


object S {

    fun seslendir(
        activity: AppCompatActivity,
        board: Board,
        ilk: String,
        son: String,
        promotion: Int = 0
    ) {
        val piece = board.getPiece(F.square(ilk))
        val bosMu = board.getPiece(F.square(son)) == Piece.NONE
        if ((piece != Piece.WHITE_PAWN && piece != Piece.BLACK_PAWN) || promotion != 0 || !bosMu)
            tasSeslendir(activity, piece)
        Timer().schedule(300){
            if (!bosMu) MediaPlayer.create(activity, R.raw.alir).start()
            Timer().schedule(300){
                hamleSeslendir(activity, son)
                Timer().schedule(600){
                    if (promotion != 0) promotionSeslendir(activity, promotion)
                }
            }
        }
    }

    fun tasSeslendir(activity: AppCompatActivity, piece: Piece) {
        when (piece) {
            Piece.WHITE_PAWN -> MediaPlayer.create(activity, R.raw.piyon).start()
            Piece.WHITE_KNIGHT -> MediaPlayer.create(activity, R.raw.at).start()
            Piece.WHITE_BISHOP -> MediaPlayer.create(activity, R.raw.fil).start()
            Piece.WHITE_ROOK -> MediaPlayer.create(activity, R.raw.kale).start()
            Piece.WHITE_QUEEN -> MediaPlayer.create(activity, R.raw.vezir).start()
            Piece.WHITE_KING -> MediaPlayer.create(activity, R.raw.sah).start()
            Piece.BLACK_PAWN -> MediaPlayer.create(activity, R.raw.piyon).start()
            Piece.BLACK_KNIGHT -> MediaPlayer.create(activity, R.raw.at).start()
            Piece.BLACK_BISHOP -> MediaPlayer.create(activity, R.raw.fil).start()
            Piece.BLACK_ROOK -> MediaPlayer.create(activity, R.raw.kale).start()
            Piece.BLACK_QUEEN -> MediaPlayer.create(activity, R.raw.vezir).start()
            Piece.BLACK_KING -> MediaPlayer.create(activity, R.raw.sah).start()
            else -> {}
        }
    }

    fun hamleSeslendir(activity: AppCompatActivity, hamleTo: String) {
        when (hamleTo[0].toString()) {
            "A" -> MediaPlayer.create(activity, R.raw.a).start()
            "B" -> MediaPlayer.create(activity, R.raw.b).start()
            "C" -> MediaPlayer.create(activity, R.raw.c).start()
            "D" -> MediaPlayer.create(activity, R.raw.d).start()
            "E" -> MediaPlayer.create(activity, R.raw.e).start()
            "F" -> MediaPlayer.create(activity, R.raw.f).start()
            "G" -> MediaPlayer.create(activity, R.raw.g).start()
            "H" -> MediaPlayer.create(activity, R.raw.h).start()
        }
        Timer().schedule(300){
            sayiOku(activity, hamleTo[1].toString().toInt())
        }
    }

    fun sayiOku(activity: AppCompatActivity, sayi: Int) {
        when (sayi) {
            1 -> MediaPlayer.create(activity, R.raw.bir).start()
            2 -> MediaPlayer.create(activity, R.raw.iki).start()
            3 -> MediaPlayer.create(activity, R.raw.uc).start()
            4 -> MediaPlayer.create(activity, R.raw.dort).start()
            5 -> MediaPlayer.create(activity, R.raw.bes).start()
            6 -> MediaPlayer.create(activity, R.raw.alti).start()
            7 -> MediaPlayer.create(activity, R.raw.yedi).start()
            8 -> MediaPlayer.create(activity, R.raw.sekiz).start()
        }
    }

    fun sayiOkuArtir(activity: AppCompatActivity) {
        sayiOku(activity, V.sayi)
        V.sayi++
    }

    fun promotionSeslendir(activity: AppCompatActivity, int: Int) {
        when (int) {
            1 -> MediaPlayer.create(activity, R.raw.vezir).start()
            2 -> MediaPlayer.create(activity, R.raw.kale).start()
            3 -> MediaPlayer.create(activity, R.raw.fil).start()
            4 -> MediaPlayer.create(activity, R.raw.at).start()
        }
    }

    fun dinleme(activity: AppCompatActivity, checkNo: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        when (checkNo) {
            1 -> intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                activity.getString(R.string.say_bir)
            )
            2 -> intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                activity.getString(R.string.say_iki)
            )
            3 -> intent.putExtra(RecognizerIntent.EXTRA_PROMPT, activity.getString(R.string.say_uc))
        }
        activity.startActivityForResult(intent, checkNo)
    }

    fun myOnActivityResult(activity: AppCompatActivity, data: Intent?, gameModum: String) {
        val results: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val harfler = listOf('H', 'G', 'F', 'E', 'D', 'C', 'B', 'A')
        val numaralar = listOf('8', '7', '6', '5', '4', '3', '2', '1')
        val taslar = listOf("PIYON", "AT", "FIL", "KALE", "VEZIR", "ŞAH", "SAH")
        var pieces = ArrayList<String>()
        var kareSons = ArrayList<String>()
        for (result in results!!) {
            try {
                val result_array: List<String> = result.toUpperCase(Locale.US).split(" ")
                for (kelime in result_array) {
                    if (taslar.contains(kelime)) {
                        pieces.add(kelime)
                    }
                    if (harfler.contains(kelime[0]) && numaralar.contains(kelime[1])) {
                        val harf_sayi = kelime[0].toString() + kelime[1]
                        kareSons.add(harf_sayi)
                    }
                }
            } catch (e: Exception) {
            }
        }

        V.ihtimallerLegal = ArrayList()
        V.ihtimallerKalan = ArrayList()

        //pieces.add("PIYON")
        pieces = ArrayList(pieces.distinct())
        kareSons = ArrayList(kareSons.distinct())
        val taraf = V.board.sideToMove.toString()
        for (tas in pieces) {
            val kareIlks: List<Square> = V.board.getPieceLocation((F.piece(taraf, tas)))

            val movesLegal: MoveList = MoveGenerator.generateLegalMoves(V.board)
            val movesHepsi: MoveList = MoveGenerator.generatePseudoLegalMoves(V.board)
            for (ilk in kareIlks) {
                for (son in kareSons) {
                    val move = Move(ilk, F.square(son))
                    if (movesHepsi.contains(move)) {
                        val hamle = ilk.toString() + "-" + son
                        if (movesLegal.contains(move)) {
                            V.ihtimallerLegal.add(hamle)
                        } else {
                            V.ihtimallerKalan.add(hamle)
                        }
                    }
                    for (promotion in pieces) {
                        val promotionMove = Move(ilk, F.square(son), F.piece(taraf, promotion))
                        if (movesHepsi.contains(promotionMove)) {
                            val promotionSayi = when (promotion) {
                                "VEZIR" -> 1
                                "KALE" -> 2
                                "FIL" -> 3
                                "AT" -> 4
                                else -> 1
                            }
                            val hamlePromotion = ilk.toString() + "-" + son + "-" + promotionSayi
                            val hamleSilinecek = ilk.toString() + "-" + son
                            if (movesLegal.contains(promotionMove)) {
                                V.ihtimallerLegal.add(hamlePromotion)
                                V.ihtimallerKalan.remove(hamleSilinecek)
                            } else {
                                V.ihtimallerKalan.add(hamlePromotion)
                            }
                        }
                    }
                }
            }

            //hamle promotion ise H7-H8 illegalden silinmeli. Legalse H7-H8-vezir legale yazılmalı. İllegalse illegale yazılmalı
        }
        //Eğer taşlı hamle yoksa yap. Yani at a3 dediğinde at a3 oyna. a3ü arama ama a3 derse aşağıdaki ifi yap
        if (V.ihtimallerLegal.size == 0) {
            pieces.add("PIYON")
            pieces = ArrayList(pieces.distinct())
            kareSons = ArrayList(kareSons.distinct())
            for (tas in pieces) {
                val kareIlks: List<Square> = V.board.getPieceLocation((F.piece(taraf, tas)))

                val movesLegal: MoveList = MoveGenerator.generateLegalMoves(V.board)
                val movesHepsi: MoveList = MoveGenerator.generatePseudoLegalMoves(V.board)
                for (ilk in kareIlks) {
                    for (son in kareSons) {
                        val move = Move(ilk, F.square(son))
                        if (movesHepsi.contains(move)) {
                            val hamle = "$ilk-$son"
                            if (movesLegal.contains(move)) {
                                V.ihtimallerLegal.add(hamle)
                            } else {
                                V.ihtimallerKalan.add(hamle)
                            }
                        }
                        for (promotion in pieces) {
                            val promotionMove = Move(ilk, F.square(son), F.piece(taraf, promotion))
                            if (movesHepsi.contains(promotionMove)) {
                                val promotionSayi = when (promotion) {
                                    "VEZIR" -> 1
                                    "KALE" -> 2
                                    "FIL" -> 3
                                    "AT" -> 4
                                    else -> 1
                                }
                                val hamlePromotion =
                                    ilk.toString() + "-" + son + "-" + promotionSayi
                                val hamleSilinecek = ilk.toString() + "-" + son
                                if (movesLegal.contains(promotionMove)) {
                                    V.ihtimallerLegal.add(hamlePromotion)
                                    V.ihtimallerKalan.remove(hamleSilinecek)
                                } else {
                                    V.ihtimallerKalan.add(hamlePromotion)
                                }
                            }
                        }
                    }
                }

                //hamle promotion ise H7-H8 illegalden silinmeli. Legalse H7-H8-vezir legale yazılmalı. İllegalse illegale yazılmalı
            }
        }

        sesliHamle(activity, V.ihtimallerLegal, gameModum)
        var legalMoves = "Legal Moves: "
        var illegalMoves = "Illegal Moves"
        for (ihtimal in V.ihtimallerLegal) {
            legalMoves = legalMoves + ihtimal + ", "
        }
        for (ihtimal in V.ihtimallerKalan) {
            illegalMoves = illegalMoves + ihtimal + ", "
        }
    }

    fun myOnActivityResult2(activity: AppCompatActivity, data: Intent?, gameModum: String) {
        val results: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        var hamleyiBirKereYap = 1
        val sayilar = listOf("BIR", "IKI", "ÜÇ", "DÖRT", "BEŞ", "ALTI", "YEDI", "SEKIZ")
        for (result in results!!) {
            try {
                val result_array: List<String> = result.toUpperCase(Locale.US).split(" ")
                for (kelime in result_array) {
                    if (sayilar.contains(kelime)) {
                        try {
                            val ihtimal = when (kelime) {
                                "BIR" -> V.ihtimallerLegal[0]
                                "IKI" -> V.ihtimallerLegal[1]
                                "ÜÇ" -> V.ihtimallerLegal[2]
                                "DÖRT" -> V.ihtimallerLegal[3]
                                "BEŞ" -> V.ihtimallerLegal[4]
                                "ALTI" -> V.ihtimallerLegal[5]
                                "YEDI" -> V.ihtimallerLegal[6]
                                "SEKIZ" -> V.ihtimallerLegal[7]
                                else -> V.ihtimallerLegal[0]
                            }
                            if (hamleyiBirKereYap == 1) {
                                sesliHamleyiUygula(activity, ihtimal, gameModum)
                                hamleyiBirKereYap++
                            }
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
                        }

                    }
                }
            } catch (e: Exception) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun myOnActivityResult3(activity: AppCompatActivity, data: Intent?, gameModum: String) {
        val results: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        var hamleyiBirKereYap = 1
        val cevaplar = listOf("EVET", "HAYIR")
        for (result in results!!) {
            try {
                val result_array: List<String> = result.toUpperCase(Locale.US).split(" ")
                for (kelime in result_array) {
                    if (cevaplar.contains(kelime)) {
                        if (kelime == "EVET") {
                            //SESLİ HAMLEDE PROMOTİON OLURSA ONLİNE HAMLE GİBİ MUAMELE EDİP DİALOG ÇIKMASINI ÖNLE
                            if (hamleyiBirKereYap == 1) {
                                val ihtimal = V.ihtimallerLegal[0]
                                sesliHamleyiUygula(activity, ihtimal, gameModum)
                                hamleyiBirKereYap++
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun sesliHamleyiUygula(activity: AppCompatActivity, ihtimal: String, gameModum: String) {
        val ilk = ihtimal.split("-")[0]
        val son = ihtimal.split("-")[1]
        //SESLİ HAMLEDE PROMOTİON OLURSA ONLİNE HAMLE GİBİ MUAMELE EDİP DİALOG ÇIKMASINI ÖNLE
        if (ihtimal.split("-").size > 2) {
            V.onlineHamlesayisi = (V.moveCount + 1).toLong()
            V.promotion = ihtimal.split("-")[2].toString().toInt()
        }
        V.secili_ilk = ilk
        V.computerPlayed = false
        H.hamleYap(activity, son, gameModum)
    }

    fun sesliHamle(activity: AppCompatActivity, ihtimaller: ArrayList<String>, gameModum: String) {

        if (ihtimaller.size == 0) {
            //legal olmayan ihtimalleri söle ve bu hamleyi yapamazsınız de
            MediaPlayer.create(activity, R.raw.sizi_anlayamadim).start()
            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    S.dinleme(activity, 1)
                }
            }.start()
        } else {
            if (ihtimaller.size == 1) {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
                val isChecked = sharedPreferences.getBoolean("onay", false)
                if (isChecked) {
                    MediaPlayer.create(activity, R.raw.soyleyecegim_hamleyi_onayliyor_musunuz)
                        .start()
                    ihtimalAnalizi(activity, ihtimaller)
                } else {
                    val ihtimal = ihtimaller[0]
                    sesliHamleyiUygula(activity, ihtimal, gameModum)
                }
            } else {
                MediaPlayer.create(activity, R.raw.kacinci_hamleyi_yapmak_istiyorsunuz).start()
                ihtimalAnalizi(activity, ihtimaller)
            }
        }

    }

    fun ihtimalAnalizi(activity: AppCompatActivity, ihtimaller: ArrayList<String>) {
        var geriSayim = 3000L
        V.sayi = 1
        for (ihtimal in ihtimaller) {
            object : CountDownTimer(geriSayim, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    val ilk: String = ihtimal.split("-")[0]
                    val son: String = ihtimal.split("-")[1]
                    if (ihtimal.split("-").size == 2) {
                        if (ihtimaller.size > 1) sayiOkuArtir(activity)
                        object : CountDownTimer(500, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }

                            override fun onFinish() {
                                seslendir(activity, V.board, ilk, son)
                            }
                        }.start()
                    } else {
                        if (ihtimaller.size > 1) sayiOkuArtir(activity)
                        object : CountDownTimer(500, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }

                            override fun onFinish() {
                                seslendir(
                                    activity,
                                    V.board,
                                    ilk,
                                    son,
                                    ihtimal.split("-")[2].toInt()
                                )
                            }
                        }.start()
                    }
                }
            }.start()
            geriSayim += 2000L
        }
        object : CountDownTimer(geriSayim, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (ihtimaller.size > 1) {
                    S.dinleme(activity, 2)
                } else {
                    S.dinleme(activity, 3)
                }
            }
        }.start()
    }
}