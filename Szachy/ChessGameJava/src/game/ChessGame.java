package game;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.File;
import pieces.Pawn;
import pieces.King;

/**
 * Obsługuje interakcję z użytkownikiem,
 * rejestruje ruchy w notacji algebraicznej oraz zapisuje partię do pliku.
 */

public class ChessGame {
    private Board board;
    private boolean whiteTurn;
    // Lista zapisanych ruchów
    private List<String> pgnMoves = new ArrayList<>();
    // Zmienna do zapamiętania wyniku partii
    private String gameResult = "*";

    public ChessGame() {
        board = new Board();
        whiteTurn = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUdanej partii!\n\n INSTRUKCJA: \n Ruch w formacie (np. e2e4 / e2 jest aktualną pozycją pionka, e4 jest finalną \n Roszada krótka O-O \n Roszada długa O-O-O \n exit wyłącza grę\n");

        while (true) {
            board.printBoard();
            System.out.println((whiteTurn ? "Białego" : "Czarnego") + " ruch. Wprowadź swój ruch (np. e2e4 lub O-O dla roszady):");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Gra wyłączona.");
                break;
            }

            try {
                // Pobieramy informacje o ruchu
                Move move = new Move(input, whiteTurn);

                int startY = move.getStartY();
                int startX = move.getStartX();
                int endY = move.getEndY();
                int endX = move.getEndX();

                var movingPiece = board.getPieceAt(startY, startX);
                var capturedPiece = board.getPieceAt(endY, endX);

                boolean isEnPassant = false;
                if (movingPiece instanceof Pawn && startX != endX && capturedPiece == null) {
                    isEnPassant = true;
                }
                board.move(move);

                boolean opponentCheck = board.isCheck(!whiteTurn);
                boolean opponentCheckmate = board.isCheckmate(!whiteTurn);
                boolean opponentStalemate = board.isStalemate(!whiteTurn);

                // Konwertujemy ruch na notację algebraiczną
                String algebraicMove = convertMoveToAlgebraic(move, movingPiece, capturedPiece, isEnPassant, opponentCheck, opponentCheckmate);
                pgnMoves.add(algebraicMove);

                // zakończenie partii
                if (opponentCheckmate) {
                    System.out.println("SZACHMAT! " + (whiteTurn ? "White" : "Black") + " Wygrywa!");
                    gameResult = whiteTurn ? "1-0" : "0-1";
                    board.printBoard();
                    break;
                } else if (opponentStalemate) {
                    System.out.println("PAT! Remis");
                    gameResult = "1/2-1/2";
                    board.printBoard();
                    break;
                } else if (opponentCheck) {
                    System.out.println("SZACH!");
                }
                whiteTurn = !whiteTurn;
            } catch (IllegalArgumentException e) {
                System.out.println("Niepoprawny ruch: " + e.getMessage());
            }
        }
        saveGameToFile();
        scanner.close();
    }

    private String convertMoveToAlgebraic(Move move, Object movingPiece, Object capturedPiece,
                                          boolean isEnPassant, boolean check, boolean checkmate) {
        if (move.isCastling()) {
            String notation = move.isKingside() ? "O-O" : "O-O-O";
            return notation + (checkmate ? "#" : (check ? "+" : ""));
        }

        StringBuilder sb = new StringBuilder();
        if (!(movingPiece instanceof Pawn)) {
            if (movingPiece instanceof King) {
                sb.append("K");
            } else if (movingPiece.getClass().getSimpleName().equals("Queen")) {
                sb.append("Q");
            } else if (movingPiece.getClass().getSimpleName().equals("Rook")) {
                sb.append("R");
            } else if (movingPiece.getClass().getSimpleName().equals("Bishop")) {
                sb.append("B");
            } else if (movingPiece.getClass().getSimpleName().equals("Knight")) {
                sb.append("N");
            }
        } else {
            if (capturedPiece != null || (isEnPassant && move.getStartX() != move.getEndX())) {
                char file = (char) ('a' + move.getStartX());
                sb.append(file);
            }
        }
        if (capturedPiece != null || (isEnPassant && move.getStartX() != move.getEndX())) {
            sb.append("x");
        }
        char destFile = (char) ('a' + move.getEndX());
        int destRank = 8 - move.getEndY();
        sb.append(destFile).append(destRank);
        if (checkmate) {
            sb.append("#");
        } else if (check) {
            sb.append("+");
        }
        return sb.toString();
    }

    private void saveGameToFile() {
        StringBuilder pgn = new StringBuilder();
        int moveNum = 1;
        for (int i = 0; i < pgnMoves.size(); i++) {
            if (i % 2 == 0) {
                pgn.append(moveNum).append(". ");
                moveNum++;
            }
            pgn.append(pgnMoves.get(i)).append(" ");
        }
        pgn.append(gameResult);

        try (PrintWriter writer = new PrintWriter(new File("game.pgn"))) {
            writer.println(pgn.toString());
            System.out.println("Partia zapisana do pliku game.pgn");
        } catch (Exception e) {
            System.out.println("Błąd przy zapisie partii do pliku: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.start();
    }
}
