package botnik.chess.server.chessai;

public class AttacksInfo {

    public int[] numberOfPossibleMoves;

    public AttacksInfo(Board board) {
        numberOfPossibleMoves = new int[64];
        for(int move : board.generateMoves()) {
            if(board.copy().makeMove(move)) {
                numberOfPossibleMoves[Move.getSourceSquare(move)]++;
            }
        }
    }

}
