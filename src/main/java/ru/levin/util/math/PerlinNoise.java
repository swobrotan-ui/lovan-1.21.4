package ru.levin.util.math;

import java.util.Random;

/**
 * Simple Perlin-like noise implementation for randomization.
 * Based on gradient noise for smooth jitter.
 */
public class PerlinNoise {
    private static final int PERM_SIZE = 256;
    private final int[] perm;
    private final Random random;

    public PerlinNoise() {
        random = new Random();
        perm = new int[PERM_SIZE * 2];
        for (int i = 0; i < PERM_SIZE; i++) {
            perm[i] = i;
        }
        shuffle(perm, PERM_SIZE);
        System.arraycopy(perm, 0, perm, PERM_SIZE, PERM_SIZE);
    }

    private void shuffle(int[] array, int size) {
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : (h == 12 || h == 14 ? x : 0);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x);
        double v = fade(y);
        int A = perm[X] + Y;
        int AA = perm[A];
        int AB = perm[A + 1];
        int B = perm[X + 1] + Y;
        int BA = perm[B];
        int BB = perm[B + 1];
        return lerp(v, lerp(u, grad(perm[AA], x, y), grad(perm[BA], x - 1, y)),
                lerp(u, grad(perm[AB], x, y - 1), grad(perm[BB], x - 1, y - 1)));
    }

    public double noise(double x) {
        return noise(x, 0);
    }
}
