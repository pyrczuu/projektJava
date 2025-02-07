package pieces;

public class Knight extends Piece {

    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        // Skoczek w "L": (2,1) lub (1,2)
        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            Piece target = board[endRow][endCol];
            return target == null || target.isWhite() != this.isWhite();
        }
        return false;
    }

    @Override
    public String toString() {
        return isWhite() ? "1N" : "2n";
    }
}