package botnik.chess.server.chessai;

import java.util.ArrayList;

public class Move {

    private static final int SOURCE_SQUARE_MASK    = 0b000000000000000000111111;
    private static final int TARGET_SQUARE_MASK    = 0b000000000000111111000000;
    private static final int PIECE_TYPE_MASK       = 0b000000001111000000000000;
    private static final int PROMOTED_PIECE_MASK   = 0b000011110000000000000000;
    private static final int CAPTURE_FLAG_MASK     = 0b000100000000000000000000;
    private static final int DOUBLE_PUSH_FLAG_MASK = 0b001000000000000000000000;
    private static final int EN_PASSANT_FLAG_MASK  = 0b010000000000000000000000;
    private static final int CASTLING_FLAG_MASK    = 0b100000000000000000000000;

    public static final String[] TYPE_TO_UNICODE_PIECE =  {
            "", "♞", "♝", "♜","♛","♚","", "♘", "♗", "♖", "♕", "♔",
    };

    public static int getSourceSquare(int move){
        return move & SOURCE_SQUARE_MASK;
    }

    public static int getTargetSquare(int move) {
        return (move & TARGET_SQUARE_MASK) >>> 6;
    }

    public static int getPieceType(int move) {
        return (move & PIECE_TYPE_MASK) >>> 12;
    }

    public static int getPromotedPiece(int move) {
        return (move & PROMOTED_PIECE_MASK) >>> 16;
    }

    public static boolean isCapture(int move) {
        return (move & CAPTURE_FLAG_MASK) != 0;
    }

    public static boolean isDoublePush(int move) {
        return (move & DOUBLE_PUSH_FLAG_MASK) != 0;
    }

    public static boolean isEnPassant(int move) {
        return (move & EN_PASSANT_FLAG_MASK) != 0;
    }

    public static boolean isCastling(int move) {
        return (move & CASTLING_FLAG_MASK) != 0;
    }

    public static boolean isPromotion(int move) {
        return getPromotedPiece(move) != 0;
    }

    public static int encodeMove(int sourceSquare,int targetSquare,int pieceType,int promotedPiece,int captureFlag,int doublePushFlag,int enPassantFlag,int castlingFlag) {
        return sourceSquare | (targetSquare << 6) | (pieceType << 12) |
                (promotedPiece << 16) | (captureFlag << 20) | (doublePushFlag << 21) |
                (enPassantFlag << 22) | (castlingFlag << 23);
    }

    public static void printMove(int encodedMove) {
        int sourceSquare = getSourceSquare(encodedMove);
        int targetSquare = getTargetSquare(encodedMove);
        int pieceType = getPieceType(encodedMove);
        System.out.println(TYPE_TO_UNICODE_PIECE[pieceType] + Utils.indexToCoordinate(sourceSquare) + " "  + Utils.indexToCoordinate(targetSquare));
        int promotedPiece = getPromotedPiece(encodedMove);
        if(promotedPiece != 0 && promotedPiece != 6)
            System.out.println("Promotion to: " + TYPE_TO_UNICODE_PIECE[promotedPiece]);
    }

    public static void printMoves(ArrayList<Integer> moveList) {
        for(int move : moveList) {
            printMove(move);
        }
    }

}
