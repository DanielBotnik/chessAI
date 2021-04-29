
public class Board {

    /*

      8     56  57  58  59  60  61  62  63
      7     48  49  50  51  52  53  54  55
      6     40  41  42  43  44  45  46  47
      5     32  33  34  35  36  37  38  39
      4     24  25  26  27  28  29  30  31
      3     16  17  18  19  20  21  22  23
      2      8   9   10  11  12  13  14  15
      1      0   1   2   3   4   5   6   7

            a   b   c   d   e   f   g   h
     */

    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final long NOT_H_FILE = 0x7F7F7F7F7F7F7F7FL;
    public static final long NOT_A_FILE = 0xFEFEFEFEFEFEFEFEL;

    private long whitePawns;
    private long whiteKnights;
    private long whiteBishops;
    private long whiteRooks;
    private long whiteQueens;
    private long whiteKings;

    private long blackPawns;
    private long blackKnights;
    private long blackBishops;
    private long blackRooks;
    private long blackQueens;
    private long blackKings;

    private long whitePieces;
    private long blackPieces;

    private long[][] maskPawnAttacks;

    public Board(){
        whitePawns = 0x000000000000FF00L;
        whiteKnights = 0x0000000000000042L;
        whiteBishops = 0x0000000000000024L;
        whiteRooks = 0x0000000000000081L;
        whiteQueens = 0x000000000000008L;
        whiteKings = 0x0000000000000010L;

        blackPawns = 0x00FF000000000000L;
        blackKnights = 0x4200000000000000L;
        blackBishops = 0x2400000000000000L;
        blackRooks = 0x8100000000000000L;
        blackQueens = 0x0800000000000000L;
        blackKings = 0x1000000000000000L;

        whitePieces = 0x000000000000FFFFL;
        blackPieces = 0xFFFF000000000000L;

        maskPawnAttacks = new long[2][64];
        initMaskPawnAttacks();

        for(int j = 0 ; j < 2 ; j++)
            for(int i = 0 ; i < 64 ; i++)
                System.out.println(BitSet.convertBoardToString(maskPawnAttacks[j][i]));

    }


    private long getWhiteMaskPawnAttack(int square) {
        long attacks = 0L;
        long board = 1L<<square;
        if(((board << 7) & NOT_H_FILE) != 0) attacks |= (board<<7);
        if(((board << 9) & NOT_A_FILE) != 0) attacks |= (board<<9);
        return attacks;
    }

    private long getBlackMaskPawnAttack(int square) {
        long attacks = 0L;
        long board = 1L<<square;
        if(((board >> 7) & NOT_A_FILE) != 0) attacks |= (board >> 7);
        if(((board >> 9) & NOT_H_FILE) != 0) attacks |= (board >> 9);
        return attacks;
    }

    private void initMaskPawnAttacks() {
        for(int i = 0 ; i < 64 ; i++) {
            maskPawnAttacks[WHITE][i] = getWhiteMaskPawnAttack(i);
            maskPawnAttacks[BLACK][i] = getBlackMaskPawnAttack(i);
        }
    }

    //Makes Board out of fen string
    public Board(String fen){

    }

   // private

    public void printBoard(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0 ; i < 64 ; i++) {
            if(((whitePawns >> i) & 1) == 1) stringBuilder.append("P|");
            else if(((whiteKnights >> i) & 1) == 1) stringBuilder.append("N|");
            else if(((whiteBishops >> i) & 1) == 1) stringBuilder.append("B|");
            else if(((whiteRooks >> i) & 1) == 1) stringBuilder.append("R|");
            else if(((whiteQueens >> i) & 1) == 1) stringBuilder.append("Q|");
            else if(((whiteKings >> i) & 1) == 1) stringBuilder.append("K|");
            else if(((blackPawns >> i) & 1) == 1) stringBuilder.append("p|");
            else if(((blackKnights >> i) & 1) == 1) stringBuilder.append("n|");
            else if(((blackBishops >> i) & 1) == 1) stringBuilder.append("b|");
            else if(((blackRooks >> i) & 1) == 1) stringBuilder.append("r|");
            else if(((blackQueens >> i) & 1) == 1) stringBuilder.append("q|");
            else if(((blackKings >> i) & 1) == 1) stringBuilder.append("k|");
            else stringBuilder.append(" |");
            if(i % 8 == 7)
                stringBuilder.append("\n------------------\n|");
        }
        System.out.println(stringBuilder);
    }
}
