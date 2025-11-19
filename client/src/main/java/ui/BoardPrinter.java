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




