package botnik.chess.server.chessai;// Starting Position rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
// another r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1

import java.util.ArrayList;
import java.util.Comparator;

public class Program {
    public static void main(String[] args) {
        UCI uci = new UCI();
        uci.mainLoop();
    }
}