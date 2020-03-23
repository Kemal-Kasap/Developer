package com.dreamscloud.deneme.ai


import android.util.Log
import com.dreamscloud.deneme.utilities.V
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator
import java.lang.Exception

object E {

    fun perft (board: Board, depth: Int, ply: Int): Long {

        if (depth == 0) {
            return 1
        }
        var nodes: Long = 0
        val moves = MoveGenerator.generateLegalMoves(board)
        for (move in moves) {
            board.doMove(move)
            nodes += perft(board, depth - 1, ply + 1)
            board.undoMove()
        }
        return nodes
    }

    //Bilgisayar için buluyor. Tahta düz ise min çünkü bilgisayar siyah. Düz değilse max çünkü bilgisayar beyaz
    //Sıra siyahta ise min çünkü bilgisayar siyah değilse max çünkü bilgisayar beyaz
    fun bestMove(board: Board): Move? {
        val side = board.sideToMove
        val posMoves = MoveGenerator.generateLegalMoves(board)
        val posMovesValue = ArrayList<Int>()
        //V.TotalMoveCount = 0
        //for döngüsünde her hamle için o hamleden sonraki tahtanın değerini hesaplıyor.
        for (move in posMoves){
            board.doMove(move)
            //posMovesValue.add(eval(board))
            //posMovesValue.add(depth(board,V.depth,0))
            posMovesValue.add(alphaBeta(board,V.zorluk, Int.MIN_VALUE, Int.MAX_VALUE))
            board.undoMove()
        }
        val bestValue = if(side==Side.BLACK) posMovesValue.min() else posMovesValue.max()
        //V.posMovesValuewithDepth = bestValue!!
        val index = posMovesValue.indexOf(bestValue)
        return posMoves[index]
    }

    fun alphaBeta(board: Board, depth: Int, alpha: Int, beta: Int):Int{
        var alfa = alpha

        if(board.isMated) return -30000
        if(board.isDraw) return 0
        if(depth<=0) return evaluate(board)

        var score:Int = Int.MIN_VALUE + 1
        for (move in MoveGenerator.generateLegalMoves(board)){
            board.doMove(move)
            score = -alphaBeta(board, depth-1, -beta, -alfa)
            board.undoMove()
            if ( score >= beta ) return (score)
            if ( score > alfa ) alfa = score
        }
        return alfa
    }

    //Derinliğe göre Hamleden sonra tahtanın değerini döndürür. Derinlik 0 ise o anki tahtanın değeri.
    //Yukardaki best move kodu ile beraber düşünürsek. Derinlik 0 ise ben oynayınca tahtanın puanı ne diyor.
    //Derinlik 1 ise ben oynadıktan sonra o en iyisini oynayınca tahtanın durumu ne
    //Derinlik 2 ise ben hamleyi yaptıktan sonra onun yapabileceği tüm hamlelerde benim vereceğim en iyi yanıta göre
    // onun hamlelerini değerlendirdikten sonra onun en iyi hamlesini oynayınca tahtanın durumu ne
    // Derinlik 3te program takılıyor. Derinlik 0 da 20 hamle, 1de 400 hamle, 2 de 8000 hamle düşünüyor.
    fun depth(board: Board, depth: Int, ply: Int): Int {
        if(depth==0){
            //V.TotalMoveCount++
            return evaluate(board)
        }else {
            var bestValue:Int?
            //DERİNLEŞMEYE ÇALIŞ DERİİNLEŞEMEZSEN DERİNLİK 0 MIŞ GİBİ EVAL AL GEÇ.
            //CATCH DE UNDO MOVE YAPMAYI UNUTMA ÇÜNKÜ DERİNLİK HESAPLAYAMAYINCA UNDO MOVE YAPMADAN ATLAYIP GİDİYOR
            try {
                val Moves = MoveGenerator.generateLegalMoves(board)
                val MovesValue = ArrayList<Int>()
                for (move in Moves){
                    board.doMove(move)
                    MovesValue.add(depth(board,depth-1,ply+1))
                    board.undoMove()
                }
                //Bilgisayar tek hamlede mat edeceğinde MovesValues 0 oluyor.
                bestValue = if(MovesValue.size>0){
                    if(board.sideToMove==Side.BLACK) MovesValue.min() else MovesValue.max()
                }else evaluate(board)
            }catch (e:Exception){
                Log.e("Hesaplanamadı:",e.toString())
                bestValue = evaluate(board)
                board.undoMove()
            }
            return bestValue!!
        }
    }

    /*Döndürdüğü değer pozitif ise Beyaz önde negatif ise Siyah
    fun evaluate(board: Board):Int {
        var boardValue = 0
        var k = 0
        val MATE = 30000
        val DRAW = 0

        for (i in 8 downTo 1) {
            for (j in listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')) {
                val tag = "$j$i"
                when(board.getPiece(F.square(tag))){
                    Piece.WHITE_PAWN -> boardValue += (100 + pawnSquare[k])
                    Piece.WHITE_KNIGHT -> boardValue += (320 + knightSquare[k])
                    Piece.WHITE_BISHOP -> boardValue += (330 + bishopSquare[k])
                    Piece.WHITE_ROOK -> boardValue += (500 + rookSquare[k])
                    Piece.WHITE_QUEEN -> boardValue += (900 + queenSquare[k])
                    Piece.WHITE_KING -> boardValue += (0 + kingSquareOpening[k])
                    Piece.BLACK_PAWN -> boardValue -= (100 + revert(pawnSquare)[k])
                    Piece.BLACK_KNIGHT -> boardValue -= (320 + revert(knightSquare)[k])
                    Piece.BLACK_BISHOP -> boardValue -= (330 + revert(bishopSquare)[k])
                    Piece.BLACK_ROOK -> boardValue -= (500 + revert(rookSquare)[k])
                    Piece.BLACK_QUEEN -> boardValue -= (900 + revert(queenSquare)[k])
                    Piece.BLACK_KING -> boardValue -= (0 + revert(kingSquareOpening)[k])
                    else -> {}
                }
                k++
            }
        }
        //Beyaz oynadı sıra siyaha geçti. Ve beyaz kazandı.
        if(board.isMated){
            boardValue = if(board.sideToMove == Side.BLACK) MATE else -MATE
        }
        if(board.isDraw) boardValue = DRAW

        return boardValue
    }-*/

    //Döndürdüğü değer pozitif ise Beyaz önde negatif ise Siyah
    fun evaluate(board: Board): Int {
        if (board.isMated || board.isDraw) {
            return if (board.isMated) {
                if (board.sideToMove == Side.BLACK) 30000 else -30000
            } else 0
        } else {
            val boardStr = board.toString().replace(
                "\n",
                ""
            )
            var boardValue = 0
            for (k in 0..63) {
                when (boardStr[k]) {
                    'p' -> boardValue -= (100 + revert(pawnSquare)[k])
                    'n' -> boardValue -= (320 + revert(knightSquare)[k])
                    'b' -> boardValue -= (330 + revert(bishopSquare)[k])
                    'r' -> boardValue -= (500 + revert(rookSquare)[k])
                    'q' -> boardValue -= (900 + revert(queenSquare)[k])
                    'k' -> boardValue -= (0 + revert(kingSquareOpening)[k])
                    'P' -> boardValue += (100 + pawnSquare[k])
                    'N' -> boardValue += (320 + knightSquare[k])
                    'B' -> boardValue += (330 + bishopSquare[k])
                    'R' -> boardValue += (500 + rookSquare[k])
                    'Q' -> boardValue += (900 + queenSquare[k])
                    'K' -> boardValue += (0 + kingSquareOpening[k])
                }
            }
            return boardValue
        }
    }

    fun revert(pieceValue: Array<Int>): ArrayList<Int> {
        val black = ArrayList<Int>()
        for (i in 56..63) black.add(pieceValue[i])
        for (i in 48..55) black.add(pieceValue[i])
        for (i in 40..47) black.add(pieceValue[i])
        for (i in 32..39) black.add(pieceValue[i])
        for (i in 24..31) black.add(pieceValue[i])
        for (i in 16..23) black.add(pieceValue[i])
        for (i in 8..15) black.add(pieceValue[i])
        for (i in 0..7) black.add(pieceValue[i])
        return black
    }

    val pawnSquare = arrayOf(
        0, 0, 0, 0, 0, 0, 0, 0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5, 5, 10, 25, 25, 10, 5, 5,
        0, 0, 0, 20, 20, 0, 0, 0,
        5, -5, -10, 0, 0, -10, -5, 5,
        5, 10, 10, -20, -20, 10, 10, 5,
        0, 0, 0, 0, 0, 0, 0, 0
    )

    val knightSquare = arrayOf(
        -50, -40, -30, -30, -30, -30, -40, -50,
        -40, -20, 0, 0, 0, 0, -20, -40,
        -30, 0, 10, 15, 15, 10, 0, -30,
        -30, 5, 15, 20, 20, 15, 5, -30,
        -30, 0, 15, 20, 20, 15, 0, -30,
        -30, 5, 10, 15, 15, 10, 5, -30,
        -40, -20, 0, 5, 5, 0, -20, -40,
        -50, -40, -30, -30, -30, -30, -40, -50
    )

    val bishopSquare = arrayOf(
        -20, -10, -10, -10, -10, -10, -10, -20,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 5, 5, 10, 10, 5, 5, -10,
        -10, 0, 10, 10, 10, 10, 0, -10,
        -10, 10, 10, 10, 10, 10, 10, -10,
        -10, 5, 0, 0, 0, 0, 5, -10,
        -20, -10, -10, -10, -10, -10, -10, -20
    )

    val rookSquare = arrayOf(
        0, 0, 0, 0, 0, 0, 0, 0,
        5, 10, 10, 10, 10, 10, 10, 5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        0, 0, 0, 5, 5, 0, 0, 0
    )

    val queenSquare = arrayOf(
        -20, -10, -10, -5, -5, -10, -10, -20,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -5, 0, 5, 5, 5, 5, 0, -5,
        0, 0, 5, 5, 5, 5, 0, -5,
        -10, 5, 5, 5, 5, 5, 0, -10,
        -10, 0, 5, 0, 0, 0, 0, -10,
        -20, -10, -10, -5, -5, -10, -10, -20
    )

    val kingSquareOpening = arrayOf(
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -20, -30, -30, -40, -40, -30, -30, -20,
        -10, -20, -20, -20, -20, -20, -20, -10,
        20, 20, 0, 0, 0, 0, 20, 20,
        20, 30, 10, 0, 0, 10, 30, 20
    )

    val kingSquareEndGame = arrayOf(
        -50, -40, -30, -20, -20, -30, -40, -50,
        -30, -20, -10, 0, 0, -10, -20, -30,
        -30, -10, 20, 30, 30, 20, -10, -30,
        -30, -10, 30, 40, 40, 30, -10, -30,
        -30, -10, 30, 40, 40, 30, -10, -30,
        -30, -10, 20, 30, 30, 20, -10, -30,
        -30, -30, 0, 0, 0, 0, -30, -30,
        -50, -30, -30, -30, -30, -30, -30, -50
    )
}