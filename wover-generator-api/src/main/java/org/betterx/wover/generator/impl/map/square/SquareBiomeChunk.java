package org.betterx.wover.generator.impl.map.square;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;

import net.minecraft.world.level.levelgen.WorldgenRandom;

public class SquareBiomeChunk implements BiomeChunk {
    private static final int BIT_OFFSET = 4;
    protected static final int WIDTH = 1 << BIT_OFFSET;
    private static final int SM_WIDTH = WIDTH >> 1;
    private static final int SM_BIT_OFFSET = BIT_OFFSET >> 1;
    private static final int MASK_OFFSET = SM_WIDTH - 1;
    protected static final int MASK_WIDTH = WIDTH - 1;

    private static final int SM_CAPACITY = SM_WIDTH * SM_WIDTH;
    private static final int CAPACITY = WIDTH * WIDTH;

    private final WoverBiomePicker.PickableBiome[] biomes;

    public SquareBiomeChunk(WorldgenRandom random, WoverBiomePicker picker) {
        WoverBiomePicker.PickableBiome[] PreBio = new WoverBiomePicker.PickableBiome[SM_CAPACITY];
        biomes = new WoverBiomePicker.PickableBiome[CAPACITY];

        for (int x = 0; x < SM_WIDTH; x++) {
            int offset = x << SM_BIT_OFFSET;
            for (int z = 0; z < SM_WIDTH; z++) {
                PreBio[offset | z] = picker.getBiome(random);
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            int offset = x << BIT_OFFSET;
            for (int z = 0; z < WIDTH; z++) {
                biomes[offset | z] = PreBio[getSmIndex(offsetXZ(x, random), offsetXZ(z, random))].getSubBiome(random);
            }
        }
    }

    @Override
    public WoverBiomePicker.PickableBiome getBiome(int x, int z) {
        return biomes[getIndex(x & MASK_WIDTH, z & MASK_WIDTH)];
    }

    @Override
    public void setBiome(int x, int z, WoverBiomePicker.PickableBiome biome) {
        biomes[getIndex(x & MASK_WIDTH, z & MASK_WIDTH)] = biome;
    }

    @Override
    public int getSide() {
        return WIDTH;
    }

    private int offsetXZ(int x, WorldgenRandom random) {
        return ((x + random.nextInt(2)) >> 1) & MASK_OFFSET;
    }

    private int getIndex(int x, int z) {
        return x << BIT_OFFSET | z;
    }

    private int getSmIndex(int x, int z) {
        return x << SM_BIT_OFFSET | z;
    }
}
