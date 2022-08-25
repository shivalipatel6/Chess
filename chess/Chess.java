package chess;

import java.util.Scanner;
/**
 *  @author Shivali Patel and David Dong
 * 
 * 
 */
/**
 * public class chess implements the chess game
 */
public class Chess {

    static King blackKing;
    static King whiteKing;
    public static int turnNum = 1; 

    
    /** 
     * Prints out the state of the chessboard after each successful move
     * of a chess Piece. 
     * <p>
     * @param chessBoard the current locations of the peices on the chessboard after a move
     */
    public static void ChessBoardPrint(Piece[][] chessBoard){
        int boardnum = 8;
        char[] boardlett = {'a','b','c','d','e','f','g','h'};
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[i].length; j++) {
                if(chessBoard[i][j] == null){
                     if( i % 2 == 0){   
                          if(j % 2 == 0){
                              System.out.print("  ");
                    }// all even indexes in this case is white
                    else{
                    System.out.print("##");                    
                }// all odd indexes in this case is Black 
            
                }//even numbers on the row first column is white
                else{
                     if(j % 2 == 0){
                        System.out.print("##");
                    }// all even indexes in this case is Black
                    else{
                        System.out.print("  ");
                    }// all odd indexes in this case is white

                }// odd numbers on the row is black

                }// if the space is null
                else{
                System.out.print(chessBoard[i][j].name);
                }// have a peice on the space
                System.out.print(" ");
            }// end inner for loop
            System.out.print(" " + boardnum);
                boardnum--; 
                System.out.println();
        }// end outerfor loop
        for(int i = 0; i < boardlett.length; i++){
            System.out.print(" " + boardlett[i]+ " ");
        }// end for loop for letters at end of board
        System.out.println();
        System.out.println();



    }// end ChessBoard Print
    
    /** 
     * Sets the Chessboard at the start of the game with the standard game set up
     * @param chessBoard - an array that holds all the locations of the chess peices on the board
     */
    public static void SetChessBoard(Piece[][] chessBoard){
        chessBoard[0][0] = new Rook("bR", false);
        chessBoard[0][1] = new Knight("bN", false);
        chessBoard[0][2] = new Bishop("bB", false);
        chessBoard[0][3] = new Queen("bQ", false);
        blackKing = new King("bK", false, 0, 4);
        chessBoard[0][4] = blackKing;
        chessBoard[0][5] = new Bishop("bB", false);
        chessBoard[0][6] = new Knight("bN", false);
        chessBoard[0][7] = new Rook("bR", false);
        
        for (int i = 0; i < chessBoard.length; i++){
            chessBoard[1][i] = new Pawn("bp", false);
            chessBoard[6][i] = new Pawn("wp", true);
        }// pawn set up

        chessBoard[7][0] = new Rook("wR",true);
        chessBoard[7][1] = new Knight("wN",true);
        chessBoard[7][2] = new Bishop("wB",true);
        whiteKing = new King("wK", true, 7, 3);
        chessBoard[7][3] = whiteKing;
        chessBoard[7][4] = new Queen("wQ",true);
        chessBoard[7][5] = new Bishop("wB",true);
        chessBoard[7][6] = new Knight("wN",true);
        chessBoard[7][7] = new Rook("wR",true);
         
    }// end set chess board (sets the beginning chessboard)

    
    /** 
     * Takes in user input and moves piece relative to input.
     * <p>
     * Evaluates for a resign, or if a draw is offered or accepted, or if a promotion of a pawn is attempted 
     * and the conditions are correct the movement will be sent to the method in Pawn called <a href="#{@link}">{@link explicitPromotion}</a>.
     * where the movement will be evaluated to see if it is a valid move.
     * <p>
     * Evaluates for the proper conditions to execute an enpassant; if the conditions are correct the movement coordinates
     * will be sent ti the method in Pawn called <a href="#{@link}">{@link enPassant}</a>. where the movement will be
     * evaluated to see if it is a valid move.
     * <p>
     * Evaluates the movement coordinates to determine the basic invalid bounds before sending the 
     * movement to chessboard. <a href="#{@link}">{@link move}</a>. which evaluates whether a movment is
     * a valid movement for that specific Peice type. 
     * <p>
     * also determines if the king peice is in check after each term in the game
     * 
     *
     * 
     * @param chessBoard - current locations of all the chess Peices on the board
     * @param turn - indicates whether it is the Black side's turn or White's
     * @param input - the player's action input given at the start of the turn.
     * @param drawOffered - indicates if another player has offered a draw.
     * @return int - returns 1 if inputs are valid, 0 if invalid, 2 if game ends, 3 if draw offered
     */
    public static int MakeMove(Piece[][] chessBoard, boolean turn, String input, int drawOffered) {
        // method returns turnCondition at the end if inputs are valid
        int turnCondition = 1;
        boolean whiteInCheck;
        boolean blackInCheck;
        Piece[] lastDeadPiece = new Piece[1];
        lastDeadPiece[0] = null;

        // check if player resigned
        if (input.equals("resign")) {
            if (turn) System.out.println("Black wins");
            else System.out.println("White wins");
            return 2;
        }
        // check if player accepted draw
        if (input.equals("draw") && drawOffered > 3) return 2;
        if (input.equals("draw") && drawOffered <= 3) return 0;

        String[] move = input.split(" ");
        // translate special user input
        if (move.length == 3) {
            if (move[2].equals("draw?")) {
                turnCondition = 4;
            }//  Starting a draw
            else{
                int oPos[] = TranslateMove(move[0]);
                int nPos[] = TranslateMove(move[1]);
                int tempi = oPos[0];
                int tempj = oPos[1];
                if (oPos == null || nPos == null) return 0; // check if new or old positions are not in bound
                if (oPos[0] == nPos[0] && oPos[1] == nPos[1]) return 0; // check if new and old positions are the same
                if (chessBoard[tempi][tempj] == null) return 0; // check if position has no piece
                if (Boolean.compare(chessBoard[tempi][tempj].side, turn) != 0) return 0; // check if piece belongs to player
               
                //check typeeeeeeeeee if not pawn invalid move, if pawn create an temp for the move.
                if(((chessBoard[tempi][tempj].name == "bp") || (chessBoard[tempi][tempj].name == "wp" ))){
                    Pawn tempPawn;
                    if(chessBoard[tempi][tempj].side == false){
                         tempPawn = new Pawn("bp", false);
                    }// black pawn
                    else{
                         tempPawn = new Pawn("wp", true);
                    }// white pawn
                
                boolean valid = tempPawn.explicitPromotion(chessBoard, oPos, nPos, move[2]);
                if (!valid) {
                    return 0;
                } // check if move was valid or not
                else{
                    return 1;
                }// move was valid
                }
                else{
                    return 0;
                }// tried to promote something not a pawn

           
            }// Promoting a pawn !!!!!!!!!!!!!

            
        }// if move.lenth = 3
        
        // translate regular user input
        int oPos[] = TranslateMove(move[0]);
        int nPos[] = TranslateMove(move[1]);
        int tempi = oPos[0];
        int tempj = oPos[1];
        if (oPos == null || nPos == null) return 0; // check if new or old positions are not in bound
        if (oPos[0] == nPos[0] && oPos[1] == nPos[1]) return 0; // check if new and old positions are the same
        if (chessBoard[tempi][tempj] == null) return 0; // check if position has no piece
        if (Boolean.compare(chessBoard[tempi][tempj].side, turn) != 0) return 0; // check if piece belongs to player
//##########################enpassant check########################################
       
            Pawn tempPawn;
            if( tempj != 7 && chessBoard[tempi][tempj +1] != null){
                if((chessBoard[tempi][tempj +1].name == "bp" )|| (chessBoard[tempi][tempj +1].name == "wp") &&
            (chessBoard[tempi][tempj +1].side != chessBoard[tempi][tempj].side)){
                tempPawn = (Pawn)chessBoard[tempi][tempj+1];
                if((turnNum-1 == tempPawn.getenpassant()) && (chessBoard[tempi][tempj]!= null && ((chessBoard[tempi][tempj].name == "bp") 
                || (chessBoard[tempi][tempj].name == "wp" )))){
                     tempPawn = (Pawn)chessBoard[tempi][tempj];
                   boolean valid = tempPawn.enPassant(chessBoard, oPos, nPos, (tempj+1));
                   if(valid == true){
                       return 1;
                   }// enpassant worked
                   }// right after a double move of the pawn right next to it
            }// checking that the side isnt null and we aren't reaching past the board
                    
               
            }// we are dealing with the side 
            else if( tempj != 0 && chessBoard[tempi][tempj -1] != null){
                if((chessBoard[tempi][tempj -1].name == "bp" )|| (chessBoard[tempi][tempj -1].name == "wp") &&
            (chessBoard[tempi][tempj -1].side != chessBoard[tempi][tempj].side)){
                tempPawn = (Pawn)chessBoard[tempi][tempj-1];
                if((turnNum-1 == tempPawn.getenpassant()) && (chessBoard[tempi][tempj]!= null && ((chessBoard[tempi][tempj].name == "bp") 
                || (chessBoard[tempi][tempj].name == "wp" )))){
                     tempPawn = (Pawn)chessBoard[tempi][tempj];
                   boolean valid = tempPawn.enPassant(chessBoard, oPos, nPos, (tempj-1));
                   if(valid == true){
                       return 1;
                   }// enpassant worked
                   }// right after a double move of the pawn right next to it
            }// checking that the side isnt null and we aren't reaching past the board
                    
               
            }// we are dealing with the side 
            

        if(chessBoard[tempi][tempj].name == "bp" || chessBoard[tempi][tempj].name == "wp"){
            tempPawn = (Pawn)chessBoard[tempi][tempj];
            tempPawn.sendTurnNum(turnNum);
        } // if piece is a pawn send in current turn

        boolean valid = chessBoard[tempi][tempj].move(chessBoard, oPos, nPos, lastDeadPiece);
        // check if move was valid or not
        if (!valid) {
            lastDeadPiece[0] = null;
            return 0;
        }

        whiteInCheck = whiteKing.check(chessBoard);
        blackInCheck = blackKing.check(chessBoard);
        // check if own king is in check
        if ((turn && whiteInCheck) || (!turn && blackInCheck)) {
            chessBoard[oPos[0]][oPos[1]] = chessBoard[nPos[0]][nPos[1]];
            chessBoard[nPos[0]][nPos[1]] = lastDeadPiece[0];
            if (chessBoard[oPos[0]][oPos[1]] instanceof King) {
                ((King)chessBoard[oPos[0]][oPos[1]]).xPos = oPos[0];
                ((King)chessBoard[oPos[0]][oPos[1]]).yPos = oPos[1];
            }

            return 0;
        }
        if ((!turn && whiteInCheck) || (turn && blackInCheck)) {
            if (turnCondition == 4) turnCondition = 5;
            else turnCondition = 3;
        }

        // either returns 1 (regular turn) or 2 (draw offered)
        return turnCondition;
    }
    
    
    /**
     *  Takes in user input and translates it into array positions.
     * returns null if inputs are out of array.
     * @param move - movement directions from player inputted at the beginning of the turn.
     * @return int[] - int array with the translated coordinates for the chessboard.
     */
    public static int[] TranslateMove(String move) {
        if (move.length() != 2) return null;
        int position[] = new int[2];
        String[] strPos = move.split("");
        switch (strPos[0]) {
            case "a": position[1] = 0;
            break;
            case "b": position[1] = 1;
            break;
            case "c": position[1] = 2;
            break;
            case "d": position[1] = 3;
            break;
            case "e": position[1] = 4;
            break;
            case "f": position[1] = 5;
            break;
            case "g": position[1] = 6;
            break;
            case "h": position[1] = 7;
            break;
            default: return null;
        }
        switch (strPos[1]) {
            case "1": position[0] = 7;
            break;
            case "2": position[0] = 6;
            break;
            case "3": position[0] = 5;
            break;
            case "4": position[0] = 4;
            break;
            case "5": position[0] = 3;
            break;
            case "6": position[0] = 2;
            break;
            case "7": position[0] = 1;
            break;
            case "8": position[0] = 0;
            break;
            default: return null;
        }   

        return position;
    }

    
    /** 
     * Main method- takes inputs from scanner and facilitates turn changes after a successful move.
     * @param args[] - terminal inputs
     */
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        Piece[][] chessBoard = new Piece[8][8];
        SetChessBoard(chessBoard);

        boolean turn = true;
        boolean gameOver = false;
        int validity = 1;
        while (!gameOver) {
            ChessBoardPrint(chessBoard);
            if (validity == 3 || validity == 5) System.out.println("Check"); // validity == 3
            if (turn) {
                System.out.print("White's move: ");
                String whiteMove = sc.nextLine();
                validity = MakeMove(chessBoard, turn, whiteMove, validity);

                // checks validity of move and switches turn if valid
                while (validity == 0) {
                    System.out.println("Illegal move, try again");
                    System.out.print("White's move: ");
                    whiteMove = sc.nextLine();
                    validity = MakeMove(chessBoard, turn, whiteMove, validity);
                }
                turnNum = turnNum + 1;
                turn = false;
            } else {
                System.out.print("Black's move: ");
                String blackMove = sc.nextLine();
                validity = MakeMove(chessBoard, turn, blackMove, validity);

                // checks validity of move and switches turn if valid
                while (validity == 0) {
                    System.out.println("Illegal move, try again");
                    System.out.print("Black's move: ");
                    blackMove = sc.nextLine();
                    validity = MakeMove(chessBoard, turn, blackMove, validity);
                }
                turnNum = turnNum +1; 
                turn = true;
            }
            if (validity == 2) gameOver = true;
        }

        sc.close();
    }// end of main

    
}// end of Chess