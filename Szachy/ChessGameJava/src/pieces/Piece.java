package pieces;

public abstract class Piece {
    private boolean isWhite;
    private boolean hasMoved = false;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    protected boolean isPathClear(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        int rowDir = Integer.compare(endRow, startRow);
        int colDir = Integer.compare(endCol, startCol);

        int currentRow = startRow + rowDir;
        int currentCol = startCol + colDir;

        while (currentRow != endRow || currentCol != endCol) {
            if (board[currentRow][currentCol] != null) {
                return false; // Ścieżka jest zablokowana
            }
            currentRow += rowDir;
            currentCol += colDir;
        }

        return true;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public abstract boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board);

    @Override
    public abstract String toString();
}
