package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        switch (this.getPieceType()) {
            case BISHOP -> {
                int[][] bishopDirections = {
                        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
                };
                addSlidingMoves(board, myPosition, bishopDirections, validMoves);

            } case ROOK -> {
                int[][] rookDirections = {
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
                };
                addSlidingMoves(board, myPosition, rookDirections, validMoves);
            }
            case QUEEN -> {
                int[][] queenDirections = {
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                        {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
                };
                addSlidingMoves(board, myPosition, queenDirections, validMoves);
            }
            case KNIGHT -> addKnightMoves(board, myPosition, validMoves);
            case KING -> addKingMoves(board, myPosition, validMoves);
            case PAWN -> addPawnMoves(board, myPosition, validMoves);
        } return validMoves;
    }

    private void addSlidingMoves(ChessBoard board,
                                 ChessPosition from,
                                 int[][] directions,
                                 Collection<ChessMove> moves) {
        int startRow = from.getRow();
        int startCol = from.getColumn();

        for (int[] direction : directions) {
            int newRow = startRow + direction[0];
            int newCol = startCol + direction[1];

            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(from, newPosition, null));
                } else {
                    if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(from, newPosition, null));
                    }
                    break;
                }

                newRow += direction[0];
                newCol += direction[1];
            }
        }
    }
    private void addKnightMoves(ChessBoard board,
                                ChessPosition from,
                                Collection<ChessMove> moves) {
        int row = from.getRow();
        int col = from.getColumn();

        int[][] knightDirections = {
                {2, 1}, {2, -1}, {1, 2}, {1, -2},
                {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}
        };

        for (int[] direction : knightDirections) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null
                        || pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(from, newPosition, null));
                }
            }
        }
    }

    private void addKingMoves(ChessBoard board,
                              ChessPosition from,
                              Collection<ChessMove> moves) {
        int row = from.getRow();
        int col = from.getColumn();

        int[][] kingDirections = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : kingDirections) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null
                        || pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(from, newPosition, null));
                }
            }
        }
    }

    private void addPawnMoves(ChessBoard board,
                              ChessPosition from,
                              Collection<ChessMove> moves) {
        int direction = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = from.getRow();
        int col = from.getColumn();

        int oneStepRow = row + direction;
        if (oneStepRow >= 1 && oneStepRow <= 8) {
            ChessPosition oneStepPos = new ChessPosition(oneStepRow, col);
            ChessPiece pieceAtOneStep = board.getPiece(oneStepPos);

            if (pieceAtOneStep == null) {
                if (oneStepRow == promotionRow) {
                    addPromotionMoves(from, oneStepPos, moves);
                } else {
                    moves.add(new ChessMove(from, oneStepPos, null));
                }
            }
        }

        if (row == startRow) {
            int twoStepRow = row + (2 * direction);
            ChessPosition between = new ChessPosition(oneStepRow, col);
            ChessPosition twoStepPos = new ChessPosition(twoStepRow, col);

            if (board.getPiece(between) == null && board.getPiece(twoStepPos) == null) {
                moves.add(new ChessMove(from, twoStepPos, null));
            }
        }

        int[][] captureDirections = {
                {direction, 1}, {direction, -1}
        };

        for (int[] diag : captureDirections) {
            int captureRow = row + diag[0];
            int captureCol = col + diag[1];

            if (captureRow < 1 || captureRow > 8 || captureCol < 1 || captureCol > 8) {
                continue;
            }

            ChessPosition capturePos = new ChessPosition(captureRow, captureCol);
            ChessPiece target = board.getPiece(capturePos);

            if (target == null || target.getTeamColor() == this.getTeamColor()) {
                continue;
            }

            if (captureRow == promotionRow) {
                addPromotionMoves(from, capturePos, moves);
            } else {
                moves.add(new ChessMove(from, capturePos, null));
            }
        }
    }

    private void addPromotionMoves(ChessPosition from,
                                   ChessPosition to,
                                   Collection<ChessMove> moves) {
        moves.add(new ChessMove(from, to, PieceType.QUEEN));
        moves.add(new ChessMove(from, to, PieceType.ROOK));
        moves.add(new ChessMove(from, to, PieceType.BISHOP));
        moves.add(new ChessMove(from, to, PieceType.KNIGHT));
    }
}


