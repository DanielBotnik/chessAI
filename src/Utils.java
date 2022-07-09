package botnik.chess.server.chessai;

import com.sun.javaws.exceptions.InvalidArgumentException;

public class Utils {

    public static String indexToCoordinate(long index) {
        return (char)('a' + index % 8) + "" +  (index / 8 + 1);
    }

    public static int chessCoordinateToIndex(String coordinate) {
        if(coordinate.length() != 2 || coordinate.charAt(0) < 'a' || coordinate.charAt(0) > 'h' ||
                coordinate.charAt(1) < '1' || coordinate.charAt(1) > '8')
            throw new IllegalArgumentException("Invalid FEN String: ");
        int file = coordinate.charAt(0) - 'a';
        int rank = Character.getNumericValue(coordinate.charAt(1));
        return (rank-1) * 8 + file;
    }
}
