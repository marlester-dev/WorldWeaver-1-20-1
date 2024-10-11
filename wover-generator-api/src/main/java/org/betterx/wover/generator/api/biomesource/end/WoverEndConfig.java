package org.betterx.wover.generator.api.biomesource.end;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.map.hex.HexBiomeMap;
import org.betterx.wover.generator.impl.map.square.SquareBiomeMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class WoverEndConfig implements BiomeSourceConfig<WoverEndBiomeSource> {
    public static final WoverEndConfig VANILLA = new WoverEndConfig(
            EndBiomeMapType.VANILLA,
            EndBiomeGeneratorType.VANILLA,
            true,
            4096,
            128,
            128,
            128,
            128
    );
    public static final WoverEndConfig MINECRAFT_17 = new WoverEndConfig(
            EndBiomeMapType.SQUARE,
            EndBiomeGeneratorType.PAULEVS,
            true,
            VANILLA.innerVoidRadiusSquared * 16 * 16,
            256,
            256,
            256,
            256
    );
    public static final WoverEndConfig MINECRAFT_18 = new WoverEndConfig(
            EndBiomeMapType.HEX,
            IntegrationCore.RUNS_NULLSCAPE ? EndBiomeGeneratorType.VANILLA : EndBiomeGeneratorType.PAULEVS,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_17.innerVoidRadiusSquared,
            MINECRAFT_17.centerBiomesSize,
            MINECRAFT_17.voidBiomesSize,
            MINECRAFT_17.landBiomesSize,
            MINECRAFT_17.barrensBiomesSize
    );

    public static final WoverEndConfig MINECRAFT_18_LARGE = new WoverEndConfig(
            EndBiomeMapType.HEX,
            IntegrationCore.RUNS_NULLSCAPE ? EndBiomeGeneratorType.VANILLA : EndBiomeGeneratorType.PAULEVS,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize * 2,
            MINECRAFT_18.landBiomesSize * 4,
            MINECRAFT_18.barrensBiomesSize * 2
    );

    public static final WoverEndConfig MINECRAFT_18_AMPLIFIED = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.PAULEVS,
            true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize,
            MINECRAFT_18.landBiomesSize,
            MINECRAFT_18.barrensBiomesSize
    );

    public static final WoverEndConfig MINECRAFT_20 = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.VANILLA,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_17.innerVoidRadiusSquared,
            MINECRAFT_17.centerBiomesSize,
            MINECRAFT_17.voidBiomesSize,
            MINECRAFT_17.landBiomesSize,
            MINECRAFT_17.barrensBiomesSize
    );

    public static final WoverEndConfig MINECRAFT_20_LARGE = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.VANILLA,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize * 2,
            MINECRAFT_18.landBiomesSize * 4,
            MINECRAFT_18.barrensBiomesSize * 2
    );

    public static final WoverEndConfig MINECRAFT_20_AMPLIFIED = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.VANILLA,
            true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize,
            MINECRAFT_18.landBiomesSize,
            MINECRAFT_18.barrensBiomesSize
    );
    public static final WoverEndConfig DEFAULT = MINECRAFT_20;

    public static final Codec<WoverEndConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    EndBiomeMapType.CODEC
                            .fieldOf("map_type")
                            .orElse(DEFAULT.mapVersion)
                            .forGetter(o -> o.mapVersion),
                    EndBiomeGeneratorType.CODEC
                            .fieldOf("generator_version")
                            .orElse(DEFAULT.generatorVersion)
                            .forGetter(o -> o.generatorVersion),
                    Codec.BOOL
                            .fieldOf("with_void_biomes")
                            .orElse(DEFAULT.withVoidBiomes)
                            .forGetter(o -> o.withVoidBiomes),
                    Codec.INT
                            .fieldOf("inner_void_radius_squared")
                            .orElse(DEFAULT.innerVoidRadiusSquared)
                            .forGetter(o -> o.innerVoidRadiusSquared),
                    Codec.INT
                            .fieldOf("center_biomes_size")
                            .orElse(DEFAULT.centerBiomesSize)
                            .forGetter(o -> o.centerBiomesSize),
                    Codec.INT
                            .fieldOf("void_biomes_size")
                            .orElse(DEFAULT.voidBiomesSize)
                            .forGetter(o -> o.voidBiomesSize),
                    Codec.INT
                            .fieldOf("land_biomes_size")
                            .orElse(DEFAULT.landBiomesSize)
                            .forGetter(o -> o.landBiomesSize),
                    Codec.INT
                            .fieldOf("barrens_biomes_size")
                            .orElse(DEFAULT.barrensBiomesSize)
                            .forGetter(o -> o.barrensBiomesSize)
            )
            .apply(instance, WoverEndConfig::new));

    public WoverEndConfig(
            @NotNull EndBiomeMapType mapVersion,
            @NotNull EndBiomeGeneratorType generatorVersion,
            boolean withVoidBiomes,
            int innerVoidRadiusSquared,
            int centerBiomesSize,
            int voidBiomesSize,
            int landBiomesSize,
            int barrensBiomesSize
    ) {
        this.mapVersion = mapVersion;
        this.generatorVersion = generatorVersion;
        this.withVoidBiomes = withVoidBiomes;
        this.innerVoidRadiusSquared = innerVoidRadiusSquared;
        this.barrensBiomesSize = Mth.clamp(barrensBiomesSize, 1, 8192);
        this.voidBiomesSize = Mth.clamp(voidBiomesSize, 1, 8192);
        this.centerBiomesSize = Mth.clamp(centerBiomesSize, 1, 8192);
        this.landBiomesSize = Mth.clamp(landBiomesSize, 1, 8192);
    }

    public enum EndBiomeMapType implements StringRepresentable {
        VANILLA("vanilla", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker)),
        SQUARE("square", (seed, biomeSize, picker) -> new SquareBiomeMap(seed, biomeSize, picker)),
        HEX("hex", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker));

        public static final Codec<EndBiomeMapType> CODEC = StringRepresentable.fromEnum(EndBiomeMapType::values);
        public final String name;
        public final @NotNull MapBuilderFunction mapBuilder;

        EndBiomeMapType(String name, @NotNull MapBuilderFunction mapBuilder) {
            this.name = name;
            this.mapBuilder = mapBuilder;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum EndBiomeGeneratorType implements StringRepresentable {
        VANILLA("vanilla"),
        PAULEVS("paulevs");

        public static final Codec<EndBiomeGeneratorType> CODEC = StringRepresentable.fromEnum(EndBiomeGeneratorType::values);
        public final String name;

        EndBiomeGeneratorType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public final @NotNull EndBiomeMapType mapVersion;
    public final @NotNull EndBiomeGeneratorType generatorVersion;
    public final boolean withVoidBiomes;
    public final int innerVoidRadiusSquared;

    public final int voidBiomesSize;
    public final int centerBiomesSize;
    public final int landBiomesSize;
    public final int barrensBiomesSize;

    @Override
    public String toString() {
        return "EndConfig{" +
                "mapVersion=" + mapVersion +
                ", generatorVersion=" + generatorVersion +
                ", withVoidBiomes=" + withVoidBiomes +
                ", innerVoidRadiusSquared=" + innerVoidRadiusSquared +
                ", voidBiomesSize=" + voidBiomesSize +
                ", centerBiomesSize=" + centerBiomesSize +
                ", landBiomesSize=" + landBiomesSize +
                ", barrensBiomesSize=" + barrensBiomesSize +
                '}';
    }

    @Override
    public boolean couldSetWithoutRepair(BiomeSourceConfig<?> input) {
        if (input instanceof WoverEndConfig cfg) {
            return withVoidBiomes == cfg.withVoidBiomes && mapVersion == cfg.mapVersion && generatorVersion == cfg.generatorVersion;
        }
        return false;
    }

    @Override
    public boolean sameConfig(BiomeSourceConfig<?> input) {
        return this.equals(input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WoverEndConfig that = (WoverEndConfig) o;
        return withVoidBiomes == that.withVoidBiomes && innerVoidRadiusSquared == that.innerVoidRadiusSquared && voidBiomesSize == that.voidBiomesSize && centerBiomesSize == that.centerBiomesSize && landBiomesSize == that.landBiomesSize && barrensBiomesSize == that.barrensBiomesSize && mapVersion == that.mapVersion && generatorVersion == that.generatorVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                mapVersion,
                generatorVersion,
                withVoidBiomes,
                innerVoidRadiusSquared,
                voidBiomesSize,
                centerBiomesSize,
                landBiomesSize,
                barrensBiomesSize
        );
    }
}
