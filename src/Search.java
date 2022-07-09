package botnik.chess.server.chessai;

import java.util.ArrayList;

public class Search {

    private static final int[][] MVV_LVA_TABLE = {
            {125, 381, 500, 537, 543, 600, 125, 381, 500, 537, 543, 600},
            {2006,1950,1943,1906,1787,1531,2006,1950,1943,1906,1787,1531},
            {1687,1943,2062,2100,2106,2162,1687,1943,2062,2100,2106,2162},
            {2625,2881,3000,3037,3043,3100,2625,2881,3000,3037,3043,3100},
            {5593,5850,5968,6006,6012,6068,5593,5850,5968,6006,6012,6068},
            {12000,12256,12375,12412,12418,12475,12000,12256,12375,12412,12418,12475},
            {125, 381, 500, 537, 543, 600, 125, 381, 500, 537, 543, 600},
            {2006,1950,1943,1906,1787,1531,2006,1950,1943,1906,1787,1531},
            {1687,1943,2062,2100,2106,2162,1687,1943,2062,2100,2106,2162},
            {2625,2881,3000,3037,3043,3100,2625,2881,3000,3037,3043,3100},
            {5593,5850,5968,6006,6012,6068,5593,5850,5968,6006,6012,6068},
            {12000,12256,12375,12412,12418,12475,12000,12256,12375,12412,12418,12475},
    };


    private static final int MAX_DEPTH = 64;
    private static final int ALPHA = -50000;
    private static final int BETA = 50000;
    private static final int NULL_PRUNING_DEPTH = 2;
    private static final int FULL_DEPTH_MOVES = 4;
    private static final int REDUCTION_LIMIT = 3;
    private static final int START_DEPTH = 2;

    private static final int CHECKMATE_VALUE = 45000;
    private static final int CHECKMATE_SCORE = 44000;
    private static final int STALEMATE = 0;

    private static final int NODES_TO_COUNT_TIME = 3000;

    private Board board;
    private TranspositionTable tt;

    private int[][] historyMoves;
    private int[][] killers;
    private int[][] pvTable;

    private int bestMoveIteration;
    private int bestScoreIteration;

    private int bestMove;
    private int bestScore;

    private long time;
    private boolean stop;
    private int nodes;

    private long startTimer;

    private boolean followPv;
    private boolean scorePv;

    public Search(Board position) {
        this.board = position.copy();
        tt = new TranspositionTable();
        historyMoves = new int[64][64];
        killers = new int[2][MAX_DEPTH];
        pvTable = new int[MAX_DEPTH][MAX_DEPTH];
    }

    public int getBestMove() {
        return bestMove ;
    }

    public int getBestScore() {
        return bestScore;
    }

    public int search(int depth,long time) {
        nodes = 0;
        stop = false;
        followPv = false;
        scorePv = false;
        int alpha = ALPHA;
        int beta = BETA;
        this.time = time;
        startTimer = System.currentTimeMillis();

        for(int currentDepth = START_DEPTH; currentDepth <= depth ; currentDepth++) {
            if(stop)
                break;
            tt.clear();
            followPv = true;
            _search(currentDepth,alpha,beta,0);
            bestScore = bestScoreIteration;
            bestMove = bestMoveIteration;
            if(bestScore > CHECKMATE_SCORE || bestScore < -CHECKMATE_SCORE) {
                int i = 0;
                while(pvTable[0][i++] != 0 && i < MAX_DEPTH) ;
                pvTable[0][i-2] = 0;
                break;
            }
        }
        return pvTable[0][0];
    }

    public int search(int depth) {
        return search(depth,Long.MAX_VALUE);
    }

    public int search(long time){
        return search(MAX_DEPTH,Long.MAX_VALUE);
    }

    private int _search(int depth,int alpha,int beta,int ply) {
        if(stop)
            return 0;
        if(nodes++ == NODES_TO_COUNT_TIME) {
            handleTime();
        }
        if(board.isDraw() && ply != 0) {
            return STALEMATE;
        }
        int entry = tt.getEvaluation(board.getKey(),alpha,beta,depth);
        if(entry != Entry.EMPTY_ENTRY)
            return Entry.getScore(entry);
        int flag = TranspositionTable.ALPHA;
        if(depth == 0)
            return quiescenceSearch(alpha,beta);
        if(ply > MAX_DEPTH)
            return Evaluation.evaluate(board);
        int turn = board.getSideToMove();
        boolean inCheck = board.isSquareAttacked(BitBoard.getLSBIndex(board.getBitBoards()[Piece.KING+6*turn]),turn^1);
        if(depth > NULL_PRUNING_DEPTH && !inCheck && ply != 0) {
            Board copy = board.copy();
            board.writeHistory();
            board.changeSideToMove();
            board.clearEnPassant();
            int score = -_search(depth - 1 - NULL_PRUNING_DEPTH,-beta,-beta + 1,ply + 1);
            this.board = copy;
            if(score >= beta)
                return beta;
        }
        ArrayList<Integer> moves = board.generateMoves();
        if(followPv)
            enablePvScoring(moves,ply);
        moves.sort((o1, o2) -> moveScore(o2,ply) - moveScore(o1,ply));
        boolean foundLegalMove = false;
        int movesSearched = 0;
        for(int move : moves) {
            Board copy = board.copy();
            if(board.makeMove(move)) {
                int score;
                foundLegalMove = true;
                if(movesSearched == 0) {
                    score = -_search(depth - 1, -beta, -alpha, ply + 1);
                }
                else {
                    if(movesSearched >= FULL_DEPTH_MOVES && depth >= REDUCTION_LIMIT && !inCheck && !Move.isCapture(move) && !Move.isPromotion(move)) {
                        score = -_search(depth - 2,-alpha - 1,-alpha,ply+1);
                    }
                    else score = alpha + 1;
                    if(score > alpha) {
                        score = -_search(depth-1,-alpha -1,-alpha,ply+1);
                        if(score > alpha && score < beta)
                            score = -_search(depth-1,-beta,-alpha,ply+1);
                    }
                }

                if(score >= beta) {
                    if(!Move.isCapture(move)) {
                        killers[1][ply] = killers[0][ply];
                        killers[0][ply] = move;
                    }
                    tt.storeEvaluation(board.getKey(),beta,depth,TranspositionTable.BETA);
                    this.board = copy;
                    return beta;
                }
                if(score > alpha) {
                    alpha = score;
                    flag = TranspositionTable.EXACT;
                    if(ply == 0) {
                        bestMoveIteration = move;
                        bestScoreIteration = score;
                    }
                    if(!Move.isCapture(move)) {
                        historyMoves[Move.getSourceSquare(move)][Move.getTargetSquare(move)] += depth;
                    }
                    pvTable[ply][ply] = move;
                    for(int nextPly = ply + 1 ; pvTable[ply+1][nextPly] != 0 && nextPly < MAX_DEPTH; nextPly++)
                        pvTable[ply][nextPly] = pvTable[ply+1][nextPly];
                }
            }
            this.board = copy;
        }
        if(!foundLegalMove) {
            if(inCheck) {
                return -CHECKMATE_VALUE + ply;
            }
            else
                return STALEMATE;
        }
        tt.storeEvaluation(board.getKey(),alpha,depth,flag);
        return alpha;
    }

    private int quiescenceSearch(int alpha,int beta) {
        if(stop)
            return 0;
        if(nodes++ == NODES_TO_COUNT_TIME) {
            handleTime();
        }
        int evaluation = Evaluation.evaluate(board);
        if(evaluation >= beta)
            return beta;
        if(evaluation > alpha)
            alpha = evaluation;
        ArrayList<Integer> moves = board.generateCaptureMoves();
        for(int move : moves) {
            Board copy = board.copy();
            if(board.makeMove(move)) {
                int score = -quiescenceSearch(-beta,-alpha);
                if(score >= beta)
                    return beta;
                if(score > alpha)
                    alpha = score;
            }
            this.board = copy;
        }
        return alpha;
    }

    private int moveScore(int move,int ply) {
        if(scorePv) {
            if(pvTable[0][ply] == move) {
                scorePv = false;
                return 30000;
            }
        }
        if(Move.isCapture(move)) {
            int side = board.getSideToMove();
            long[] bitboards = board.getBitBoards();
            long targetSquare = 1L << Move.getTargetSquare(move);
            for(int piece = 6 * side ; piece < side * 6 + 6 ; piece++)
                if((bitboards[piece] & targetSquare) == 1)
                    return MVV_LVA_TABLE[Move.getPieceType(move)][piece] + 10000;
        }
        if(Move.isPromotion(move))
            return 9000;
        if(killers[0][ply] == move)
            return 8000;
        if(killers[1][ply] == move)
            return 7000;
        int historyMove = historyMoves[Move.getSourceSquare(move)][Move.getTargetSquare(move)];
        if(historyMove != 0)
            return historyMove;
        return 0;
    }

    private void enablePvScoring(ArrayList<Integer> moves,int ply) {
        followPv = false;
        for(int move : moves) {
            if(pvTable[0][ply] == move) {
                scorePv = true;
                followPv = true;
            }
        }
    }

    public ArrayList<Integer> getPvMoves(){
        ArrayList<Integer> pvMoves = new ArrayList<>();
        int i = 0;
        while(pvTable[0][i] != 0)
            pvMoves.add(pvTable[0][i++]);
        return pvMoves;
    }

    private void handleTime() {
        nodes = 0;
        long currentTime = System.currentTimeMillis();
        time -= (currentTime - startTimer);
        startTimer = currentTime;
        stop = time < 0;
    }

}


