package game;

import pieces.*;

/**
 * Klasa zawiera szachową planszę i logikę:
 *   inicjalizacja figur
 *   wykonywanie ruchów
 *   sprawdzanie szach, mat, pat
 *   niedopuszczanie do ruchów, które wystawiają/pozostawiają króla w szachu
 */

public class Board {
    private final Piece[][] board;
    private int enPassantRow = -1;
    private int enPassantCol = -1;

    //Inicjalizacja planszy
    public Board() {
        board = new Piece[8][8];
        initializePieces();
    }

    /**
     * Zwraca figurę znajdującą się na zadanej pozycji planszy
     * @param row Numer wiersza (0-7)
     * @param col Numer kolumny (0-7)
     */

    public Piece getPieceAt(int row, int col) {
        return board[row][col];
    }


    //Symulacja ruchów
    public Board(Piece[][] otherBoard) {
        board = new Piece[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[y][x] = otherBoard[y][x];
            }
        }
    }

    private void initializePieces() {
        // Czarne figury
        for (int x = 0; x < 8; x++) {
            board[1][x] = new Pawn(false);
        }
        board[0][0] = new Rook(false);
        board[0][1] = new Knight(false);
        board[0][2] = new Bishop(false);
        board[0][3] = new Queen(false);
        board[0][4] = new King(false);
        board[0][5] = new Bishop(false);
        board[0][6] = new Knight(false);
        board[0][7] = new Rook(false);

        // Białe figury
        for (int x = 0; x < 8; x++) {
            board[6][x] = new Pawn(true);
        }
        board[7][0] = new Rook(true);
        board[7][1] = new Knight(true);
        board[7][2] = new Bishop(true);
        board[7][3] = new Queen(true);
        board[7][4] = new King(true);
        board[7][5] = new Bishop(true);
        board[7][6] = new Knight(true);
        board[7][7] = new Rook(true);
    }

    public void printBoard() {
        System.out.println("    A    B    C    D    E    F    G    H");
        for (int y = 0; y < 8; y++) {
            System.out.print((8 - y));
            for (int x = 0; x < 8; x++) {
                System.out.print(" | ");
                if (board[y][x] == null) {
                    System.out.print(" .");
                } else {
                    System.out.print(board[y][x]);
                }
            }
            System.out.println(" | " + (8 - y));
        }
        System.out.println("    A    B    C    D    E    F    G    H");
    }

    /**
     * Metoda sprawdza poprawność ruchu
     * @param move Obiekt reprezentujący ruch.
     */

    public void move(Move move) {
        // Pobierz figurę z pozycji startowej
        Piece piece = board[move.getStartY()][move.getStartX()];
        if (piece == null) {
            throw new IllegalArgumentException("Brak pionka do ruchu");
        }
        if (piece.isWhite() != move.isWhiteTurn()) {
            throw new IllegalArgumentException("Nie twoja tura");
        }
        // Obsługa roszady
        if (move.isCastling()) {
            performCastling(move);
            return;
        }

        // Sprawdzanie ruchu en passant
        boolean isEnPassant = false;
        if (piece instanceof Pawn) {
            int startC = move.getStartX();
            int endR = move.getEndY();
            int endC = move.getEndX();

            if (startC != endC && board[endR][endC] == null) {
                if (endR == enPassantRow && endC == enPassantCol) {
                    isEnPassant = true;
                }
            }
        }

        // Obsługa bicia en passant
        if (isEnPassant) {
            if (piece.isWhite()) {
                board[move.getEndY() + 1][move.getEndX()] = null;
            } else {
                board[move.getEndY() - 1][move.getEndX()] = null;
            }
        } else {
            if (!piece.isValidMove(move.getStartY(), move.getStartX(),
                    move.getEndY(), move.getEndX(), board)) {
                throw new IllegalArgumentException("Niepoprawny ruch dla tego pionka");
            }
            // Sprawdzenie, czy na docelowym polu nie stoi figura tego samego koloru
            Piece targetPiece = board[move.getEndY()][move.getEndX()];
            if (targetPiece != null && targetPiece.isWhite() == move.isWhiteTurn()) {
                throw new IllegalArgumentException("Nie możesz zbić swojej figury");
            }
        }
        int oldEnPassantRow = enPassantRow;
        int oldEnPassantCol = enPassantCol;
        enPassantRow = -1;
        enPassantCol = -1;

        if (piece instanceof Pawn) {
            int startRow = move.getStartY();
            int endRow = move.getEndY();
            int startCol = move.getStartX();
            int moveDist = Math.abs(endRow - startRow);
            if (moveDist == 2) {
                enPassantRow = (startRow + endRow) / 2;
                enPassantCol = startCol;
            }
        }

        // Sprawdzenie za pomocą symulacji, czy król będzie w szachu
        Piece[][] tempBoard = copyBoard();
        tempBoard[move.getEndY()][move.getEndX()] = tempBoard[move.getStartY()][move.getStartX()];
        tempBoard[move.getStartY()][move.getStartX()] = null;
        if (isEnPassant) {
            if (piece.isWhite()) {
                tempBoard[move.getEndY() + 1][move.getEndX()] = null;
            } else {
                tempBoard[move.getEndY() - 1][move.getEndX()] = null;
            }
        }
        Board temp = new Board(tempBoard);
        if (temp.isCheck(piece.isWhite())) {
            // Cofnięcie, gdy ruch wystawia króla w szachu
            if (isEnPassant) {
                if (piece.isWhite()) {
                    board[move.getEndY() + 1][move.getEndX()] = new Pawn(false);
                } else {
                    board[move.getEndY() - 1][move.getEndX()] = new Pawn(true);
                }
            }
            enPassantRow = oldEnPassantRow;
            enPassantCol = oldEnPassantCol;
            throw new IllegalArgumentException("Nielegalny ruch: twój król jest lub pozostaje w szachu");
        }

        // Wykonanie ruchu na planszy
        board[move.getEndY()][move.getEndX()] = piece;
        board[move.getStartY()][move.getStartX()] = null;
        if (piece instanceof King) {
            ((King) piece).setHasMoved(true);
        } else if (piece instanceof Rook) {
            ((Rook) piece).setHasMoved(true);
        }
    }

    private void performCastling(Move move) {
        King king = (King) board[move.getStartY()][move.getStartX()];
        boolean kingside = move.isKingside();

        if (kingside) {
            // Sprawdzanie warunków dla roszady krótszej
            if (king.hasMoved()) {
                throw new IllegalArgumentException("Brak możliwości roszady: król się poruszył");
            }
            Piece rook = board[move.getStartY()][7];
            if (!(rook instanceof Rook)) {
                throw new IllegalArgumentException("Brak możliwości roszady: Nie ma wieży");
            }
            Rook kingsideRook = (Rook) rook;
            if (kingsideRook.hasMoved()) {
                throw new IllegalArgumentException("Brak możliwości roszady: Wieża się poruszyła");
            }
            // Sprawdzenie, czy pola pomiędzy królem a wieżą są wolne
            for (int col = move.getStartX() + 1; col < 7; col++) {
                if (board[move.getStartY()][col] != null) {
                    throw new IllegalArgumentException("Brak możliwości roszady: Ścieżka jest pod atakiem");
                }
            }
            // Król nie może być w szachu oraz nie może przechodzić przez atakowane pola
            if (isCheck(king.isWhite())) {
                throw new IllegalArgumentException("Brak możliwości roszady: Król jest w szachu");
            }
            for (int col = move.getStartX(); col <= move.getEndX(); col++) {
                Board tempBoard = new Board(this.board);
                tempBoard.movePiece(move.getStartY(), move.getStartX(), move.getStartY(), col);
                if (tempBoard.isCheck(king.isWhite())) {
                    throw new IllegalArgumentException("Brak możliwości roszady: Ścieżka jest pod atakiem");
                }
            }
            // Wykonanie roszady krótszej
            board[move.getEndY()][move.getEndX()] = king;
            board[move.getStartY()][move.getStartX()] = null;
            board[move.getStartY()][5] = rook;
            board[move.getStartY()][7] = null;

            king.setHasMoved(true);
            kingsideRook.setHasMoved(true);
        } else {
            if (king.hasMoved()) {
                throw new IllegalArgumentException("Brak możliwości roszady: Król się poruszył");
            }
            Piece rook = board[move.getStartY()][0];
            if (!(rook instanceof Rook)) {
                throw new IllegalArgumentException("Brak możliwości roszady: Nie ma wieży");
            }
            Rook queensideRook = (Rook) rook;
            if (queensideRook.hasMoved()) {
                throw new IllegalArgumentException("Brak możliwości roszady: Wieża się już poruszyła");
            }
            // Sprawdzenie, czy pola między królem a wieżą są wolne
            for (int col = move.getEndX() + 1; col < move.getStartX(); col++) {
                if (board[move.getStartY()][col] != null) {
                    throw new IllegalArgumentException("Brak możliwości roszady: Brak miejsca dla roszady");
                }
            }
            if (isCheck(king.isWhite())) {
                throw new IllegalArgumentException("Brak możliwości roszady: Król jest w szachu");
            }
            for (int col = move.getStartX(); col >= move.getEndX(); col--) {
                Board tempBoard = new Board(this.board);
                tempBoard.movePiece(move.getStartY(), move.getStartX(), move.getStartY(), col);
                if (tempBoard.isCheck(king.isWhite())) {
                    throw new IllegalArgumentException("Brak możliwości roszady: Ścieżka jest pod atakiem");
                }
            }
            if (board[move.getEndY()][move.getEndX()] != null) {
                throw new IllegalArgumentException("Brak możliwości roszady: Brak miejsca dla roszady");
            }
            board[move.getEndY()][move.getEndX()] = king;
            board[move.getStartY()][move.getStartX()] = null;
            board[move.getStartY()][3] = rook;
            board[move.getStartY()][0] = null;

            king.setHasMoved(true);
            queensideRook.setHasMoved(true);
        }
    }

    //Pomocnicza metoda do przenoszenia figury na kopii planszy
    private void movePiece(int startY, int startX, int endY, int endX) {
        Piece piece = board[startY][startX];
        board[endY][endX] = piece;
        board[startY][startX] = null;
    }

    public int[] findKingPosition(boolean isWhite) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board[y][x];
                if (p != null && p instanceof King && p.isWhite() == isWhite) {
                    return new int[]{y, x};
                }
            }
        }
        return null;
    }

    //Sprawdzenie, czy król znajduje się w szachu
    public boolean isCheck(boolean isWhite) {
        int[] kingPos = findKingPosition(isWhite);
        if (kingPos == null) {
            return false;
        }
        int kingY = kingPos[0];
        int kingX = kingPos[1];

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board[y][x];
                if (p != null && p.isWhite() != isWhite) {
                    if (p.isValidMove(y, x, kingY, kingX, board)) {
                        return true; // Król jest atakowany
                    }
                }
            }
        }
        return false;
    }

    //Sprawdzenie, czy król znajduje się w sytuacji mata
    public boolean isCheckmate(boolean isWhite) {
        if (!isCheck(isWhite)) {
            return false;
        }
        for (int startY = 0; startY < 8; startY++) {
            for (int startX = 0; startX < 8; startX++) {
                Piece piece = board[startY][startX];
                if (piece != null && piece.isWhite() == isWhite) {
                    for (int endY = 0; endY < 8; endY++) {
                        for (int endX = 0; endX < 8; endX++) {
                            if (endY == startY && endX == startX) {
                                continue;
                            }
                            if (piece.isValidMove(startY, startX, endY, endX, board)) {
                                try {
                                    Piece[][] tempBoard = copyBoard();
                                    tempBoard[endY][endX] = tempBoard[startY][startX];
                                    tempBoard[startY][startX] = null;
                                    if (piece instanceof Pawn && startX != endX && tempBoard[endY][endX] == null) {
                                        if (piece.isWhite()) {
                                            tempBoard[endY + 1][endX] = null;
                                        } else {
                                            tempBoard[endY - 1][endX] = null;
                                        }
                                    }
                                    Board temp = new Board(tempBoard);
                                    if (!temp.isCheck(isWhite)) {
                                        return false;
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    //Sprawdzenie remisu
    public boolean isStalemate(boolean isWhite) {
        if (isCheck(isWhite)) {
            return false;
        }
        for (int startY = 0; startY < 8; startY++) {
            for (int startX = 0; startX < 8; startX++) {
                Piece piece = board[startY][startX];
                if (piece != null && piece.isWhite() == isWhite) {
                    for (int endY = 0; endY < 8; endY++) {
                        for (int endX = 0; endX < 8; endX++) {
                            if (startY == endY && startX == endX) continue;
                            if (piece.isValidMove(startY, startX, endY, endX, board)) {
                                try {
                                    Piece[][] tempBoard = copyBoard();
                                    tempBoard[endY][endX] = tempBoard[startY][startX];
                                    tempBoard[startY][startX] = null;
                                    if (piece instanceof Pawn && startX != endX && tempBoard[endY][endX] == null) {
                                        if (piece.isWhite()) {
                                            tempBoard[endY + 1][endX] = null;
                                        } else {
                                            tempBoard[endY - 1][endX] = null;
                                        }
                                    }
                                    Board temp = new Board(tempBoard);
                                    if (!temp.isCheck(isWhite)) {
                                        return false;
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    //Kopia planszy
    private Piece[][] copyBoard() {
        Piece[][] temp = new Piece[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                temp[y][x] = board[y][x];
            }
        }
        return temp;
    }
}
