package ui;

public class BoardPrinter {

    public static void printInitialBoard(boolean whitePerspective) {
        System.out.println(EscapeSequences.ERASE_SCREEN);

        if (whitePerspective) {
            printWhiteView();
        } else {
            printBlackView();
        }
    }

    private static void printWhiteView() {
        char[] files = {'a','b','c','d','e','f','g','h'};

        for (int rank = 8; rank >= 1; rank--) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + rank + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);

            for (int f = 0; f < 8; f++) {
                boolean light = ((rank + f) % 2 == 0);
                String bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                String piece = initialPiece(rank, files[f]);

                System.out.print(bg + piece + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

        System.out.print("  ");
        for (char c : files) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + " " + c + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
        System.out.println();
    }

    private static void printBlackView() {
        char[] files = {'h','g','f','e','d','c','b','a'};

        for (int rank = 1; rank <= 8; rank++) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + rank + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);

            for (int f = 0; f < 8; f++) {
                boolean light = ((rank + f) % 2 == 0);
                String bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                char file = files[f];
                String piece = initialPiece(rank, file);

                System.out.print(bg + piece + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

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





