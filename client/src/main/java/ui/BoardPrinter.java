package ui;

public class BoardPrinter {

    private static final char[] WHITE_FILES = {'a','b','c','d','e','f','g','h'};
    private static final char[] BLACK_FILES = {'h','g','f','e','d','c','b','a'};

    public static void printInitialBoard(boolean whitePerspective) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        printBoard(whitePerspective);
    }

    private static void printBoard(boolean whitePerspective) {
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
                String piece = initialPiece(rank, file);

                System.out.print(bg + piece + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

        printFileLabels(files);
    }

    

