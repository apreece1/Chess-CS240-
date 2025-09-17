package chess;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
             int startRow = myPosition.getRow();
             int startCol = myPosition.getColumn();

             int currentRow = startRow + 1;
             int currentCol = startCol + 1;

             while (currentRow < 8 && currentRow >= 0 && currentCol < 8 && currentCol >= 0){
                 //check the piece at current row and col

                 ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
                 ChessPiece pieceAtCurrentPosition = board.getPiece(newPosition);

                 if (pieceAtCurrentPosition == null){


                 }
                 else if (pieceAtCurrentPosition.getTeamColor() != this.getTeamColor() )

                 //add a valid move to list
                 //then increment to next square
                 //currentRow ++
                 //Currentcol ++
             }
        }
        return List.of();
    }
}