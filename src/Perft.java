package botnik.chess.server.chessai;

import java.util.ArrayList;

public class Perft {

    public static final byte CAPTURES   = 0b00001;
    public static final byte EN_PASSANT = 0b00010;
    public static final byte CASTLING   = 0b00100;
    public static final byte PROMOTION  = 0b01000;
    public static final byte CHECK      = 0b10000;
    public static final byte ALL_FLAGS  = 0b11111;

    private static final int NODES_INDEX      = 0;
    private static final int CAPTURES_INDEX   = 1;
    private static final int EN_PASSANT_INDEX = 2;
    private static final int CASTLING_INDEX   = 3;
    private static final int PROMOTION_INDEX  = 4;
    private static final int CHECK_INDEX      = 5;

    public static int[] perft(Board board,int depth,byte flags) {
        return perft(board,depth,flags,(byte)0);
    }

    public static void prettyPrint(Board board,int depth) {
        int totalNodes = 0;
        ArrayList<Integer> moves = board.generateMoves();
        for(int move : moves) {
            Board copy = board.copy();
            boolean validMove = copy.makeMove(move);
            copy.historyIndex--;
            if(validMove) {
                int result = perft(copy,depth - 1);
                totalNodes += result;
                System.out.println(String.format("%s%s: %d",Utils.indexToCoordinate(Move.getSourceSquare(move)),Utils.indexToCoordinate(Move.getTargetSquare(move)),result));
            }
        }
        System.out.println("\nTotal Nodes: " + totalNodes + "\n");
    }

    public static void prettyPrint(Board board,int depth,byte flags) {
        if(flags == 0) {
            prettyPrint(board, depth);
            return;
        }
        int[] results = perft(board,depth,flags,(byte)0);
        if((flags & CAPTURES) != 0)
            System.out.println("Capture Nodes: " + results[CAPTURES_INDEX]);
        if((flags & EN_PASSANT) != 0)
            System.out.println("E.p. Nodes: " + results[EN_PASSANT_INDEX]);
        if((flags & CASTLING) != 0)
            System.out.println("Castling Nodes: " + results[CASTLING_INDEX]);
        if((flags & PROMOTION) != 0)
            System.out.println("Promotion Nodes: " + results[PROMOTION_INDEX]);
        System.out.println("\nTotal Nodes: " + results[NODES_INDEX] + "\n");
    }


    private static int perft(Board board, int depth){
        if(depth == 0) {
            return 1;
        }
        int counter = 0;
        ArrayList<Integer> moves = board.generateMoves();
        for(int move : moves) {
            Board copy = board.copy();
            boolean validMove = copy.makeMove(move);
            copy.historyIndex--;
            if(validMove) {
                counter += perft(copy,depth - 1);
            }
        }
        return counter;
    }

    private static int[] perft(Board board,int depth,byte flags,byte activeFlags) {
        int[] counters = new int[5];
        if(depth == 0) {
            counters[NODES_INDEX]++;
            if((flags & CAPTURES) != 0 && (activeFlags & CAPTURES) != 0)
                counters[CAPTURES_INDEX]++;
            if((flags & EN_PASSANT) != 0 && (activeFlags & EN_PASSANT) != 0)
                counters[EN_PASSANT_INDEX]++;
            if((flags & CASTLING) != 0 && (activeFlags & CASTLING) != 0)
                counters[CASTLING_INDEX]++;
            if((flags & PROMOTION) != 0 && (activeFlags & PROMOTION) != 0)
                counters[PROMOTION_INDEX]++;
            return counters;
        }
        ArrayList<Integer> moves = board.generateMoves();
        for(int move : moves){
            Board copy = board.copy();
            boolean validMove = copy.makeMove(move);
            copy.historyIndex--;
            if(validMove) {
                int[] deeperCounters = perft(copy,depth-1,flags,
                        (byte)((Move.isCapture(move)  ? CAPTURES : 0) | (Move.isEnPassant(move) ? EN_PASSANT : 0) |
                               (Move.isCastling(move) ? CASTLING : 0) | (Move.isPromotion(move) ? PROMOTION : 0)));
                for(int i = 0 ; i < 5 ; i++)
                    counters[i] += deeperCounters[i];
            }
        }
        return counters;
    }

    public static int _perft(Board board,int depth,byte flags) {
        return _perft(board,depth,flags,(byte)0);
    }

    private static int _perft(Board board,int depth,byte flags,byte activeFlags) {
        int counter = 0;
        if(depth == 0) {
            return 1;
        }
        ArrayList<Integer> moves = board.generateMoves();
        for(int move : moves){
            Board copy = board.copy();
            boolean validMove = copy.makeMove(move);
            copy.historyIndex--;
            if(validMove) {
                counter += _perft(copy,depth-1,(byte)0,(byte)0);
            }
        }
        return counter;
    }
}
