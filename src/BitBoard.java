package botnik.chess.server.chessai;

import java.util.SplittableRandom;

public class BitBoard {

    private final static SplittableRandom random = new SplittableRandom(4919);

    public static final long NOT_H_FILE   = 0x7F7F7F7F7F7F7F7FL;
    public static final long NOT_A_FILE   = 0xFEFEFEFEFEFEFEFEL;
    public static final long NOT_GH_FILES = 0x3F3F3F3F3F3F3F3FL;
    public static final long NOT_AB_FILES = 0xFCFCFCFCFCFCFCFCL;
    public static final long SECOND_RANK  = 0x000000000000FF00L;
    public static final long SEVENTH_RANK = 0x00FF000000000000L;

    public static int getLSBIndex(long i) {
        return Long.numberOfTrailingZeros(i & ~(i-1));
    }

    public static String convertBoardToString(long board){
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder rowBuilder = new StringBuilder();
        for(int i = 0 ; i < 64 ; i++) {
            rowBuilder.append((board >> (63-i)) & 1);
            if(i % 8 == 7 ) {
                stringBuilder.append(rowBuilder.reverse()).append('\n');
                rowBuilder.setLength(0);
            }

        }
        return stringBuilder.toString();
    }

    public static long randomBoard(){
        return random.nextLong();
    }

    // Stockfish implementation
    public static long sparseRandomBoard(){
        return randomBoard() & randomBoard() & randomBoard();
    }

    public static long makeOccupiedBoard(int index,int numberOfBits, long attackMask) {
        long occupiedBoard = 0L;
        for(int i = 0 ; i < numberOfBits ; i++) {
            int square = BitBoard.getLSBIndex(attackMask);
            attackMask ^= (1L<<square);
            if((index & (1 << i)) != 0)
                occupiedBoard |= (1L<<square);
        }
        return occupiedBoard;
    }

}
