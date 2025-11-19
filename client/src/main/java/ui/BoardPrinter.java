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

