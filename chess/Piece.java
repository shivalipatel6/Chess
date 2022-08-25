package chess;
/**
 *  @author Shivali Patel and David Dong
 * 
 */

public abstract class Piece {
    String name;
    boolean side;
/**
 *  Class constructor for Piece
 * 
 * @param name - the name of the peice
 * @param side - indicates whether the peice belongs to the black or white side
 */
    Piece(String name, boolean side) {
        this.name = name;
        this.side = side;
    }
/**
 * Prints out the name and side of the indicated peice
 * 
 */
    // general methods
    public void printInfo() {
        System.out.println("Name, Side: " + name + ", " + side);
    }

    
    
    /** 
     * general move method which evaluates the move to see if it is an valid move for the Peice
     * and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    // specific methods to be filled in
    abstract public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece[] killedPiece);
}

// King piece
class King extends Piece {
    int xPos;
    int yPos;
    boolean moved = false;
/**
 * class constructor for King which extends peice
 * 
 * 
 * @param name - name of chess peice
 * @param side - indicates whether on Black side or White side
 * @param xPos - x coordinate of king's postion 
 * @param yPos - y coordinate of the king's postion
 */
    King(String name, boolean side, int xPos, int yPos) {
        super(name, side);
        this.xPos = xPos;
        this.yPos = yPos;
    }
/**
 * returns whether the king has moved or not
 * @return true if moved, false if not moved
 */
    public boolean getMoved(){
        return this.moved;
    }
  /** 
     * Move method for <a href="#{@link}">{@link King}</a>. which evaluates the move to see if it is an valid move for King
     * and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    @Override public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece[] killedPiece) {
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];

        // invalid, new position has piece on the same side
        if (chessBoard[newi][newj] != null && chessBoard[newi][newj].side == this.side && (!(chessBoard[newi][newj] instanceof Rook) || (chessBoard[newi][newj] instanceof Rook && ((Rook)chessBoard[newi][newj]).getMoved() || this.moved || this.check(chessBoard)))) return false;
        int iDif = Math.abs(newi - oldi);
        int jDif = Math.abs(newj - oldj);
        // valid, move piece
        if ((iDif == 0 || iDif == 1) && (jDif == 0 || jDif == 1)) {
            this.xPos = newi;
            this.yPos = newj;
            if (chessBoard[newi][newj] != null) killedPiece[0] = chessBoard[newi][newj];
            chessBoard[newi][newj] = chessBoard[oldi][oldj];
            chessBoard[oldi][oldj] = null;
        } else if (newi == oldi) {
            int tempj = oldj;
            while (tempj != newj) {
                if (tempj < newj) {
                    tempj++;
                    if (tempj == newj) break;
                    if (chessBoard[oldi][tempj] != null) return false;
                } else if (tempj > newj) {
                    tempj--;
                    if (tempj == newj) break;
                    if (chessBoard[oldi][tempj] != null) return false;
                }
            }
        } else {
            // invalid, new position is out of bounds for a king
            return false;
        }

        // castling
        if (chessBoard[newi][newj] != null && chessBoard[newi][newj] instanceof Rook) {
            ((Rook)chessBoard[newi][newj]).moved = true;
            if (newj == 0) {
                chessBoard[oldi][oldj - 2] = chessBoard[oldi][oldj];
                chessBoard[oldi][oldj] = null;
                this.xPos = oldi;
                this.yPos = oldj - 2;
                chessBoard[oldi][oldj - 1] = chessBoard[newi][newj];
                chessBoard[newi][newj] = null;
                // check if king is put into check revert everything
                if (this.check(chessBoard)) {
                    chessBoard[oldi][oldj] = chessBoard[oldi][oldj - 2];
                    this.xPos = oldi;
                    this.yPos = oldj;
                    chessBoard[oldi][oldj - 2] = null;
                    chessBoard[newi][newj] = chessBoard[oldi][oldj - 1];
                    chessBoard[oldi][oldj - 1] = null;
                    ((Rook)chessBoard[newi][newj]).moved = false;
                    return false;
                }
            } else {
                chessBoard[oldi][oldj + 2] = chessBoard[oldi][oldj];
                chessBoard[oldi][oldj] = null;
                this.xPos = oldi;
                this.yPos = oldj + 2;
                chessBoard[oldi][oldj + 1] = chessBoard[newi][newj];
                chessBoard[newi][newj] = null;
                // check if king is put into check revert everything
                if (this.check(chessBoard)) {
                    chessBoard[oldi][oldj] = chessBoard[oldi][oldj + 2];
                    this.xPos = oldi;
                    this.yPos = oldj;
                    chessBoard[oldi][oldj + 2] = null;
                    chessBoard[newi][newj] = chessBoard[oldi][oldj + 1];
                    chessBoard[oldi][oldj + 1] = null;
                    ((Rook)chessBoard[newi][newj]).moved = false;
                    return false;
                }
            }
            this.moved = true;
            return true;
        }

        this.moved = true;
        return true;
    }// ends move
/**
 * Does distance Calulations to determine whether the King peice in question is in check or not
 * @param chessBoard - an array that holds the locations of all current peices on the chessboard
 * @return - returns false if king is not in check and true if the king is in check
 */
    public boolean check(Piece[][] chessBoard) { // ISSUE WITH ILLEGAL MOVES GOING THROUGH REGARDLESS
        int tempi = xPos; // y-direction
        int tempj = yPos; // x-direction
        
        // use distance count to check for kings & pawns
        int distanceCount = 0;
        // check for bishops & queens
        while (tempi < 7 && tempj < 7) { // +x, -y
            tempi++;
            tempj++;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (!this.side && chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof Pawn) return true;
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Bishop)) return true;
            else break;
        }
        tempi = xPos;
        tempj = yPos;
        distanceCount = 0;
        while (tempi < 7 && tempj > 0) { // -x, -y
            tempi++;
            tempj--;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (!this.side && chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof Pawn) return true;
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Bishop)) return true;
            else break;
        }
        tempi = xPos;
        tempj = yPos;
        distanceCount = 0;
        while (tempi > 0 && tempj > 0) { // -x, +y
            tempi--;
            tempj--;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (this.side && chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof Pawn) return true;
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Bishop)) return true;
            else break;
        }
        tempi = xPos;
        tempj = yPos;
        distanceCount = 0;
        while (tempi > 0 && tempj < 7) { // +x, +y
            tempi--;
            tempj++;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (this.side && chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof Pawn) return true;
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Bishop)) return true;
            else break;
        }

        // use distanceCount to check for kings
        // check for castles and queens
        tempi = xPos;
        tempj = yPos;
        distanceCount = 0;
        while (tempi < 7) { // -y
            tempi++;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Rook)) return true;
            else break;
        }
        tempi = xPos;
        distanceCount = 0;
        while (tempi > 0) { // +y
            tempi--;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Rook)) return true;
            else break;
        }
        tempi = xPos;
        distanceCount = 0;
        while (tempj < 7) { // +x
            tempj++;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Rook)) return true;
            else break;
        }
        tempj = yPos;
        distanceCount = 0;
        while (tempj > 0) { // -x
            tempj--;
            distanceCount++;
            if (chessBoard[tempi][tempj] == null) {}
            else if (chessBoard[tempi][tempj].side != this.side && distanceCount == 1 && chessBoard[tempi][tempj] instanceof King) return true;
            else if (chessBoard[tempi][tempj].side != this.side && (chessBoard[tempi][tempj] instanceof Queen || chessBoard[tempi][tempj] instanceof Rook)) return true;
            else break;
        }

        // check for horse
        tempi = xPos - 1;
        tempj = yPos - 2;
        if (tempi > 0 && tempj > 0 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempj = yPos + 2;
        if (tempi > 0 && tempj < 7 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempi = xPos + 1;
        if (tempi < 7 && tempj < 7 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempj = yPos - 2;
        if (tempi < 7 && tempj > 0 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempi = xPos - 2;
        tempj = yPos - 1;
        if (tempi > 0 && tempj > 0 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempj = yPos + 1;
        if (tempi > 0 && tempj < 7 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempi = xPos + 2;
        if (tempi < 7 && tempj < 7 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;
        tempj = yPos - 1;
        if (tempi < 7 && tempj > 0 && chessBoard[tempi][tempj] != null && chessBoard[tempi][tempj].side != this.side && chessBoard[tempi][tempj] instanceof Knight) return true;

        return false; // not in check
    }

}// end king

// Queen piece
class Queen extends Piece {
    /**
     * 
     * Class constructor for Queen which extends Piece
    * 
     * @param name - the name of the peice
     * @param side - indicates whether the peice belongs to the black or white side
     */
    Queen(String name, boolean side) {
        super(name, side);
    }
 /** 
     * Move method for <a href="#{@link}">{@link Queen}</a>. which evaluates the move to 
     * see if it is an valid move for Queen and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    @Override public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece killedPiece[]) {
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];

        // invalid, new position has piece on the same side
        if (chessBoard[newi][newj] != null && chessBoard[newi][newj].side == this.side) return false;

        int tempi = oldi;
        int tempj = oldj;
        int iDif = Math.abs(newi - oldi);
        int jDif = Math.abs(newj - oldj);
        // make sure there are no pieces in between oPos and nPos
        if (oldi == newi || oldj == newj) {
            // check like rook
            while (tempi != newi) {
                if (tempi < newi) {
                    tempi++;
                    if (tempi == newi) break;
                    if (chessBoard[tempi][oldj] != null) return false;
                } else if (tempi > newi) {
                    tempi--;
                    if (tempi == newi) break;
                    if (chessBoard[tempi][oldj] != null) return false;
                }
            }
            while (tempj != newj) {
                if (tempj < newj) {
                    tempj++;
                    if (tempj == newj) break;
                    if (chessBoard[oldi][tempj] != null) return false;
                } else if (tempj > newj) {
                    tempj--;
                    if (tempj == newj) break;
                    if (chessBoard[oldi][tempj] != null) return false;
                }
            }
        } else if (iDif == jDif) {
            // check like bishop
            while (iDif > 0) {
                if (tempi < newi) {
                    tempi++;
                    if (tempi == newi) break;
                } else {
                    tempi--;
                    if (tempi == newi) break;
                }
                if (tempj < newj) tempj++;
                else tempj--;
                iDif--;

                // invalid, there exists a piece between oPos and nPos
                if (chessBoard[tempi][tempj] != null) return false;
            }
        } else {
            // invalid, new position is out of bounds for a queen
            return false;
        }

        // no pieces in between oPos and fPos
        killedPiece[0] = chessBoard[newi][newj];
        chessBoard[newi][newj] = chessBoard[oldi][oldj];
        chessBoard[oldi][oldj] = null;
        return true;
    }
}

// Rook piece
class Rook extends Piece {
    boolean moved = false;
    /**
     * 
     * Class constructor for Rook which extends Piece
    * 
     * @param name - the name of the peice
     * @param side - indicates whether the peice belongs to the black or white side
     */
    Rook(String name, boolean side) {
        super(name, side);
    }
/**
 *  returns true or false value determining if the rook has been moved
 * @return true if moved, false if not moved
 */
    public boolean getMoved(){
        return this.moved;
    }
/** 
     * Move method for <a href="#{@link}">{@link Rook}</a>. which evaluates the move to 
     * see if it is an valid move for Rook and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    @Override public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece killedPiece[]) {
        // IMPLEMENT CHECK, CHECKMATE, AND CASTLING
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];

        // invalid, new position has piece on the same side
        if (chessBoard[newi][newj] != null && chessBoard[newi][newj].side == this.side && (!(chessBoard[newi][newj] instanceof King) || (chessBoard[newi][newj] instanceof King && ((King)chessBoard[newi][newj]).getMoved() || this.moved || ((King)chessBoard[newi][newj]).check(chessBoard)))) return false;
        // invalid, new position is out of bounds for a rook
        if (!(oldi == newi || oldj == newj)) return false;
        
        // make sure there are no pieces in between oPos and nPos
        int tempi = oldi;
        int tempj = oldj;
        while (tempi != newi) {
            if (tempi < newi) {
                tempi++;
                if (tempi == newi) break;
                if (chessBoard[tempi][oldj] != null) return false;
            } else if (tempi > newi) {
                tempi--;
                if (tempi == newi) break;
                if (chessBoard[tempi][oldj] != null) return false;
            }
        }
        while (tempj != newj) {
            if (tempj < newj) {
                tempj++;
                if (tempj == newj) break;
                if (chessBoard[oldi][tempj] != null) return false;
            } else if (tempj > newj) {
                tempj--;
                if (tempj == newj) break;
                if (chessBoard[oldi][tempj] != null) return false;
            }
        }

        // castling
        if (chessBoard[newi][newj] != null && chessBoard[newi][newj] instanceof King) {
            ((King)chessBoard[newi][newj]).moved = true;
            if (oldj == 0) {
                chessBoard[newi][newj - 2] = chessBoard[newi][newj];
                ((King)chessBoard[newi][newj - 2]).xPos = newi;
                ((King)chessBoard[newi][newj - 2]).yPos = newj - 2;
                chessBoard[newi][newj] = null;
                chessBoard[newi][newj - 1] = chessBoard[oldi][oldj];
                chessBoard[oldi][oldj] = null;

                // check if king is put into check revert everything
                if (((King)chessBoard[newi][newj - 2]).check(chessBoard)) {
                    chessBoard[newi][newj] = chessBoard[newi][newj - 2];
                    ((King)chessBoard[newi][newj]).xPos = newi;
                    ((King)chessBoard[newi][newj]).yPos = newj;
                    chessBoard[newi][newj - 2] = null;
                    chessBoard[oldi][oldj] = chessBoard[newi][newj - 1];
                    chessBoard[newi][newj - 1] = null;
                    ((King)chessBoard[newi][newj]).moved = false;
                    return false;
                }
            } else {
                chessBoard[newi][newj + 2] = chessBoard[newi][newj];
                ((King)chessBoard[newi][newj + 2]).xPos = newi;
                ((King)chessBoard[newi][newj + 2]).yPos = newj + 2;
                chessBoard[newi][newj] = null;
                chessBoard[newi][newj + 1] = chessBoard[oldi][oldj];
                chessBoard[oldi][oldj] = null;

                // check if king is put into check revert everything
                if (((King)chessBoard[newi][newj + 2]).check(chessBoard)) {
                    chessBoard[newi][newj] = chessBoard[newi][newj + 2];
                    ((King)chessBoard[newi][newj]).xPos = newi;
                    ((King)chessBoard[newi][newj]).yPos = newj;
                    chessBoard[newi][newj + 2] = null;
                    chessBoard[oldi][oldj] = chessBoard[newi][newj + 1];
                    chessBoard[newi][newj + 1] = null;
                    ((King)chessBoard[newi][newj]).moved = false;
                    return false;
                }
            }
            this.moved = true;
            return true;
        }

        // no pieces in between oPos and fPos
        killedPiece[0] = chessBoard[newi][newj];
        chessBoard[newi][newj] = chessBoard[oldi][oldj];
        chessBoard[oldi][oldj] = null;
        this.moved = true;
        return true;
    }
}

// Bishop piece
class Bishop extends Piece {
    /**
     * 
     * Class constructor for Bishop which extends Piece
    * 
     * @param name - the name of the peice
     * @param side - indicates whether the peice belongs to the black or white side
     */
    Bishop(String name, boolean side) {
        super(name, side);
    }
/** 
     * Move method for <a href="#{@link}">{@link Bishop}</a>. which evaluates the move to 
     * see if it is an valid move for Bishop and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    @Override public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece killedPiece[]) {
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];

        // invalid, new position has piece on the same side
        if (chessBoard[newi][newj] != null && chessBoard[newi][newj].side == this.side) return false;
        
        int iDif = Math.abs(newi - oldi);
        int jDif = Math.abs(newj - oldj);
        // invalid, new position is out of bounds for a bishop
        if (iDif != jDif) return false;
        
        // make sure there are no pieces in between oPos and nPos
        int tempi = oldi;
        int tempj = oldj;
        while (iDif > 0) {
            if (tempi < newi) {
                tempi++;
                if (tempi == newi) break;
            } else {
                tempi--;
                if (tempi == newi) break;
            }
            if (tempj < newj) tempj++;
            else tempj--;
            iDif--;

            // invalid, there exists a piece between oPos and nPos
            if (chessBoard[tempi][tempj] != null) return false;
        }

        // no pieces in between oPos and fPos
        killedPiece[0] = chessBoard[newi][newj];
        chessBoard[newi][newj] = chessBoard[oldi][oldj];
        chessBoard[oldi][oldj] = null;
        return true;
    }
}

// Knight piece
class Knight extends Piece {
    /**
     * 
     * Class constructor for Rook which extends Piece
    * 
     * @param name - the name of the peice
     * @param side - indicates whether the peice belongs to the black or white side
     */
    Knight(String name, boolean side) {
        super(name, side);
    }
/** 
     * Move method for <a href="#{@link}">{@link Knight}</a>. which evaluates the move to 
     * see if it is an valid move for Knight and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    @Override public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece killedPiece[]) {
        // check legality of move then move
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];
        if (chessBoard[newi][newj] == null) {
            if(((oldi+2 == newi|| oldi - 2 == newi )&& (oldj + 1 == newj || oldj - 1 == newj)) ||  
            ((oldi-1 == newi || oldi +1 == newi) && (oldj+2 == newj || oldj -2 == newj)) ) {  
                killedPiece[0] = chessBoard[newi][newj];
                chessBoard[newi][newj] = chessBoard[oldi][oldj];
                chessBoard[oldi][oldj] = null;
             } // horsie movements
             else{
                 return false;
             }  // invalid horsie movements
        }// if the new move is an empty space
     else if (chessBoard[newi][newj].side == this.side) {
        // new position has piece on the same side as current piece
        return false;
    } // end if else where you try to capture a fellow peice
    else {
        if(((oldi+2 == newi|| oldi - 2 == newi )&& (oldj + 1 == newj || oldj - 1 == newj)) ||  
        ((oldi-1 == newi || oldi +1 == newi) && (oldj+2 == newj || oldj -2 == newj)) ) {  
            killedPiece[0] = chessBoard[newi][newj];
            chessBoard[newi][newj] = chessBoard[oldi][oldj];
            chessBoard[oldi][oldj] = null;
         } // horsie movements
         else{
             return false;
         }  // invalid horsie movements


    }// end final else (in this one we are taking another player's peice-possibly)

        return true;
    }
}

// Pawn piece
class Pawn extends Piece {
    boolean hasntMoved = true; 
    public int enpassant = -1;
    public int currturn; 
    /**
     *  class constructor for Pawn which extends Peice
     * @param name - name of the pawn
     * @param side - indicates whether the pawn is on the black side or white side.
     */
    Pawn(String name, boolean side) {
        super(name, side);
    }
/**
 *  returns which an int which indicates whether a pawn has done a double step and
 *           if that makes it a possible canidate to be captured via enpassant
 * 
 * @return - returns which an int of the turn number the pawn double stepped. if the pawn has not will return -1
 */
    public int getenpassant(){

        return this.enpassant;
    }// getter method for enpassant
/**
 *  retrieves the current term number and saves it in <a href="#{@link}">{@link currturn}</a>.
 * @param turnNum - the number of turns that has happened sent in from Chess
 */
    public void sendTurnNum(int turnNum){
        this.currturn = turnNum;  
    }// end sendTurnNum
/** 
     * Move method for <a href="#{@link}">{@link Pawn}</a>. which evaluates the move to 
     * see if it is an valid move for Pawn and if it is excutes it.
     * @param chessBoard - the current state of all Peices on the board
     * @param oPos - an array holding the coordinates for the peice being moved's orginial position
     * @param nPos - an array holding the coordinates for the peice being moved's potiental new postion.
     * @return boolean - returns true if the move is valid and executed, false is move is invalid.
     */
    @Override public boolean move(Piece[][] chessBoard, int[] oPos, int[] nPos, Piece killedPiece[]) {
        // check legality of move then move
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];
//!!!!!!!!!!!!!!!! EDGE CASE WHAT HAPPENS IF TRIES TO GO OFF BOARD?????????????????????????????
        if (chessBoard[newi][newj] == null) {
            if((this.side == true && oldi - 1 == newi && oldj == newj) ||
            (this.side == false && oldi + 1 == newi && oldj == newj)) {  
                if((this.side == true && newi == 0) || (this.side == false && newi == 7) ){
                    if(this.side == true){
                        killedPiece[0] = chessBoard[newi][newj];
                        chessBoard[newi][newj] = new Queen("wQ",true);
                        chessBoard[oldi][oldj] = null;
                    }// white pawn promotes to white queen
                    else{
                        killedPiece[0] = chessBoard[newi][newj];
                        chessBoard[newi][newj] = new Queen("bQ",false);
                        chessBoard[oldi][oldj] = null;
                    }// black pawn promotes to black queen

                }// basic promotoion 
                else{
                    killedPiece[0] = chessBoard[newi][newj];
                    chessBoard[newi][newj] = chessBoard[oldi][oldj];
                    chessBoard[oldi][oldj] = null;
                    this.hasntMoved = false;
                }// just a basic move forwards
              }// end check for one movement forwards
             else if((this.side == true && oldi - 2 == newi && oldj == newj) ||
             (this.side == false && oldi + 2 == newi && oldj == newj) ){  
// !!!!!!!!!!!!!!!!! CANNOT MOVE FORWARD IF THERE IS A PEICE INFRONT OF IT
                if( (this.side == true && chessBoard[newi +1][newj] == null) ||
                (this.side == false && chessBoard[newi - 1][newj] == null)){
                    if(this.hasntMoved == true)  {
                         this.hasntMoved = false;
                         killedPiece[0] = chessBoard[newi][newj];
                        chessBoard[newi][newj] = chessBoard[oldi][oldj];
                        chessBoard[oldi][oldj] = null;
                        this.enpassant = currturn;
                      }// checking that it is at the start position]
                      else{
                        return false;
                      }// invalid pawn movement cause it has already moved
                }// so a pawn doesn't try to jump over anything
                else{
                    return false;
                }// there is something blocking the way
                 
             }// if jumps fowards two moves
                else{
                    return false;
                }// attempts to move sideways when there is no other peice there
            } // pawn moves onto a empty space
        else{
        // trying to move in a space with another peice
        if(chessBoard[newi][newj].side != this.side ) { 
            if(( this.side == false && oldi + 1 == newi && oldj+1 == newj) ||
            ( this.side == false && oldi + 1 == newi && oldj - 1 == newj) ||
             (this.side == true && oldi - 1 == newi && oldj + 1 == newj) ||
             (this.side == true && oldi - 1 == newi && oldj - 1 == newj) ) {
                // capture
                if((this.side == true && newi == 0) || (this.side == false && newi == 7) ){
                    if(this.side == true){
                        killedPiece[0] = chessBoard[newi][newj];
                        chessBoard[newi][newj] = new Queen("wQ",true);
                        chessBoard[oldi][oldj] = null;
                    }// white pawn promotes to white queen on a capture
                    else{
                        killedPiece[0] = chessBoard[newi][newj];
                        chessBoard[newi][newj] = new Queen("bQ",false);
                        chessBoard[oldi][oldj] = null;

                    }// black pawn promotes to black queen on a capture
                }// we hit the end of the board with a default promotion
                else{
                    this.hasntMoved = false;
                    killedPiece[0] = chessBoard[newi][newj];
                    chessBoard[newi][newj] = chessBoard[oldi][oldj];
                    chessBoard[oldi][oldj] = null;
                }// normal capture
               
            }// capture
            else{
                return false;
            }
         } // invalide pawn movements
         else{
             return false;
         }  // invalid pawn movements trying to capture same side


    }// end final else (in this one we are taking another player's peice-possibly)



    return true;
    }// end move
/**
 * evaluates the move to see if it is a valid promotion move. if move is valid the pawn 
 * is promoted to indicated new Peice type
 * @param chessBoard - holds the locations of all current peices on the chessboard
 * @param oPos - current postion of the pawn being moved and promoted.
 * @param nPos - proposed new postion for promoted pawn.
 * @param newPeice - indicates the type of peice to be promoted.
 * @return - returns true is the promotion was valid and executed and false if the promotion is invalid
 */
    public boolean explicitPromotion(Piece[][] chessBoard, int[] oPos, int[] nPos, String newPeice) {
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];
        if (chessBoard[newi][newj] == null) {
            if((this.side == true && oldi - 1 == newi && oldj == newj) ||
            (this.side == false && oldi + 1 == newi && oldj == newj)) {
                if((this.side == true && newi == 0) || (this.side == false && newi == 7) ){
                    if(this.side == true){
                        switch (newPeice) {
                            case "Q": chessBoard[newi][newj] = new Queen("wQ",true);
                                      chessBoard[oldi][oldj] = null;
                            break;
                            case "N": chessBoard[newi][newj] = new Knight("wN",true);
                                      chessBoard[oldi][oldj] = null;
                            break;
                            case "R": chessBoard[newi][newj] = new Rook("wR",true);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "B": chessBoard[newi][newj] = new Bishop("wB",true);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            default: return false;
                        }// end switch case for each type of promotion- white peice
                    }// white pawn promotes on a reg step forwards
                    else{
                        switch (newPeice) {
                            case "Q": chessBoard[newi][newj] = new Queen("bQ",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "N": chessBoard[newi][newj] = new Knight("bN",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "R": chessBoard[newi][newj] = new Rook("bR",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "B": chessBoard[newi][newj] = new Bishop("bB",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            default: return false;
                        }// end switch case for each type of promotion-Black peice
                    }// black pawn promotes on a reg step forwards
                }// we hit the end of the board 
                else{
                    return false;
                }// tried to promote before end of board

             }// ensuring it is on a one step forwards
             else{
                return false;
             }// tried to move more than one step or diagonal onto an empty space

        }// promotion by moving into empty end
        else{   
            if(chessBoard[newi][newj].side != this.side ) { 
                if(( this.side == false && oldi + 1 == newi && oldj+1 == newj) ||
            ( this.side == false && oldi + 1 == newi && oldj - 1 == newj) ||
             (this.side == true && oldi - 1 == newi && oldj + 1 == newj) ||
             (this.side == true && oldi - 1 == newi && oldj - 1 == newj) ) {
                if((this.side == true && newi == 0) || (this.side == false && newi == 7) ){
                    if(this.side == true){
                        switch (newPeice) {
                            case "Q": chessBoard[newi][newj] = new Queen("wQ",true);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "N": chessBoard[newi][newj] = new Knight("wN",true);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "R": chessBoard[newi][newj] = new Rook("wR",true);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "B": chessBoard[newi][newj] = new Bishop("wB",true);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            default: return false;
                        }// end switch case for each type of promotion- white peice
                    }// white pawn promotes on a capture
                    else{
                        switch (newPeice) {
                            case "Q": chessBoard[newi][newj] = new Queen("bQ",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "N": chessBoard[newi][newj] = new Knight("bN",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "R": chessBoard[newi][newj] = new Rook("bR",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            case "B": chessBoard[newi][newj] = new Bishop("bB",false);
                                      chessBoard[oldi][oldj] = null;

                            break;
                            default: return false;
                        }// end switch case for each type of promotion-Black peice
                    }// black pawn promotes on a capture
                }// we hit the end of the board 
                else{
                    return false;
                }// attempts to capture and promote when not on the end board- valid move
                //not valid promotion 

             }// diagonal forward movement
             else{
                return false;
             }// attempting to take an enemy space on a non-diagonal move



            }// not trying to move onto a space with a piece from its own side
            else{
                return false;
            }// attempting to move onto a space with a piece from its own side

        }// attempting to move onto a space with an peice on it (possibly capture and promote sequence)


        return true; 
    }// end explicitPromotion
/**
 * evaluates whether the attempt at an enpassant is valid and if it is valid the enpassant
 * is executed.
 * @param chessBoard - holds the locations of all current peices on the chessboard
 * @param oPos - current postion of the pawn being moved and promoted.
 * @param nPos - proposed new postion for promoted pawn.
 * @param whichside - indicates which y coordinate location the pawn that is a valid capture option is on 
 * @return - returns true is enpassant was valid and executed, false is the enpassant was invalid.
 */
    public boolean enPassant(Piece[][] chessBoard, int[] oPos, int[] nPos, int whichside) {
        int oldi = oPos[0];
        int oldj = oPos[1];
        int newi = nPos[0];
        int newj = nPos[1];
   if((this.side == false && oldi + 1 == newi && oldj+1 == newj && newj == whichside )|| (this.side == true && oldi - 1 == newi && oldj + 1 == newj && newj == whichside) ||
        (this.side == false && oldi + 1 == newi && oldj - 1 == newj && newj == whichside) || (this.side == true && oldi - 1 == newi && oldj - 1 == newj && newj == whichside) ){
                   
            chessBoard[oldi][whichside] = null;
            chessBoard[newi][newj] = chessBoard[oldi][oldj];
            chessBoard[oldi][oldj] = null;
        }// enpassant conditions */
        else{
            return false;
        }// not a diagonal enpassant move even after filling out enpassant conditions

        return true;
    }// end enPassant



}// end peice