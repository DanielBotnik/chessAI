package botnik.chess.server.chessai;

public class Attacks {

    public static final byte[] BISHOP_RELEVANT_BITS = {
            6,5,5,5,5,5,5,6,
            5,5,5,5,5,5,5,5,
            5,5,7,7,7,7,5,5,
            5,5,7,9,9,7,5,5,
            5,5,7,9,9,7,5,5,
            5,5,7,7,7,7,5,5,
            5,5,5,5,5,5,5,5,
            6,5,5,5,5,5,5,6
    };

    public static final byte[] ROOK_RELEVANT_BITS = {
            12,11,11,11,11,11,11,12,
            11,10,10,10,10,10,10,11,
            11,10,10,10,10,10,10,11,
            11,10,10,10,10,10,10,11,
            11,10,10,10,10,10,10,11,
            11,10,10,10,10,10,10,11,
            11,10,10,10,10,10,10,11,
            12,11,11,11,11,11,11,12
    };

    public static final long[] BISHOP_MAGIC_NUMBERS = {
            1153660380749267712L,
            987395818324608L,
            73821227961032320L,
            74344854183749696L,
            1208093932970446848L,
            5188305171406454784L,
            146516538686449224L,
            3096783895462914L,
            16536691523223680L,
            1227248559508717712L,
            19877175763456L,
            72343811765846016L,
            6052839068490990592L,
            315272933492916256L,
            4611686310520885248L,
            146163369092L,
            586035369419481216L,
            598271800246792L,
            144687488240255492L,
            -4303189014436863984L,
            1125908531464448L,
            -8642689152652409856L,
            175930727473160L,
            4684034021519405088L,
            289092943160018948L,
            576782101895448624L,
            11278790345097232L,
            1153211776028918016L,
            282574489403392L,
            -9077002849934570484L,
            4615081874115560512L,
            178155245666820L,
            180715870728101900L,
            146388995973712033L,
            110909941245675010L,
            7494025102297137280L,
            -8628878743563010016L,
            27022149668110436L,
            282613143177216L,
            1155173993762850880L,
            2542208934744065L,
            7071918894717472768L,
            1153629591185664003L,
            162201338443206784L,
            4521604403241472L,
            -5764308451475849148L,
            19422907281457664L,
            19153499554056708L,
            1130302383067136L,
            290520693535152160L,
            2252901685659648L,
            326512111218393088L,
            3389261856637184L,
            153273091626639876L,
            2263998093590784L,
            585469056472089620L,
            2305879327591629312L,
            -4033780344350826432L,
            17601348571136L,
            1157495473666557953L,
            576465153572225312L,
            396352226526241320L,
            432521521538793600L,
            -2304715958054022847L,
    };
    public static final long[] ROOK_MAGIC_NUMBERS = {
            36028935531692052L,
            -9079238973122903806L,
            -8899095269887410008L,
            612498347563941894L,
            -8754988879477211007L,
            1224983496708065282L,
            2089674627327467776L,
            1297046598689243266L,
            -6912884688373678048L,
            3173489899810725888L,
            1738671068581740801L,
            144396731772577025L,
            -7998111441757929456L,
            5066558171025416L,
            11540491795431680L,
            639651886193967360L,
            4611847096885051424L,
            4503875579027456L,
            -6626905563894116351L,
            9896142570752L,
            288371663530099712L,
            5190258382879064576L,
            441357162334257480L,
            1132496985262275L,
            288371117937148164L,
            81082390847430730L,
            292742772411073536L,
            10326632535949440L,
            -9221115836846571392L,
            -8031606976870873084L,
            432636264795078914L,
            -8052400391020347327L,
            54043505445568640L,
            1513279912264343554L,
            738882610008064L,
            581787713538112L,
            13515199084496896L,
            72059795217056768L,
            13520149093024272L,
            1172062369992409185L,
            5566607607617585152L,
            4647785321898459136L,
            4652218552521097232L,
            301742308913741840L,
            1155173442933490688L,
            -5764325893432868846L,
            562954318774280L,
            40532675827728388L,
            72902568728077056L,
            158505772253440L,
            4611703612763014272L,
            292769194779410688L,
            5044313135075296512L,
            2346942756007575680L,
            2306812709768192L,
            2305865003900879360L,
            281631764156930L,
            594545532444115219L,
            108368003543861265L,
            6760897134460961L,
            78250730923233290L,
            36310289175676929L,
            2305887058417291412L,
            5066825012380674L,
    };

//    private long findMagicNumber(int square, Board.Piece type) {
//        int relevantBits = type == Board.Piece.BISHOP ? BISHOP_RELEVANT_BITS[square] : ROOK_RELEVANT_BITS[square];
//        long[] occupancies = new long[4096];
//        long[] attacks = new long[4096];
//        long[] usedAttacks = new long[4096];
//        long attackMask = type == Board.Piece.BISHOP ? getBishopAttackMask(square) : getRookAttackMask(square);
//        int occupancyIndices = 1<<relevantBits;
//        for(int i = 0 ; i < occupancyIndices ; i++) {
//            occupancies[i] = BitBoard.makeOccupiedBoard(i,relevantBits,attackMask);
//            attacks[i] = type == Board.Piece.BISHOP ? getBishopAttackWithBlocks(square,occupancies[i]) : getRookAttackWithBlocks(square,occupancies[i]);
//        }
//        for(int randomCount = 0 ; randomCount < 100000000 ; randomCount++) {
//            long magicNumber = BitBoard.sparseRandomBoard();
//            if(BitBoard.countBits((magicNumber * attackMask) & 0xFF00000000000000L) < 6) continue;
//            java.util.Arrays.fill(usedAttacks,0);
//            int i = 0;
//            boolean fail = false;
//            for(; !fail && i < occupancyIndices ; i++) {
//                long magicIndex = ((occupancies[i] * magicNumber) >>> (64 - relevantBits));
//                if(usedAttacks[(int)magicIndex] == 0L)
//                    usedAttacks[(int)magicIndex] = attacks[i];
//                else if(usedAttacks[(int)magicIndex] != attacks[i])
//                    fail = true;
//            }
//            if(!fail)
//                return magicNumber;
//        }
//        System.out.println("how we got here? i gave you bajilion options");
//        return 0L;
//    }

    public static long[][] pawnsAttackTable = pawnsAttackTable = new long[2][64];
    public static long[] knightsAttackTable = new long[64];
    public static long[] kingsAttackTable = new long[64];
    private static long[] bishopsMasks = new long[64];
    private static long[][] bishopsAttackTable = new long[64][512];
    private static long[] rooksMasks = new long[64];
    private static long[][] rooksAttackTable = new long[64][4096];
    private static long[][] pawnsMovesMask = new long[2][64];

    static {
        initLeaperPieces();
        initSlidingPieces();
    }

    public static long getWhitePawnAttackMask(int square) {
        long attacks = 0L;
        long board = 1L<<square;
        if(((board << 7) & BitBoard.NOT_H_FILE) != 0) attacks |= (board<<7);
        if(((board << 9) & BitBoard.NOT_A_FILE) != 0) attacks |= (board<<9);
        return attacks;
    }

    public static long getBlackPawnAttackMask(int square) {
        long attacks = 0L;
        long board = 1L<<square;
        if(((board >>> 7) & BitBoard.NOT_A_FILE) != 0) attacks |= (board >>> 7);
        if(((board >>> 9) & BitBoard.NOT_H_FILE) != 0) attacks |= (board >>> 9);
        return attacks;
    }

    public static long getWhitePawnMoveMask(int square) {
        if(square > 55)
            return 0L;
        long moves = 0L;
        if(square >= 8 && square <= 15) {
            moves |= 1L << (square + 16);
        }
        moves |= 1L << (square + 8);
        return moves;
    }

    public static long getBlackPawnMoveMask(int square) {
        if(square < 8)
            return 0L;
        long moves = 0L;
        if(square <= 55 && square >= 48) {
            moves |= 1L << (square - 16);
        }
        moves |= 1L << (square - 8);
        return moves;
    }

    public static long getKnightAttackMask(int square) {
        long attacks = 0L;
        long board = 1L<<square;
        if(((board << 17) & BitBoard.NOT_A_FILE) != 0) attacks |= (board << 17);
        if(((board << 15) & BitBoard.NOT_H_FILE) != 0) attacks |= (board << 15);
        if(((board << 10) & BitBoard.NOT_AB_FILES) != 0) attacks |= (board << 10);
        if(((board << 6) & BitBoard.NOT_GH_FILES) != 0) attacks |= (board << 6);
        if(((board >>> 17) & BitBoard.NOT_H_FILE) != 0) attacks |= (board >>> 17);
        if(((board >>> 15) & BitBoard.NOT_A_FILE) != 0) attacks |= (board >>> 15);
        if(((board >>> 10) & BitBoard.NOT_GH_FILES) != 0) attacks |= (board >>> 10);
        if(((board >>> 6) & BitBoard.NOT_AB_FILES) != 0) attacks |= (board >>> 6);
        return attacks;
    }

    public static long getKingAttackMask(int square){
        long attacks = 0L;
        long board = 1L<<square;
        if(((board << 1) & BitBoard.NOT_A_FILE) != 0) attacks |= (board << 1);
        if(((board << 7) & BitBoard.NOT_H_FILE) != 0) attacks |= (board << 7);
        if((board << 8) != 0) attacks |= (board << 8);
        if(((board << 9) & BitBoard.NOT_A_FILE) != 0) attacks |=  (board << 9);
        if(((board >>> 1) & BitBoard.NOT_H_FILE) != 0) attacks |= (board >>> 1);
        if(((board >>> 7) & BitBoard.NOT_A_FILE) != 0)  attacks  |= (board >>> 7);
        if((board >>> 8) != 0) attacks |= (board >>> 8);
        if(((board >>> 9) & BitBoard.NOT_H_FILE) != 0) attacks |= (board >>> 9);
        return attacks;
    }

    public static long getBishopAttackMask(int square){
        long attacks = 0L;

        int rank = square / 8;
        int file = square % 8;

        for(int r = rank + 1, f = file + 1; r < 7 && f < 7 ; r++,f++) attacks |= (1L<<(r * 8 + f));
        for(int r = rank - 1, f = file + 1; r > 0 && f < 7 ; r--,f++) attacks |= (1L<<(r * 8 + f));
        for(int r = rank + 1, f = file - 1; r < 7 && f > 0 ; r++,f--) attacks |= (1L<<(r * 8 + f));
        for(int r = rank - 1, f = file - 1; r > 0 && f > 0 ; r--,f--) attacks |= (1L<<(r * 8 + f));

        return attacks;
    }

    public static long getRookAttackMask(int square){
        long attacks = 0L;

        int rank = square / 8;
        int file = square % 8;

        for(int r = rank + 1 ; r < 7 ; r++) attacks |= (1L<<(r * 8 + file));
        for(int r = rank - 1 ; r > 0 ; r--) attacks |= (1L<<(r * 8 + file));
        for(int f = file + 1 ; f < 7 ; f++) attacks |= (1L<<(rank * 8 + f));
        for(int f = file - 1 ; f > 0 ; f--) attacks |= (1L<<(rank * 8 + f));

        return attacks;
    }

    public static long getBishopAttackWithBlocks(int square,long blockingBoard) {
        long attacks = 0L;

        int rank = square / 8;
        int file = square % 8;

        for(int r = rank + 1, f = file + 1; r < 8 && f < 8 ; r++,f++) {
            attacks |= (1L<<(r * 8 + f));
            if(((1L<<(r * 8 + f)) & blockingBoard) != 0) break;
        }
        for(int r = rank - 1, f = file + 1; r >= 0 && f < 8 ; r--,f++) {
            attacks |= (1L<<(r * 8 + f));
            if(((1L<<(r * 8 + f)) & blockingBoard) != 0) break;
        }
        for(int r = rank + 1, f = file - 1; r < 8 && f >= 0 ; r++,f--) {
            attacks |= (1L<<(r * 8 + f));
            if(((1L<<(r * 8 + f)) & blockingBoard) != 0) break;
        }
        for(int r = rank - 1, f = file - 1; r >= 0 && f >= 0 ; r--,f--){
            attacks |= (1L<<(r * 8 + f));
            if(((1L<<(r * 8 + f)) & blockingBoard) != 0) break;
        }

        return attacks;
    }

    public static long getRookAttackWithBlocks(int square,long blockingBoard) {
        long attacks = 0L;

        int rank = square / 8;
        int file = square % 8;

        for(int r = rank + 1 ; r < 8 ; r++) {
            attacks |= (1L<<(r * 8 + file));
            if(((1L<<(r * 8 + file)) & blockingBoard) != 0) break;
        }
        for(int r = rank - 1 ; r >= 0 ; r--) {
            attacks |= (1L<<(r * 8 + file));
            if(((1L<<(r * 8 + file)) & blockingBoard) != 0) break;
        }
        for(int f = file + 1 ; f < 8 ; f++){
            attacks |= (1L<<(rank * 8 + f));
            if((1L<<(rank * 8 + f) & blockingBoard) != 0) break;
        }
        for(int f = file - 1 ; f >= 0 ; f--) {
            attacks |= (1L<<(rank * 8 + f));
            if(((1L<<(rank * 8 + f)) & blockingBoard) != 0) break;
        }

        return attacks;
    }

    private static void initLeaperPieces() {
        for(int i = 0 ; i < 64 ; i++) {
            pawnsAttackTable[Color.WHITE][i] = getWhitePawnAttackMask(i);
            pawnsAttackTable[Color.BLACK][i] = getBlackPawnAttackMask(i);
            pawnsMovesMask[Color.WHITE][i] = getWhitePawnMoveMask(i);
            pawnsMovesMask[Color.BLACK][i] = getBlackPawnMoveMask(i);
            knightsAttackTable[i] = getKnightAttackMask(i);
            kingsAttackTable[i] = getKingAttackMask(i);
        }
    }

    private static void initBishops() {
        for(int square = 0 ; square < 64 ; square++) {
            bishopsMasks[square] = Attacks.getBishopAttackMask(square);
            long attackMask = bishopsMasks[square];
            int relevantBits = BISHOP_RELEVANT_BITS[square];
            int occupiedIndices = 1 << relevantBits;
            for(int i = 0 ; i < occupiedIndices ; i++) {
                long occupancy =  BitBoard.makeOccupiedBoard(i,relevantBits,attackMask);
                int magicIndex = (int)((occupancy * BISHOP_MAGIC_NUMBERS[square]) >>> (64 - relevantBits));
                bishopsAttackTable[square][magicIndex] = Attacks.getBishopAttackWithBlocks(square,occupancy);
            }
        }
    }

    private static void initRooks() {
        for(int square = 0 ; square < 64 ; square++) {
            rooksMasks[square] = Attacks.getRookAttackMask(square);
            long attackMask = rooksMasks[square];
            int relevantBits = ROOK_RELEVANT_BITS[square];
            int occupiedIndices = 1 << relevantBits;
            for(int i = 0 ; i < occupiedIndices ; i++) {
                long occupancy = BitBoard.makeOccupiedBoard(i,relevantBits,attackMask);
                int magicIndex = (int)((occupancy * ROOK_MAGIC_NUMBERS[square]) >>> (64 - relevantBits));
                rooksAttackTable[square][magicIndex] = Attacks.getRookAttackWithBlocks(square,occupancy);
            }
        }
    }

    private static void initSlidingPieces() {
        initBishops();
        initRooks();
    }

    public static long getBishopAttacks(int square,long occupancy) {
        occupancy &= bishopsMasks[square];
        occupancy *= BISHOP_MAGIC_NUMBERS[square];
        occupancy >>>= (64 - BISHOP_RELEVANT_BITS[square]);
        return bishopsAttackTable[square][(int)occupancy];
    }

    public static long getRookAttacks(int square,long occupancy) {
        occupancy &= rooksMasks[square];
        occupancy *= ROOK_MAGIC_NUMBERS[square];
        occupancy >>>= (64 - ROOK_RELEVANT_BITS[square]);
        return rooksAttackTable[square][(int)occupancy];
    }

    public static long getQueenAttacks(int square,long occupancy) {
        return getBishopAttacks(square,occupancy) | getRookAttacks(square,occupancy);
    }

}
