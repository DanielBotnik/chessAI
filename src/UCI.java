package botnik.chess.server.chessai;

import javafx.util.Pair;

import java.awt.*;
import java.util.Locale;
import java.util.Scanner;

public class UCI {

    private static Scanner in = new Scanner(System.in);

    private Board board;

    public UCI() {
        board = new Board();
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";


    public void mainLoop() {
        String input;
        String[] tokens = null;
        while(!(input = in.nextLine()).equalsIgnoreCase("quit")) {
            tokens = input.split(" ");
            int length = tokens.length;
            if(length == 0) continue;
            if(tokens[0].equals("go")) {
                if(length == 1) {
                    // Evalute for ever
                }
                if(tokens[1].equals("perft")) {
                    if(length == 2) {
                        System.out.println(ANSI_RED + "Improper Use" + ANSI_RESET + "\ngo perft <depth> <flags>");
                    }
                    if(length >= 3) {
                        try {
                            int depth = Integer.parseInt(tokens[2]);
                            byte flags = 0;
                            boolean time = false;
                            for(int i = 3 ; i < tokens.length ; i++) {
                                switch(tokens[i]) {
                                    case "-a": case "-all":
                                        flags = Perft.ALL_FLAGS;
                                        break;
                                    case "-cap": case "-captures":
                                        flags |= Perft.CAPTURES;
                                        break;
                                    case "-en": case "-enpassant": case "-ep":
                                        flags |= Perft.EN_PASSANT;
                                        break;
                                    case "-p": case "-pro": case "-promotion":
                                        flags |= Perft.PROMOTION;
                                        break;
                                    case "-cas": case "-castling":
                                        flags |= Perft.CASTLING;
                                        break;
                                    case "-time": case "-t":
                                        time = true;
                                        break;
                                }
                            }
                            long currTime = System.currentTimeMillis();
                            Perft.prettyPrint(board,depth,flags);
                            if(time) {
                                System.out.println((System.currentTimeMillis() - currTime) + "ms");
                            }
                        } catch(NumberFormatException e){
                            System.out.println(ANSI_RED + "Error: Improper depth" + ANSI_RESET);
                        }

                    }
                }
                else if(tokens[1].equals("depth")) {
                    try {
                        int depth = Integer.parseInt(tokens[2]);
                        Search search = new Search(board);
                        search.search(depth);
                        System.out.print("Score: " + search.getBestScore() + "   Move: ");
                        Move.printMove(search.getBestMove());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(tokens[0].equals("position") || tokens[0].equals("pos")) {
                if(length == 1) {
                    System.out.println(ANSI_RED + "Improper use\n" + ANSI_RESET + "position fen <fen key>");
                }
                else {
                    if(tokens[1].equals("startpos") || tokens[1].equals("start")) {
                        board = new Board();
                    }
                    int i = 2;
                    if(tokens[1].equals("fen")) {
                        StringBuilder stringBuilder = new StringBuilder(100);
                        for(; i < tokens.length && !tokens[i].equals("moves") ; i++)
                            stringBuilder.append(tokens[i] + " ");
                        Pair<Board,Boolean> result = Fen.tryMakeBoard(stringBuilder.toString().trim());
                        board = result.getKey();
                        if(!result.getValue()) {
                            System.out.println("Shit input, normal board it is");
                        }
                    }
                    if(i < tokens.length && !tokens[i].equals("moves"))
                        System.out.println("Invalid word something");
                    i++;
                    for(; i < tokens.length ; i++) {
                        if (!board.tryMakeUCIMove(tokens[i])) {
                            System.out.println("Shit input,normal board it is");
                            board = new Board();
                        }
                    }
                }
            }
            else if(tokens[0].equals("board") || tokens[0].equals("view") || tokens[0].equals("d")) {
                board.printBoard();
            }
        }
    }

}
