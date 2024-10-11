package org.betterx.wover.surface.impl.conditions;


import org.betterx.wover.surface.api.noise.NoiseParameterManager;
import org.betterx.wover.surface.mixin.SurfaceRulesContextAccessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class RoughNoiseConditionImpl implements SurfaceRules.ConditionSource {
    public static final Codec<RoughNoiseConditionImpl> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(o -> o.noise),
                    Codec.DOUBLE.fieldOf("min_threshold").forGetter(o -> o.minThreshold),
                    Codec.DOUBLE.fieldOf("max_threshold").orElse(Double.MAX_VALUE).forGetter(o -> o.maxThreshold),
                    FloatProvider.CODEC.fieldOf("roughness").forGetter(o -> o.roughness)
            )
            .apply(
                    instance,
                    (noise1, minThreshold1, maxThreshold1, roughness1) -> new RoughNoiseConditionImpl(
                            noise1,
                            roughness1,
                            minThreshold1,
                            maxThreshold1
                    )
            ));

    public static final KeyDispatchDataCodec<RoughNoiseConditionImpl> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    private final ResourceKey<NormalNoise.NoiseParameters> noise;
    private final double minThreshold;
    private final double maxThreshold;
    private final FloatProvider roughness;

    public RoughNoiseConditionImpl(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            FloatProvider roughness,
            double minThreshold,
            double maxThreshold
    ) {
        this.noise = noise;
        this.minThreshold = minThreshold;

        this.maxThreshold = maxThreshold;
        this.roughness = roughness;
    }

    public RoughNoiseConditionImpl(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            double minThreshold,
            double maxThreshold
    ) {
        this(noise, UniformFloat.of(-0.1f, 0.4f), minThreshold, maxThreshold);
    }

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return KEY_CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(final SurfaceRules.Context context2) {
        final SurfaceRulesContextAccessor ctx = SurfaceRulesContextAccessor.class.cast(context2);
        final NormalNoise normalNoise = ctx.getRandomState().getOrCreateNoise(this.noise);
        final RandomSource roughnessSource = ctx.getRandomState()
                                                .getOrCreateRandomFactory(NoiseParameterManager.ROUGHNESS_NOISE.location())
                                                .fromHashOf(NoiseParameterManager.ROUGHNESS_NOISE.location());

        class NoiseThresholdCondition extends SurfaceRules.LazyCondition {
            NoiseThresholdCondition() {
                super(context2);
            }

            @Override
            protected long getContextLastUpdate() {
                final SurfaceRulesContextAccessor ctx = SurfaceRulesContextAccessor.class.cast(this.context);
                return ctx.getLastUpdateY() + ctx.getLastUpdateXZ();
            }

            protected boolean compute() {
                double d = normalNoise
                        .getValue(
                                ctx.getBlockX(),
                                ctx.getBlockY(),
                                ctx.getBlockZ()
                        ) + roughness.sample(roughnessSource);
                return d >= minThreshold && d <= maxThreshold;
            }
        }

        return new NoiseThresholdCondition();
    }
}
