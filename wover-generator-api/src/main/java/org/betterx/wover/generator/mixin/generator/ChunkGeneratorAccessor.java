package org.betterx.wover.generator.mixin.generator;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("biomeSource")
    @Mutable
    void wover_setBiomeSource(BiomeSource biomeSource);

    @Accessor("featuresPerStep")
    @Mutable
    void wover_setFeaturesPerStep(Supplier<List<FeatureSorter.StepFeatureData>> supplier);
}