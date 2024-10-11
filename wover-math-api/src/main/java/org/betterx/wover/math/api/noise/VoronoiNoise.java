package org.betterx.wover.math.api.noise;


import org.betterx.wover.math.api.MathHelper;

import net.minecraft.core.BlockPos;

import java.util.Random;

public class VoronoiNoise {
    private static final Random RANDOM = new Random();
    final int seed;

    public VoronoiNoise() {
        this(0);
    }

    public VoronoiNoise(int seed) {
        this.seed = seed;
    }

    private int getSeed(int x, int y, int z) {
        int h = seed + x * 374761393 + y * 668265263 + z;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }

    public double sample(double x, double y, double z) {
        int ix = MathHelper.floor(x);
        int iy = MathHelper.floor(y);
        int iz = MathHelper.floor(z);

        float px = (float) (x - ix);
        float py = (float) (y - iy);
        float pz = (float) (z - iz);

        float d = 10;

        for (int pox = -1; pox < 2; pox++) {
            for (int poy = -1; poy < 2; poy++) {
                for (int poz = -1; poz < 2; poz++) {
                    RANDOM.setSeed(getSeed(pox + ix, poy + iy, poz + iz));
                    float pointX = pox + RANDOM.nextFloat();
                    float pointY = poy + RANDOM.nextFloat();
                    float pointZ = poz + RANDOM.nextFloat();
                    float d2 = MathHelper.lengthSqr(pointX - px, pointY - py, pointZ - pz);
                    if (d2 < d) {
                        d = d2;
                    }
                }
            }
        }

        return Math.sqrt(d);
    }

    public Random getRandom(double x, double y, double z) {
        int ix = MathHelper.floor(x);
        int iy = MathHelper.floor(y);
        int iz = MathHelper.floor(z);

        float px = (float) (x - ix);
        float py = (float) (y - iy);
        float pz = (float) (z - iz);

        float d = 10;

        int posX = 0;
        int posY = 0;
        int posZ = 0;

        for (int pox = -1; pox < 2; pox++) {
            for (int poy = -1; poy < 2; poy++) {
                for (int poz = -1; poz < 2; poz++) {
                    RANDOM.setSeed(getSeed(pox + ix, poy + iy, poz + iz));
                    float pointX = pox + RANDOM.nextFloat();
                    float pointY = poy + RANDOM.nextFloat();
                    float pointZ = poz + RANDOM.nextFloat();
                    float d2 = MathHelper.lengthSqr(pointX - px, pointY - py, pointZ - pz);
                    if (d2 < d) {
                        d = d2;
                        posX = pox;
                        posY = poy;
                        posZ = poz;
                    }
                }
            }
        }

        posX += ix;
        posY += iy;
        posZ += iz;

        int seed = MathHelper.getSeed(posY, posX, posZ);
        RANDOM.setSeed(seed);

        return RANDOM;
    }

    public BlockPos[] getPos(double x, double y, double z, double scale) {
        int ix = MathHelper.floor(x);
        int iy = MathHelper.floor(y);
        int iz = MathHelper.floor(z);

        float px = (float) (x - ix);
        float py = (float) (y - iy);
        float pz = (float) (z - iz);

        float d = 10;
        float selX = 0;
        float selY = 0;
        float selZ = 0;
        float selXPre = 0;
        float selYPre = 0;
        float selZPre = 0;

        for (int pox = -1; pox < 2; pox++) {
            for (int poy = -1; poy < 2; poy++) {
                for (int poz = -1; poz < 2; poz++) {
                    RANDOM.setSeed(getSeed(pox + ix, poy + iy, poz + iz));
                    float pointX = pox + RANDOM.nextFloat();
                    float pointY = poy + RANDOM.nextFloat();
                    float pointZ = poz + RANDOM.nextFloat();
                    float d2 = MathHelper.lengthSqr(pointX - px, pointY - py, pointZ - pz);
                    if (d2 < d) {
                        d = d2;
                        selXPre = selX;
                        selYPre = selY;
                        selZPre = selZ;
                        selX = pointX;
                        selY = pointY;
                        selZ = pointZ;
                    }
                }
            }
        }

        BlockPos p1 = new BlockPos(
                (int) ((ix + (double) selX) * scale),
                (int) ((iy + (double) selY) * scale),
                (int) ((iz + (double) selZ) * scale)
        );
        BlockPos p2 = new BlockPos(
                (int) ((ix + (double) selXPre) * scale),
                (int) ((iy + (double) selYPre) * scale),
                (int) ((iz + (double) selZPre) * scale)
        );
        return new BlockPos[]{p1, p2};
    }
}
