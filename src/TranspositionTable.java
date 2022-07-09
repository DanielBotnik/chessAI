package botnik.chess.server.chessai;

import java.util.Arrays;

public class TranspositionTable {

    private static final int HASH_GAP   = 4;
    private static final int HASH_SIZE  = 0x400000;
    private static final int TABLE_SIZE = HASH_GAP * HASH_SIZE;

    public static final int EXACT = 0;
    public static final int ALPHA = 1;
    public static final int BETA  = 2;

    private long[] keys;
    private int[] entries;

    public TranspositionTable() {
        keys = new long[TABLE_SIZE];
        entries = new int[TABLE_SIZE];
    }

    public void storeEvaluation(long key,int evaluation,int depth,int flag) {
        int startIndex = (int)(Math.abs(key % HASH_SIZE)) * HASH_GAP;
        int i = startIndex;
        while(i < startIndex + HASH_GAP & keys[i] != 0) i++;
        keys[i] = key;
        entries[i] = Entry.makeEntry(depth,evaluation,flag);
    }

    public int getEvaluation(long key,int alpha,int beta,int depth) {
        int startIndex = (int)(Math.abs(key % HASH_SIZE)) * HASH_GAP;
        for(int i = startIndex; i < startIndex + HASH_GAP ; i++)
            if(keys[i] == key) {
                int entry = entries[i];
                if(Entry.getDepth(entry) >= depth) {
                    int score = Entry.getScore(entry);
                    int flag = Entry.getFlag(entry);
                    if(flag == EXACT)
                        return score;
                    if(flag == ALPHA && score <= alpha)
                        return alpha;
                    if(flag == BETA && score >= beta)
                        return beta;
                }
            }
        return Entry.EMPTY_ENTRY;
    }

    public void clear(){
        Arrays.fill(keys,0);
    }

}
