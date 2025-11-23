package ui;

public class BoardPrinter {

    private static final char[] WHITE_FILES = {'a','b','c','d','e','f','g','h'};
    private static final char[] BLACK_FILES = {'h','g','f','e','d','c','b','a'};

    public static void printInitialBoard(boolean whitePerspective) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        printBoard(whitePerspective);
    }

