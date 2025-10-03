package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && getTeamTurn() == chessGame.getTeamTurn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), getTeamTurn());
    }

    public ChessGame() {

        board = new ChessBoard();
        board.resetBoard();
        this.teamTurn = TeamColor.WHITE; //white goes first bb

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
       this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);


        for (ChessMove move : potentialMoves) {
            //save state

            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();

            //make move

            board.addPiece(end, piece);
            board.addPiece(start, null);

            //check if in check

            boolean checkInCheck = isInCheck(piece.getTeamColor());

            //move piece back

            board.addPiece(start, piece);
            board.addPiece(end, capturedPiece);

            //add move if not in check to moves.

            if (!checkInCheck) {
                validMoves.add(move);
            }
        }

        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);

        //check for piece and turn
        if(piece == null){
            throw new InvalidMoveException("No piece at start.");
        }
        if(piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Not teams turn");
        }

        //check if move is valid
        Collection<ChessMove> legalMoves = validMoves(start);
        if(legalMoves == null || !legalMoves.contains(move)){
            throw new InvalidMoveException("Illegal Move.");

        }

        //do move
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(start, null);

        //switch teams/turns
        TeamColor nextTurn = (getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        setTeamTurn(nextTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //iterate through until find king

        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                if (pieceAtNewPosition != null && pieceAtNewPosition.getPieceType() ==  ChessPiece.PieceType.KING && pieceAtNewPosition.getTeamColor() == teamColor){
                    kingPosition = newPosition;
                    break;
                }
            }
            if (kingPosition != null) break;
        }

        if(kingPosition == null){
            return false;
        }

        //find opposite color
        TeamColor opposing = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        //go through each piece on other team
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition oppPosition = new ChessPosition(row, col);
                ChessPiece oppPiece = board.getPiece(oppPosition);

                if (oppPiece != null && oppPiece.getTeamColor() == opposing) {
                    Collection<ChessMove> oppMoves = oppPiece.pieceMoves(board, oppPosition);

                    for (ChessMove move : oppMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

       if(!isInCheck(teamColor)){
           return false;
       }

        // check for inCheck

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                //check for other team moves
                if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(newPosition);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        //return true if no valid moves
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //not in check
        if(isInCheck(teamColor)){
            return false;
        }
        //check if opp has moves
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                //check for other team moves
                if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(newPosition);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
        //return tru id no valid moves
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
