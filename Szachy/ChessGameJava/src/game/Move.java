package game;

/**
 * Klasa reprezentująca pojedynczy ruch w szachach.
 * Obsługuje zarówno standardowe ruchy (np. "e2e4") jak i roszady ("O-O" oraz "O-O-O")
 */

public class Move {
    private int startX, startY;
    private int endX, endY;
    private boolean isWhiteTurn;
    private boolean isCastling;
    private boolean isKingside;

    public Move(String moveStr, boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
        this.isCastling = false;
        this.isKingside = false;

        if (moveStr.equalsIgnoreCase("O-O")) {
            // Roszada krótsza
            this.isCastling = true;
            this.isKingside = true;
            if (isWhiteTurn) {
                this.startY = 7;
                this.startX = 4;
                this.endY = 7;
                this.endX = 6;
            } else {
                this.startY = 0;
                this.startX = 4;
                this.endY = 0;
                this.endX = 6;
            }
        } else if (moveStr.equalsIgnoreCase("O-O-O")) {
            // Roszada dłuższa
            this.isCastling = true;
            this.isKingside = false;
            if (isWhiteTurn) {
                this.startY = 7;
                this.startX = 4;
                this.endY = 7;
                this.endX = 2;
            } else {
                this.startY = 0;
                this.startX = 4;
                this.endY = 0;
                this.endX = 2;
            }
        } else {
            if (moveStr.length() != 4) {
                throw new IllegalArgumentException("Ruch musi mieć długość 4 znaków lub być prawidłowym ruchem roszady (O-O or O-O-O)");
            }
            this.startX = moveStr.charAt(0) - 'a';
            this.startY = 8 - Character.getNumericValue(moveStr.charAt(1));
            this.endX = moveStr.charAt(2) - 'a';
            this.endY = 8 - Character.getNumericValue(moveStr.charAt(3));
        }

        if (!isValidCoordinates()) {
            throw new IllegalArgumentException("Pisz małymi literami/Wyszedłeś za planszę");
        }
    }

    private boolean isValidCoordinates() {
        return startX >= 0 && startX < 8 && startY >= 0 && startY < 8 &&
                endX >= 0 && endX < 8 && endY >= 0 && endY < 8;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public boolean isCastling() {
        return isCastling;
    }

    public boolean isKingside() {
        return isKingside;
    }
}
