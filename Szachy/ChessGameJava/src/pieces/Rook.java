package pieces;

public class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        if (startRow != endRow && startCol != endCol) {
            return false;
        }

        if (!isPathClear(startRow, startCol, endRow, endCol, board)) {
            return false;
        }

        Piece target = board[endRow][endCol];
        if (target != null && target.isWhite() == this.isWhite()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return isWhite() ? "1R" : "2r";
    }
}
