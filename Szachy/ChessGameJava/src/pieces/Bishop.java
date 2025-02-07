package pieces;

public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        if (rowDiff != colDiff) {
            return false;
        }
        if (!isPathClear(startRow, startCol, endRow, endCol, board)) {
            return false;
        }

        Piece target = board[endRow][endCol];
        return target == null || target.isWhite() != this.isWhite();
    }

    @Override
    public String toString() {
        return isWhite() ? "1B" : "2b";
    }
}