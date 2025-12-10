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
