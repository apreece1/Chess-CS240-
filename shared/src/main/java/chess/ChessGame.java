package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

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
        throw new RuntimeException("Not implemented");
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
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition oppPosition = new ChessPosition(row, col);
                ChessPiece oppPiece = board.getPiece(oppPosition);
            }


        //calculate moves piece could make

        //check if piece is in kings position

        //returns
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
