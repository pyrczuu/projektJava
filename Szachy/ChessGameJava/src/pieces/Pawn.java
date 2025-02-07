package pieces;

public class Pawn extends Piece {

    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        int direction = isWhite() ? -1 : 1;

        if (endRow == startRow + direction && endCol == startCol) {
            if (board[endRow][endCol] == null) {
                return true;
            }
        }

        if ((isWhite() && startRow == 6) || (!isWhite() && startRow == 1)) {
            if (endRow == startRow + 2 * direction && endCol == startCol) {
                if (board[startRow + direction][startCol] == null && board[endRow][endCol] == null) {
                    return true;
                }
            }
        }

        if (endRow == startRow + direction && Math.abs(endCol - startCol) == 1) {
            Piece target = board[endRow][endCol];
            return target != null && target.isWhite() != this.isWhite();
        }

        return false;
    }

    @Override
    public String toString() {
        return isWhite() ? "1P" : "2p";
    }
}
