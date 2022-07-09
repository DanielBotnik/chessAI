package botnik.chess.server.chessai;

public class Zobrist {

    public static final long[][] PIECE_KEYS   = new long[12][64];
    public static final long[] ENPASSANT_KEYS = new long[64];
    public static final long[] CASTLING_KEYS  = new long[16];
    public static final long SIDE_TO_MOVE_KEY = BitBoard.randomBoard();

    static {
        initRandomKeys();
    }

    private static void initRandomKeys() {
        for(int piece = 0 ; piece < 12 ; piece++)
            for(int square = 0 ; square < 64 ; square++)
                PIECE_KEYS[piece][square] = BitBoard.randomBoard();
        for(int square = 0 ; square < 64 ; square++)
            ENPASSANT_KEYS[square] = BitBoard.randomBoard();
        for(int castling = 0 ; castling < 16 ; castling++)
            CASTLING_KEYS[castling] = BitBoard.randomBoard();
    }

    public static long generateKeyFromBoard(Board board){
        long key = 0L;
        int piece = 0;
        for(long bitBoard : board.getBitBoards()){
            while(bitBoard != 0) {
                int square = BitBoard.getLSBIndex(bitBoard);
                bitBoard ^= (1L << square);
                key ^= PIECE_KEYS[piece][square];
            }
            piece++;
        }
        key ^= CASTLING_KEYS[board.getCastlingRights()];
        key ^= SIDE_TO_MOVE_KEY * board.getSideToMove();
        if(board.isEnPassant())
            key ^= ENPASSANT_KEYS[board.getEnPassantSquare()];
        return key;
    }

    public static long generateKeyFromFen(String fen) {
        return generateKeyFromBoard(new Board(fen));
    }

}
