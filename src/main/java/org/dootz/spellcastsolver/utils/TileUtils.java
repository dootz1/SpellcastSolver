package org.dootz.spellcastsolver.utils;

import java.util.Random;

public class TileUtils {
    private static final int[] SPELLCAST_LETTER_POINTS = {
            1, // A
            4, // B
            5, // C
            3, // D
            1, // E
            5, // F
            3, // G
            4, // H
            1, // I
            7, // J
            6, // K
            3, // L
            4, // M
            2, // N
            1, // O
            4, // P
            8, // Q
            2, // R
            2, // S
            2, // T
            4, // U
            5, // V
            5, // W
            7, // X
            4, // Y
            8  // Z
    };

    private static final Random random = new Random();

    private TileUtils() {}

    public static int letterToPoints(char letter) {
        return SPELLCAST_LETTER_POINTS[letter - 'A'];
    }
    public static boolean validLetter(char letter) { return letter >= 'A' && letter <= 'Z'; }
    public static char randomLetter() {
        return (char) ('A' + random.nextInt(26));
    }
}
