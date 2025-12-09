package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessMove;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class BoardPrinter {

    private static final char[] WHITE_FILES = {'a','b','c','d','e','f','g','h'};
    private static final char[] BLACK_FILES = {'h','g','f','e','d','c','b','a'};

    public static void printInitialBoard(boolean whitePerspective) {
        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();
        printGame(game, whitePerspective);
    }

    public static void printGame(ChessGame game, boolean whitePerspective) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        printBoard(game, whitePerspective);
    }

    private static void printBoard(ChessGame game, boolean whitePerspective) {
        ChessBoard board = game.getBoard();
        char[] files = whitePerspective ? WHITE_FILES : BLACK_FILES;
        int startRank = whitePerspective ? 8 : 1;
        int endRank = whitePerspective ? 1 : 8;
        int step = whitePerspective ? -1 : 1;

        for (int rank = startRank; (step > 0 ? rank <= endRank : rank >= endRank); rank += step) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + rank + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);

            for (int f = 0; f < 8; f++) {
                boolean light = ((rank + f) % 2 == 0);
                String bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                char file = files[f];
                int col = fileToCol(file);
                ChessPiece piece = board.getPiece(new ChessPosition(rank, col));
                String symbol = pieceSymbol(piece);

                System.out.print(bg + symbol + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

        printFileLabels(files);
    }

    private static int fileToCol(char file) {
        return (file - 'a') + 1;
    }

    private static void printFileLabels(char[] files) {
        System.out.print("  ");
        for (char c : files) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + " " + c + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
        System.out.println();
    }

    private static String pieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }

    public static void printGameWithHighlights(ChessGame game,
                                               boolean whitePerspective,
                                               ChessPosition from,
                                               Collection<ChessMove> moves) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        printBoardWithHighlights(game, whitePerspective, from, moves);
    }

    private static void printBoardWithHighlights(ChessGame game,
                                                 boolean whitePerspective,
                                                 ChessPosition from,
                                                 Collection<ChessMove> moves) {
        ChessBoard board = game.getBoard();
        char[] files = whitePerspective ? WHITE_FILES : BLACK_FILES;
        int startRank = whitePerspective ? 8 : 1;
        int endRank = whitePerspective ? 1 : 8;
        int step = whitePerspective ? -1 : 1;

        // Precompute destination squares
        Set<String> destSquares = new HashSet<>();
        for (ChessMove m : moves) {
            ChessPosition end = m.getEndPosition();
            destSquares.add(end.getRow() + "," + end.getColumn());
        }

        for (int rank = startRank; (step > 0 ? rank <= endRank : rank >= endRank); rank += step) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + rank + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);

            for (int f = 0; f < 8; f++) {
                char file = files[f];
                int col = fileToCol(file);

                boolean isFrom = (from.getRow() == rank && from.getColumn() == col);
                boolean isDest = destSquares.contains(rank + "," + col);

                // Decide background color
                String bg;
                if (isFrom) {
                    bg = EscapeSequences.SET_BG_COLOR_DARK_GREEN;   // selected piece
                } else if (isDest) {
                    bg = EscapeSequences.SET_BG_COLOR_GREEN;        // legal moves
                } else {
                    boolean light = ((rank + f) % 2 == 0);
                    bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                            : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                }

                ChessPiece piece = board.getPiece(new ChessPosition(rank, col));
                String symbol = pieceSymbol(piece);

                System.out.print(bg + symbol + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

        printFileLabels(files);
    }

}

