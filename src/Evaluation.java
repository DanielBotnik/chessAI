package botnik.chess.server.chessai;

public class Evaluation {

    public static final int PAWN      = 80;
    public static final int KNIGHT    = 325;
    public static final int BISHOP    = 325;
    public static final int ROOK      = 500;
    public static final int QUEEN     = 975;

    public static final int PAWN_EG   = 100;
    public static final int KNIGHT_EG = 375;
    public static final int BISHOP_EG = 400;
    public static final int ROOK_EG   = 620;
    public static final int QUEEN_EG  = 1100;

    public static final int[] WEIGHTS = {
            me(PAWN, PAWN_EG), me(KNIGHT, KNIGHT_EG), me(BISHOP, BISHOP_EG), me(ROOK, ROOK_EG), me(QUEEN, QUEEN_EG), me(0, 0),
            me(-PAWN, -PAWN_EG), me(-KNIGHT, -KNIGHT_EG), me(-BISHOP, -BISHOP_EG), me(-ROOK, -ROOK_EG), me(-QUEEN, -QUEEN_EG), me(0, 0)
    };
    public static final int[][] MOBILITY_BONUS = {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {
                me(-24, -30), me(-21, -26), me(-9, -13), me(-1, -6), me(1, 2),
                me(3, 4), me(7, 9), me(12, 12), me(18, 16)
            },
            {
                me(-20,-25),me(-10,-13),me(6,-3),me(10,3),me(16,7),
                me(21,14), me(22,22), me(24,24),me(25,25),me(27,28),
                me(29,29),me(31,32),me(34,33),me(36,34)
            },
            {
                me(-20,-31 ), me(-11,-10 ), me(0, 4), me(2, 11), me(3,29 ),
                me(5,38 ), me(7,39 ), me(10, 42), me(13,48 ), me(14,49 ),
                me(15,51 ), me(16,53 ), me(19,57 ), me(19,60 ), me(23,65)
            },
            {
                    me(-32,-40),me(-24,-30),me(-13,-14),me(-8,-4),me(-2,5),
                    me(6,14),me(8,18),me(13,24),me(14,25),me(18,33),
                    me(20,34),me(21,36),me(22,40),me(23,42),me(24,43),
                    me(25,43),me(25,44),me(26,45),me(26,46),me(27,46),
                    me(30,49),me(32,55),me(33,57),me(33,58),me(34,59),
                    me(34,60),me(35,62),me(35,71)
            },
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };

    public static int evaluate(Board board) {

        AttacksInfo attacksInfo = new AttacksInfo(board);

        long[] bitBoards = board.getBitBoards();
        int score = 0;
        int totalMovesWhite = 0, totalMovesBlack = 0;
        for(int i = 0; i < 12 ; i++) {
            long bitBoard = bitBoards[i];
            score += Long.bitCount(bitBoard) * WEIGHTS[i];
            while(bitBoard != 0) {
                int square = BitBoard.getLSBIndex(bitBoard);
                bitBoard ^= (1L << square);
                if(i < 6) {
                    score += MOBILITY_BONUS[i][attacksInfo.numberOfPossibleMoves[square]];
                    score += PSQT.PSQT[i][square];
                }
                else {
                    score -= MOBILITY_BONUS[i - 6][attacksInfo.numberOfPossibleMoves[mirrorSquare(square)]];
                    score -= PSQT.PSQT[i - 6][mirrorSquare(square)];
                }
            }
        }
        return board.getSideToMove() == 0 ? m(score) : -m(score);
    }


    public static int me(int middleGame,int endGame) {
        return (middleGame << 16) + endGame;
    }

    public static int e(int me) {
        return me & 0xFFFF;
    }

    public static short m(int me){
        return (short) ((me + 0x8000) >>> 16);
    }

    public static int mirrorSquare(int square) {
        return square ^ 56;
    }
}
