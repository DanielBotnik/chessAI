package botnik.chess.server.chessai;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {

    private static final int HISTORY_SIZE = 512;
    private static final int REPETITION_LOOK_BACK = 20;

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

    //boards are the following order PNBRQKpnbrqk
    private long[] bitBoards;
    private long[] occupancies;

    private long[][] bitBoardsHistory;

    private long key;

    private long[] history;
    public int historyIndex;
    private int lastPawnCaptureMove;

    // 0 bit, whose turn is it, 0 -> white, 1 -> black
    // 1 is there En Passant, 0 -> no, 1 -> yes
    // 2-7 En Passant location
    // 8 bit, can white castle king side, 0 -> no, 1 -> yes
    // 9 bit, can white castle queen side
    // 10 bit can black castle king side
    // 11 bit can black castle queen side
    private int state;

    public long[] getBitBoards() {
        return bitBoards;
    }

    public long getWhitePawns() {
        return bitBoards[Piece.PAWN];
    }

    public long getWhiteKnights() {
        return bitBoards[Piece.KNIGHT];
    }

    public long getWhiteBishops() {
        return bitBoards[Piece.BISHOP];
    }

    public long getWhiteRooks() {
        return bitBoards[Piece.ROOK];
    }

    public long getWhiteQueens() {
        return bitBoards[Piece.QUEEN];
    }

    public long getWhiteKing() {
        return bitBoards[Piece.KING];
    }

    public long getBlackPawns() {
        return bitBoards[Piece.B_PAWN];
    }

    public long getBlackKnights() {
        return bitBoards[Piece.B_KNIGHT];
    }

    public long getBlackBishops() {
        return bitBoards[Piece.B_BISHOP];
    }

    public long getBlackRooks() {
        return bitBoards[Piece.B_ROOK];
    }

    public long getBlackQueens() {
        return bitBoards[Piece.B_QUEEN];
    }

    public long getBlackKing() {
        return bitBoards[Piece.B_KING];
    }

    public long getKey(){
        return key;
    }

    public Board(){
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Board(String fen){

        bitBoards = new long[Piece.NUM_OF_PIECES];
        occupancies = new long[3];

        String[] segments = fen.split(" ");

        int row = 7,file = 0;
        int i = 0;
        for(; i < segments[0].length(); i++) {
            char c = segments[0].charAt(i);
            if(c == '/') {
                if(file != 8)
                    throw new IllegalArgumentException("Invalid FEN string: " + segments[0].charAt(i));
                file = 0;
                row--;
            }
            else if(file > 7)
                throw new IllegalArgumentException("Invalid FEN string: " + segments[0].charAt(i));
            else if(c >= '1' && c <= '8')
                file += Character.getNumericValue(c);
            else {
                bitBoards[Fen.FENCharToBitBoardIndex(c)] |= 1L << (row * 8 + file);
                file++;
            }
        }

        if(segments[1].charAt(0) == 'b')
            state |= State.sideToMoveMask;
        if(segments[2].contains("K")) state |= State.whiteCastleKingSideMask;
        if(segments[2].contains("Q")) state |= State.whiteCastleQueenSideMask;
        if(segments[2].contains("k")) state |= State.blackCastleKingSideMask;
        if(segments[2].contains("q")) state |= State.blackCastleQueenSideMask;
        if(!segments[3].equals("-")) {
            state |= State.isEnPassantMask;
            state |= Utils.chessCoordinateToIndex(segments[3])<<2;
            if((segments[1].charAt(0) == 'w' && (bitBoards[Piece.B_PAWN] & (1L << (Utils.chessCoordinateToIndex(segments[3]) - 8))) == 0)
            || (segments[1].charAt(0) == 'b' && (bitBoards[Piece.PAWN] & (1L << Utils.chessCoordinateToIndex(segments[3]) + 8)) == 0))
                throw new IllegalArgumentException("Invalid FEN String: ");
        }
        if(!segments[4].equals("-")) {
            lastPawnCaptureMove = Integer.parseInt(segments[4]);
        }
        for(int pieceType = Piece.PAWN; pieceType <= Piece.KING; pieceType++)
            occupancies[Color.WHITE] |= bitBoards[pieceType];
        for(int pieceType = Piece.B_PAWN; pieceType <= Piece.B_KING; pieceType++)
            occupancies[Color.BLACK] |= bitBoards[pieceType];
        occupancies[Color.Both] = occupancies[Color.BLACK] | occupancies[Color.WHITE];
        key = Zobrist.generateKeyFromBoard(this);
        history = new long[HISTORY_SIZE];
        history[0] = key;
        historyIndex = 1;
    }
    public boolean isSquareAttacked(int square,int attackingSide){
        return ((Attacks.pawnsAttackTable[1-attackingSide][square] & bitBoards[Piece.DIFFERENCE*attackingSide]) |
                (Attacks.knightsAttackTable[square] & bitBoards[Piece.DIFFERENCE*attackingSide+Piece.KNIGHT]) |
                (Attacks.getBishopAttacks(square,occupancies[Color.Both]) & bitBoards[Piece.DIFFERENCE*attackingSide+Piece.BISHOP]) |
                (Attacks.getRookAttacks(square,occupancies[Color.Both]) & bitBoards[Piece.DIFFERENCE*attackingSide+Piece.ROOK]) |
                (Attacks.getQueenAttacks(square,occupancies[Color.Both]) & bitBoards[Piece.DIFFERENCE*attackingSide+Piece.QUEEN]) |
                (Attacks.kingsAttackTable[square] & bitBoards[Piece.DIFFERENCE*attackingSide+Piece.KING])) != 0;
    }

    public int getSideToMove() {
        return State.getSideToMove(state);
    }

    public int getEnPassantSquare() {
        return State.getEnPassantSquare(state);
    }

    public boolean isEnPassant(){
        return State.isEnPassant(state);
    }

    public int getCastlingRights() {
        return State.getCastlingRights(state);
    }

    public ArrayList<Integer> generateMoves(){
        ArrayList<Integer> moveList = new ArrayList<>(60);
        int turn = State.getSideToMove(state);
        int oppositeTurn = turn^1;
        int sourceSquare, targetSquare;
        long bitBoard, attacks;
        for(int pieceType = 6 * turn; pieceType < 6 + 6 * turn ; pieceType++) {
            bitBoard = bitBoards[pieceType];
            if (pieceType % 6 == Piece.PAWN) {
                int moveBonus;
                long marchingPawns, doublePushPawns, promotionPawnsCaptures, marchingPromotionPawns, capturePawns;
                if (turn == Color.WHITE) {
                    moveBonus = 8;
                    marchingPawns = bitBoard & ~(occupancies[Color.Both] >>> 8);
                    doublePushPawns = marchingPawns & BitBoard.SECOND_RANK & ~(occupancies[Color.Both] >>> 16);
                    marchingPromotionPawns = marchingPawns & BitBoard.SEVENTH_RANK;
                    marchingPawns &= ~BitBoard.SEVENTH_RANK;
                    promotionPawnsCaptures = bitBoard & BitBoard.SEVENTH_RANK;
                } else {
                    moveBonus = -8;
                    marchingPawns = bitBoard & (~occupancies[Color.Both] << 8);
                    doublePushPawns = marchingPawns & BitBoard.SEVENTH_RANK & ~(occupancies[Color.Both] << 16);
                    marchingPromotionPawns = marchingPawns & BitBoard.SECOND_RANK;
                    marchingPawns &= ~BitBoard.SECOND_RANK;
                    promotionPawnsCaptures = bitBoard & BitBoard.SECOND_RANK;
                }
                capturePawns = bitBoard & ~(marchingPromotionPawns | promotionPawnsCaptures);
                while (marchingPawns != 0) {
                    sourceSquare = BitBoard.getLSBIndex(marchingPawns);
                    marchingPawns ^= 1L << sourceSquare;
                    moveList.add(Move.encodeMove(sourceSquare, sourceSquare + moveBonus, pieceType, 0, 0, 0, 0, 0));
                }
                while (doublePushPawns != 0) {
                    sourceSquare = BitBoard.getLSBIndex(doublePushPawns);
                    doublePushPawns ^= 1L << sourceSquare;
                    moveList.add(Move.encodeMove(sourceSquare, sourceSquare + moveBonus * 2, pieceType, 0, 0, 1, 0, 0));
                }
                while (marchingPromotionPawns != 0) {
                    sourceSquare = BitBoard.getLSBIndex(marchingPromotionPawns);
                    marchingPromotionPawns ^= 1L << sourceSquare;
                    moveList.add(Move.encodeMove(sourceSquare, sourceSquare + moveBonus, pieceType, Piece.KNIGHT + turn * 6, 0, 0, 0, 0));
                    moveList.add(Move.encodeMove(sourceSquare, sourceSquare + moveBonus, pieceType, Piece.BISHOP + turn * 6, 0, 0, 0, 0));
                    moveList.add(Move.encodeMove(sourceSquare, sourceSquare + moveBonus, pieceType, Piece.ROOK + turn * 6, 0, 0, 0, 0));
                    moveList.add(Move.encodeMove(sourceSquare, sourceSquare + moveBonus, pieceType, Piece.QUEEN + turn * 6, 0, 0, 0, 0));
                }
                while (promotionPawnsCaptures != 0) {
                    sourceSquare = BitBoard.getLSBIndex(promotionPawnsCaptures);
                    promotionPawnsCaptures ^= 1L << sourceSquare;
                    attacks = Attacks.pawnsAttackTable[turn][sourceSquare] & occupancies[oppositeTurn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= 1L << targetSquare;
                        moveList.add(Move.encodeMove(sourceSquare, targetSquare, pieceType, Piece.KNIGHT + turn * 6, 1, 0, 0, 0));
                        moveList.add(Move.encodeMove(sourceSquare, targetSquare, pieceType, Piece.BISHOP + turn * 6, 1, 0, 0, 0));
                        moveList.add(Move.encodeMove(sourceSquare, targetSquare, pieceType, Piece.ROOK + turn * 6, 1, 0, 0, 0));
                        moveList.add(Move.encodeMove(sourceSquare, targetSquare, pieceType, Piece.QUEEN + turn * 6, 1, 0, 0, 0));
                    }
                }
                while (capturePawns != 0) {
                    sourceSquare = BitBoard.getLSBIndex(capturePawns);
                    capturePawns ^= 1L << sourceSquare;
                    attacks = Attacks.pawnsAttackTable[turn][sourceSquare] & occupancies[oppositeTurn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= 1L << targetSquare;
                        moveList.add(Move.encodeMove(sourceSquare, targetSquare, pieceType, 0, 1, 0, 0, 0));
                    }
                }
                if (State.isEnPassant(state)) {
                    int enpassantSquare = State.getEnPassantSquare(state);
                    long enpassantAttacks = Attacks.pawnsAttackTable[oppositeTurn][enpassantSquare] & bitBoard;
                    while (enpassantAttacks != 0) {
                        sourceSquare = BitBoard.getLSBIndex(enpassantAttacks);
                        enpassantAttacks ^= 1L << sourceSquare;
                        moveList.add(Move.encodeMove(sourceSquare, enpassantSquare, 0, 0, 1, 0, 1, 0));
                    }
                }
            } else if (pieceType % 6 == Piece.KING) {
                while (bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.kingsAttackTable[sourceSquare] & ~occupancies[turn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0, (int) ((occupancies[oppositeTurn] & (1L << targetSquare)) >>> targetSquare),0,0,0));
                    }
                }
                if (!isSquareAttacked(BitBoard.getLSBIndex(bitBoards[Piece.KING + 6 * turn]), oppositeTurn)) {
                    if (turn == Color.WHITE) {
                        if (State.canWhiteCastleKingSide(state) && (occupancies[Color.Both] & Squares.F1) == 0 && (occupancies[Color.Both] & Squares.G1) == 0) {
                            if (!isSquareAttacked(Squares.F1_INDEX, Color.BLACK) && !isSquareAttacked(Squares.G1_INDEX, Color.BLACK)) {
                                moveList.add(Move.encodeMove(Squares.E1_INDEX, Squares.G1_INDEX, pieceType, 0, 0, 0, 0, 1));
                            }
                        }
                        if (State.canWhiteCastleQueenSide(state) && (occupancies[Color.Both] & Squares.B1) == 0 && (occupancies[Color.Both] & Squares.C1) == 0
                                && (occupancies[Color.Both] & Squares.D1) == 0) {
                            if (!isSquareAttacked(Squares.C1_INDEX, Color.BLACK) && !isSquareAttacked(Squares.D1_INDEX, Color.BLACK)) {
                                moveList.add(Move.encodeMove(Squares.E1_INDEX, Squares.C1_INDEX, pieceType, 0, 0, 0, 0, 1));
                            }
                        }
                    }
                    if (turn == Color.BLACK) {
                        if (State.canBlackCastleKingSide(state) && (occupancies[Color.Both] & Squares.F8) == 0 && (occupancies[Color.Both] & Squares.G8) == 0) {
                            if (!isSquareAttacked(Squares.F8_INDEX, Color.WHITE) && !isSquareAttacked(Squares.G8_INDEX, Color.WHITE)) {
                                moveList.add(Move.encodeMove(Squares.E8_INDEX, Squares.G8_INDEX, pieceType, 0, 0, 0, 0, 1));
                            }
                        }
                        if (State.canBlackCastleQueenSide(state) && (occupancies[Color.Both] & Squares.B8) == 0 && (occupancies[Color.Both] & Squares.C8) == 0
                                && (occupancies[Color.Both] & Squares.D8) == 0) {
                            if (!isSquareAttacked(Squares.C8_INDEX, Color.WHITE) && !isSquareAttacked(Squares.D8_INDEX, Color.WHITE)) {
                                moveList.add(Move.encodeMove(Squares.E8_INDEX, Squares.C8_INDEX, pieceType, 0, 0, 0, 0, 1));
                            }
                        }
                    }
                }
            } else if (pieceType % 6 == Piece.KNIGHT) {
                while (bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.knightsAttackTable[sourceSquare] & ~occupancies[turn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0, (int) ((occupancies[oppositeTurn] & (1L << targetSquare)) >>> targetSquare),0,0,0));
                    }

                }
            } else if (pieceType % 6 == Piece.BISHOP) {
                while (bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.getBishopAttacks(sourceSquare, occupancies[Color.Both]) & ~occupancies[turn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0, (int) ((occupancies[oppositeTurn] & (1L << targetSquare)) >>> targetSquare),0,0,0));
                    }
                }
            } else if (pieceType % 6 == Piece.ROOK) {
                while (bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.getRookAttacks(sourceSquare, occupancies[Color.Both]) & ~occupancies[turn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0, (int) ((occupancies[oppositeTurn] & (1L << targetSquare)) >>> targetSquare),0,0,0));
                    }
                }
            } else if (pieceType % 6 == Piece.QUEEN) {
                while (bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.getQueenAttacks(sourceSquare, occupancies[Color.Both]) & ~occupancies[turn];
                    while (attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0, (int) ((occupancies[oppositeTurn] & (1L << targetSquare)) >>> targetSquare),0,0,0));
                    }
                }
            }
        }
        return moveList;
    }

    public ArrayList<Integer> generateCaptureMoves(){
        ArrayList<Integer> moveList = new ArrayList<>(70);
        int turn = State.getSideToMove(state);
        int sourceSquare, targetSquare;
        long bitBoard, attacks;
        for(int pieceType = 6 * turn; pieceType < 6 + 6 * turn ; pieceType++) {
            bitBoard = bitBoards[pieceType];
            if(pieceType % 6 == Piece.PAWN) {
                boolean isEnPassant = State.isEnPassant(state);
                while(bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    if(isEnPassant) {
                        long enpassantAttack = Attacks.pawnsAttackTable[turn][sourceSquare] & (1L << State.getEnPassantSquare(state));
                        if(enpassantAttack != 0) {
                            moveList.add(Move.encodeMove(sourceSquare,BitBoard.getLSBIndex(enpassantAttack),pieceType,0,1,0,1,0));
                        }
                    }

                    attacks = Attacks.pawnsAttackTable[turn][sourceSquare] & occupancies[turn^1];

                    while(attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= 1L << targetSquare;
                        if(turn == Color.WHITE && targetSquare >= 56 && targetSquare <= 63) {
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.QUEEN,1,0,0,0));
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.BISHOP,1,0,0,0));
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.ROOK,1,0,0,0));
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.KNIGHT,1,0,0,0));
                        }
                        else if(turn == Color.BLACK && targetSquare >= 0 && targetSquare <= 7) {
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.QUEEN + 6,1,0,0,0));
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.BISHOP + 6,1,0,0,0));
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.ROOK + 6,1,0,0,0));
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,Piece.KNIGHT + 6,1,0,0,0));
                        }
                        else
                            moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0,1,0,0,0));
                    }
                }
            }
            else if(pieceType % 6 == Piece.ROOK) {
                while(bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.getRookAttacks(sourceSquare, occupancies[Color.Both]) & occupancies[turn^1];
                    while(attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0,1,0,0,0));
                    }
                }
            }
            else if(pieceType % 6 == Piece.BISHOP) {
                while(bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.getBishopAttacks(sourceSquare, occupancies[Color.Both]) & occupancies[turn^1];
                    while(attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0,1,0,0,0));
                    }
                }
            }
            else if(pieceType % 6 == Piece.QUEEN) {
                while(bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.getQueenAttacks(sourceSquare, occupancies[Color.Both]) & occupancies[turn^1];
                    while(attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0,1,0,0,0));
                    }
                }
            }
            else if(pieceType % 6 == Piece.KNIGHT){
                while(bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.knightsAttackTable[sourceSquare] & occupancies[turn^1];
                    while(attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0,1,0,0,0));
                    }
                }
            }
            else {
                while(bitBoard != 0) {
                    sourceSquare = BitBoard.getLSBIndex(bitBoard);
                    bitBoard ^= (1L << sourceSquare);
                    attacks = Attacks.kingsAttackTable[sourceSquare] & occupancies[turn^1];
                    while(attacks != 0) {
                        targetSquare = BitBoard.getLSBIndex(attacks);
                        attacks ^= (1L << targetSquare);
                        moveList.add(Move.encodeMove(sourceSquare,targetSquare,pieceType,0,1,0,0,0));
                    }
                }
            }
        }
        return moveList;
    }

    public void printBoard(){
        String pieceTypesChars = "PNBRQKpnbrqk";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|");
        int row = 7, file = 0;
        while(row >= 0) {
            int square = row * 8 + file;
            file++;
            int type = -1;
            for(int pieceType = 0; pieceType < 12; pieceType++) {
                if(((bitBoards[pieceType]>>>square) & 1) == 1) {
                    type = pieceType;
                    break;
                }
            }
            if(type != -1)
                stringBuilder.append(pieceTypesChars.charAt(type) + "|");
            else
                stringBuilder.append(" |");
            if(file == 8) {
                System.out.println(stringBuilder);
                stringBuilder.setLength(0);
                stringBuilder.append("|");
                row--;
                file=0;
            }
        }
        if(State.getSideToMove(state) == 0)
            System.out.println("Turn: white");
        else
            System.out.println("Turn black");
        if(State.canWhiteCastleKingSide(state))
            System.out.println("White can castle king side");
        if(State.canWhiteCastleQueenSide(state))
            System.out.println("White can castle queen side");
        if(State.canBlackCastleKingSide(state))
            System.out.println("Black can castle king side");
        if(State.canBlackCastleQueenSide(state))
            System.out.println("Black can castle queen side");
        if(State.isEnPassant(state)) {
            System.out.println("En Passant location: " + Utils.indexToCoordinate(State.getEnPassantSquare(state)));
        }
    }

    public Board copy(){
        Board board = new Board();
        board.bitBoards = Arrays.copyOf(this.bitBoards,12);
        board.occupancies = Arrays.copyOf(this.occupancies,3);
        board.state = this.state;
        board.key = this.key;
        board.historyIndex = this.historyIndex;
        board.history = this.history;
        board.lastPawnCaptureMove = this.lastPawnCaptureMove;
        return board;
    }

    public boolean makeMove(int move) {

        int turn = State.getSideToMove(state);

        int sourceSquare = Move.getSourceSquare(move);
        int targetSquare = Move.getTargetSquare(move);
        int piece = Move.getPieceType(move);

        key ^= Zobrist.PIECE_KEYS[piece][sourceSquare] ^ Zobrist.PIECE_KEYS[piece][targetSquare];

        bitBoards[piece] ^= ((1L << sourceSquare) | (1L << targetSquare));
        occupancies[turn] ^= ((1L << sourceSquare) | (1L << targetSquare));

        if(Move.isCapture(move)) {
            long prevPawnBitBoard = bitBoards[6*turn];
            for(int i = 6 - 6*turn ; i < 12 - 6*turn ; i++) {
                key ^= Zobrist.PIECE_KEYS[i][targetSquare] * ((bitBoards[i] & (1L << targetSquare)) >>> targetSquare);
                bitBoards[i] &= ~(1L << targetSquare);
            }
            if(prevPawnBitBoard != bitBoards[6*turn])
                lastPawnCaptureMove = -1;
            occupancies[turn^1] &= ~(1L << targetSquare);
            if(Move.isEnPassant(move)) {
                key ^= Zobrist.PIECE_KEYS[6-6*turn][targetSquare + (-8 + 16 * turn)];
                bitBoards[6-6*turn] &= ~(1L << (targetSquare + (-8 + 16 * turn)));
                occupancies[turn^1] &= ~(1L << (targetSquare + (-8 + 16 * turn)));
            }
        }
        else if(Move.isCastling(move)) {
            if(targetSquare == 6) {
                bitBoards[3] ^= (Squares.H1 | Squares.F1);
                occupancies[0] ^= (Squares.H1 | Squares.F1);
                key ^= Zobrist.PIECE_KEYS[Piece.ROOK][7] ^ Zobrist.PIECE_KEYS[Piece.ROOK][5];
            }
            else if(targetSquare == 2) {
                bitBoards[3] ^= (Squares.A1 | Squares.D1);
                occupancies[0] ^= (Squares.A1 | Squares.D1);
                key ^= Zobrist.PIECE_KEYS[Piece.ROOK][0] ^ Zobrist.PIECE_KEYS[Piece.ROOK][3];
            }
            else if(targetSquare == 62) {
                bitBoards[9] ^= (Squares.H8 | Squares.F8);
                occupancies[1] ^= (Squares.H8 | Squares.F8);
                key ^= Zobrist.PIECE_KEYS[Piece.ROOK + 6][61] ^ Zobrist.PIECE_KEYS[Piece.ROOK + 6][63];
            }
            else {
                bitBoards[9] ^= (Squares.A8 | Squares.D8);
                occupancies[1] ^= (Squares.A8 | Squares.D8);
                key ^= Zobrist.PIECE_KEYS[Piece.ROOK + 6][56] ^ Zobrist.PIECE_KEYS[Piece.ROOK + 6][59];
            }
        }
        if(Move.isDoublePush(move)) {
            state |= State.isEnPassantMask;
            state &= ~State.enPassantSquareMask;
            state |= ((targetSquare + (-8 + 16 * turn)) << 2);
            key ^= Zobrist.ENPASSANT_KEYS[targetSquare + (-8 + 16 * turn)];
        }
        else {
            state &= ~State.isEnPassantMask;
        }

        if(Move.getPromotedPiece(move) != 0) {
            bitBoards[6*turn] ^= (1L << targetSquare);
            bitBoards[Move.getPromotedPiece(move)] ^= (1L << targetSquare);
            key ^= Zobrist.PIECE_KEYS[6*turn][targetSquare] ^ Zobrist.PIECE_KEYS[Move.getPromotedPiece(move)][targetSquare];
        }

        key ^= Zobrist.CASTLING_KEYS[State.getCastlingRights(state)];
        state &= State.CASTLING_RIGHTS[sourceSquare];
        state &= State.CASTLING_RIGHTS[targetSquare];
        key ^= Zobrist.CASTLING_KEYS[State.getCastlingRights(state)];

        occupancies[Color.Both] = occupancies[Color.WHITE] | occupancies[Color.BLACK];

        state ^= 1;
        key ^= Zobrist.SIDE_TO_MOVE_KEY;
        history[historyIndex++] = key;
        lastPawnCaptureMove++;
        return !isSquareAttacked(BitBoard.getLSBIndex(bitBoards[Piece.KING+6*turn]),turn^1);

    }

    public boolean isDraw(){
        if(lastPawnCaptureMove > 100)
            return true;
        int repetitions = 0;
        for(int i = historyIndex - 3; i > historyIndex - REPETITION_LOOK_BACK && i >= 0 ; i-=2){
            if(history[i] == key)
                repetitions++;
        }
        return repetitions >= 2;
    }

    public boolean equals(Board b) {
        for(int i = 0 ; i < 12 ; i++)
            if(bitBoards[i] != b.bitBoards[i])
                return false;
        for(int i = 0 ; i < 3 ; i++)
            if(occupancies[i] != b.occupancies[i])
                return false;
        return true;
    }

    public void changeSideToMove() {
        state ^= 1;
    }

    public void clearEnPassant() {
        state &= ~State.isEnPassantMask;
    }

    public void writeHistory(){
        history[historyIndex++] = key;
    }

    public long[] getHistory() {
        return history;
    }

    public boolean isLegalPosition() {
        int turn = State.getSideToMove(state);
        return !isSquareAttacked(BitBoard.getLSBIndex(bitBoards[Piece.KING + 6 * (turn^1)]), turn ) &&
                Long.bitCount(bitBoards[Piece.KING]) == 1 && Long.bitCount(bitBoards[Piece.B_KING]) == 1;
    }

    public boolean tryMakeUCIMove(String ANmove) {
        if(ANmove.length() > 5)
            return false;
        int promotePiece = 0;
        if(ANmove.length() == 5) {
            switch(ANmove.charAt(4)) {
                case 'q':
                    promotePiece = Piece.QUEEN;
                    break;
                case 'n':
                    promotePiece = Piece.KNIGHT;
                    break;
                case 'b':
                    promotePiece = Piece.BISHOP;
                    break;
                case 'r':
                    promotePiece = Piece.ROOK;
                    break;
                default:
                    return false;
            }
        }
        int sourceSquare, targetSquare;
        try {
            sourceSquare = Utils.chessCoordinateToIndex(ANmove.substring(0,2));
            targetSquare = Utils.chessCoordinateToIndex(ANmove.substring(2,4));
            ArrayList<Integer> moves = generateMoves();
            for(int move : moves)
                if(Move.getSourceSquare(move) == sourceSquare && Move.getTargetSquare(move) == targetSquare && Move.getPromotedPiece(move) % 6 == promotePiece)
                    return makeMove(move);
            return false;
        }catch(IllegalArgumentException e) {
            return false;
        }

    }

}
