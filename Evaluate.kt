
    //Find the best move. If move turn on black min value. Else max value.
    fun bestMove(board: Board): Move? {
        val side = board.sideToMove
        val posMoves = MoveGenerator.generateLegalMoves(board)
        val posMovesValue = ArrayList<Int>()
        //These for cycle find move's value. Move's value means value of board after move was played.
        for (move in posMoves){
            board.doMove(move)
            //This code add value accordig to thinking of depth
            posMovesValue.add(depth(board,V.depth,0))
            board.undoMove()
        }
        val bestValue = if(side==Side.BLACK) posMovesValue.min() else posMovesValue.max()
        val index = posMovesValue.indexOf(bestValue)
        return posMoves[index]
    }

    //Board value with depht thinking.
    //Depth 1 (400 move thinking) computer play and then player do his best move.
    //Depth 2 (8000 move thinking) computer play and then player do his best move. And then computer do best move
    //Depth 3 take long time. Depth 2 takes 5 second. Approximately Dept 3 takes 2 minutes. Alpha Beta must write.
    fun depth(board: Board, depth: Int, ply: Int): Int {
        if(depth==0){
            return eval(board)
        }else {
            var bestValue:Int? = eval(board)
            //TRY depth thinking. If not possible take board value.
            //on Catch dont forget undoMove. Because if try not possible undoMove in Try section will passed.
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
                }else eval(board)
            }catch (e:Exception){
                Log.e("Error:",e.toString())
                bestValue = eval(board)
                board.undoMove()
            }
            return bestValue!!
        }
    }

    //If return value is pozitif White is good. If negatif Black is good
    fun eval(board: Board):Int {
        var boardValue = 0
        var k = 0
        val MATE = 30000
        val DRAW = 0
        //Look all square on the board
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
        //If white win the game turn is on black. For this reason if side Black this means white win
        if(board.isMated){
            boardValue = if(board.sideToMove == Side.BLACK) MATE else -MATE
        }
        if(board.isDraw) boardValue = DRAW

        return boardValue
    }

    //Calculate value of black pieces position 
    fun revert(pieceValue: Array<Int>): ArrayList<Int> {
        val black = ArrayList<Int>()
        for(i in 56..63) black.add(pieceValue[i])
        for(i in 48..55) black.add(pieceValue[i])
        for(i in 40..47) black.add(pieceValue[i])
        for(i in 32..39) black.add(pieceValue[i])
        for(i in 24..31) black.add(pieceValue[i])
        for(i in 16..23) black.add(pieceValue[i])
        for(i in 8..15) black.add(pieceValue[i])
        for(i in 0..7) black.add(pieceValue[i])
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
        -50, -40, -30, -30, -30, -30, -40, -50)

    val bishopSquare = arrayOf(
        -20, -10, -10, -10, -10, -10, -10, -20,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 5, 5, 10, 10, 5, 5, -10,
        -10, 0, 10, 10, 10, 10, 0, -10,
        -10, 10, 10, 10, 10, 10, 10, -10,
        -10, 5, 0, 0, 0, 0, 5, -10,
        -20, -10, -10, -10, -10, -10, -10, -20)

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
