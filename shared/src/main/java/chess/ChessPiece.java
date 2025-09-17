package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
        //List holds valid moves
        Collection<ChessMove> validMoves = new ArrayList<>();

        //gets the starting row and column
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        switch (this.getPieceType()) {
            case BISHOP:
                int[][] bishopdirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

                for (int[] direction : bishopdirections) {
                    int rowDirection = direction[0];
                    int colDirection = direction[1];

                    //all directions
                    int currentRow = startRow + rowDirection;
                    int currentCol = startCol + colDirection;

                    while (currentRow <= 8 && currentRow >= 1 && currentCol <= 8 && currentCol >= 1) {
                        //creates position object for square being checked
                        ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
                        //Get or check for piece at that position since the square is empty we add it to the list and continue
                        ChessPiece pieceAtCurrentPosition = board.getPiece(newPosition);
                        if (pieceAtCurrentPosition == null) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                            //Check for opposing piece stop if there is one
                        } else {
                            if (pieceAtCurrentPosition.getTeamColor() != this.getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newPosition, null));
                            }

                            break;
                        }

                        currentRow += rowDirection;
                        currentCol += colDirection;

                    }
                }
                break;

            case KNIGHT:
                int[][] knightDirections = {{2,1},{1,2},{-2,1},
                        {-1,2},{-1,2},{-2,1},{-1,-2},{-2,-1}
                };

                for (int[] direction : knightDirections) {
                    int newRow = startRow + direction[0];
                    int newCol = startCol + direction[1];

                    if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                        if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
                break; //
        }

        return validMoves;
    }
}