package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

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

                System.out.print(bg + piece + EscapeSequences.RESET_BG_COLOR);
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

    private static String initialPiece(int rank, char file) {
        return switch (rank) {
            case 2 -> EscapeSequences.WHITE_PAWN;
            case 7 -> EscapeSequences.BLACK_PAWN;

            case 1 -> switch (file) {
                case 'a','h' -> EscapeSequences.WHITE_ROOK;
                case 'b','g' -> EscapeSequences.WHITE_KNIGHT;
                case 'c','f' -> EscapeSequences.WHITE_BISHOP;
                case 'd' -> EscapeSequences.WHITE_QUEEN;
                case 'e' -> EscapeSequences.WHITE_KING;
                default -> EscapeSequences.EMPTY;
            };

            case 8 -> switch (file) {
                case 'a','h' -> EscapeSequences.BLACK_ROOK;
                case 'b','g' -> EscapeSequences.BLACK_KNIGHT;
                case 'c','f' -> EscapeSequences.BLACK_BISHOP;
                case 'd' -> EscapeSequences.BLACK_QUEEN;
                case 'e' -> EscapeSequences.BLACK_KING;
                default -> EscapeSequences.EMPTY;
            };

            default -> EscapeSequences.EMPTY;
        };
    }
}



