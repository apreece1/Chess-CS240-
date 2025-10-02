package chess;

import java.util.ArrayList;
import java.util.Collection;
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
                    int newRow = startRow + direction[0];
                    int newCol = startCol + direction[1];

                    while (newRow <= 8 && newRow >= 1 && newCol <= 8 && newCol >= 1) {
                        //creates position object for square being checked
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
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

                        newRow += direction[0];
                        newCol += direction[1];

                    }
                }
                break;

            case KNIGHT:
                int[][] knightDirections = {{2, 1}, {2, -1}, {1, 2}, {1, -2},
                        {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}};

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
            //The knight and King have very similar abilities, with the king having different restrictions
            case KING:
                int[][] kingDirections = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

                for (int[] direction : kingDirections) {
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

            case PAWN:
                int pawnDirections;
                int promotionRow;

                if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    pawnDirections = 1;
                    startRow = 2;
                    promotionRow = 8;
                } else {
                    pawnDirections = -1;
                    startRow = 7;
                    promotionRow = 1;
                }
                //basic one square move
                int basicMoveRow = myPosition.getRow() + pawnDirections;
                int basicMoveCol = myPosition.getColumn();

                if (basicMoveRow >= 1 && basicMoveRow <= 8) {
                    ChessPosition newPosition = new ChessPosition(basicMoveRow, basicMoveCol);
                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition == null) {
                        if (basicMoveRow == promotionRow) {
                            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }

                //two square move
                if (myPosition.getRow() == startRow) {
                    int twoSquareRow = myPosition.getRow() + (pawnDirections * 2);
                    int twoSquareCol = myPosition.getColumn();

                    ChessPosition first = new ChessPosition(basicMoveRow, basicMoveCol);
                    ChessPosition second = new ChessPosition(twoSquareRow, twoSquareCol);

                    if (board.getPiece(first) == null && board.getPiece(second) == null) {
                        validMoves.add(new ChessMove(myPosition, second, null));
                    }
                }
                //diagonal capture

                int[][] captureDiag = {{pawnDirections, 1}, {pawnDirections, -1}};

                for (int[] Diag : captureDiag) {
                    int captureRow = myPosition.getRow() + Diag[0];
                    int captureCol = myPosition.getColumn() + Diag[1];

                    if (captureRow >= 1 && captureRow <= 8 && captureCol >= 1 && captureCol <= 8) {
                        ChessPosition newPosition = new ChessPosition(captureRow, captureCol);
                        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                        if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                            if (captureRow == promotionRow) {
                                validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                        }
                    }
                }
                break;

            //the rook introduces sliding method, this is used for the queen as well consider finding a way to combine them?
            case ROOK:
                int[][] rookDirections = {{1,0},{-1,0},{0,1},{0,-1}};

                for (int[] direction : rookDirections) {
                    int newRow = startRow + direction[0];
                    int newCol = startCol + direction[1];

                    while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);


                        if (pieceAtNewPosition == null) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        } else {
                            if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()){
                                validMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                            break;
                        }
                        newRow += direction[0];
                        newCol += direction[1];
                    }
                }
                break;

            //the same as rook but with more directions
            case QUEEN:
                int[][] queenDirections = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{1,-1},{-1,1}};

                for (int[] direction : queenDirections) {
                    int newRow = myPosition.getRow() + direction[0];
                    int newCol = myPosition.getColumn() + direction[1];

                    while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                        if (pieceAtNewPosition == null) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        } else {
                            if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                            break;
                        }
                        newRow += direction[0];
                        newCol += direction[1];
                    }
                }
                break;
        }
        return validMoves;
    }
}