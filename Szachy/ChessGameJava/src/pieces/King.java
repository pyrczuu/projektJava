package pieces;

public class King extends Piece {

    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }

        // Nie może zbić własnej figury
        Piece target = board[endRow][endCol];
        if (target != null && target.isWhite() == this.isWhite()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return isWhite() ? "1K" : "2k";
    }
}
