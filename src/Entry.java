package botnik.chess.server.chessai;

public class Entry {

    private static final int DEPTH_MASK = 0b00000000000000000011111111;
    private static final int SCORE_MASK = 0b00111111111111111100000000;
    private static final int FLAG_MASK  = 0b11000000000000000000000000;

    public static final int EMPTY_ENTRY = 0b100000000000000000000000000;

    public static final int getDepth(int entry) {
        return entry & DEPTH_MASK;
    }

    public static final int getScore(int entry){
        return entry >>> 8;
    }

    public static final int getFlag(int entry){
        return entry >>> 24;
    }

    public static final int makeEntry(int depth,int score,int flag) {
        return depth | (score << 8) | (flag << 24);
    }
}
