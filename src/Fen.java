package botnik.chess.server.chessai;

import com.sun.org.apache.bcel.internal.generic.ILOAD;
import javafx.util.Pair;

public class Fen {

    public static int FENCharToBitBoardIndex(char c) {
        switch (c) {
            case 'P':
                return 0;
            case 'N':
                return 1;
            case 'B':
                return 2;
            case 'R':
                return 3;
            case 'Q':
                return 4;
            case 'K':
                return 5;
            case 'p':
                return 6;
            case 'n':
                return 7;
            case 'b':
                return 8;
            case 'r':
                return 9;
            case 'q':
                return 10;
            case 'k':
                return 11;
        }
        throw new IllegalArgumentException("Invalid FEN Char: " + c);
    }

    public static Pair<Board,Boolean> tryMakeBoard(String fen){
        if(fen.length() == 0)
            return new Pair<>(new Board(),false);
        String[] segments = fen.split(" ");
        String finalFen = String.join(" ",segments);
        Board board = null;
        try {
            switch(segments.length) {
                case 1: {
                    board = new Board(finalFen + " w - - 0 1");
                    if(!board.isLegalPosition())
                        board = new Board(finalFen + " b - - 0 1");
                }
                case 2: {
                    board = new Board(finalFen + " - - 0 1");
                }
                case 3: {
                    board = new Board(finalFen + " - 0 1");
                }
                case 4: {
                    board = new Board(finalFen + " 0 1");
                }
            }
        }catch(IllegalArgumentException e) {
            return new Pair<>(new Board(),false);
        } finally {
            if(board.isLegalPosition())
                return new Pair<>(board,true);
            return new Pair<>(new Board(),false);
        }
    }

}
