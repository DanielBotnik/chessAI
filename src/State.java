package botnik.chess.server.chessai;

public class State {

    public static final int sideToMoveMask            = 0b000000000001;
    public static final long isEnPassantMask          = 0b000000000010;
    public static final long enPassantSquareMask      = 0b000011111100;
    public static final long whiteCastleKingSideMask  = 0b000100000000;
    public static final long whiteCastleQueenSideMask = 0b001000000000;
    public static final long blackCastleKingSideMask  = 0b010000000000;
    public static final long blackCastleQueenSideMask = 0b100000000000;

    public static final long[] CASTLING_RIGHTS = {
            3583, 4095, 4095, 4095, 3327, 4095, 4095, 3839,
            4095, 4095, 4095, 4095, 4095, 4095, 4095, 4095,
            4095, 4095, 4095, 4095, 4095, 4095, 4095, 4095,
            4095, 4095, 4095, 4095, 4095, 4095, 4095, 4095,
            4095, 4095, 4095, 4095, 4095, 4095, 4095, 4095,
            4095, 4095, 4095, 4095, 4095, 4095, 4095, 4095,
            4095, 4095, 4095, 4095, 4095, 4095, 4095, 4095,
            2047, 4095, 4095, 4095, 1023, 4095, 4095, 3071
    };

    public static int getSideToMove(int state){
        return state & sideToMoveMask;
    }

    public static boolean isEnPassant(int state){
        return (state & isEnPassantMask) != 0;
    }

    public static int getEnPassantSquare(int state){
      return (int) ((state & enPassantSquareMask) >>> 2);
    }

    public static boolean canWhiteCastleKingSide(int state){
        return (state & whiteCastleKingSideMask) != 0;
    }

    public static boolean canWhiteCastleQueenSide(int state){
        return (state & whiteCastleQueenSideMask) != 0;
    }

    public static boolean canBlackCastleKingSide(int state){
        return (state & blackCastleKingSideMask) != 0;
    }

    public static boolean canBlackCastleQueenSide(int state){
        return (state & blackCastleQueenSideMask) != 0;
    }

    public static int getCastlingRights(int state) {
        return (state >>> 8);
    }

}
