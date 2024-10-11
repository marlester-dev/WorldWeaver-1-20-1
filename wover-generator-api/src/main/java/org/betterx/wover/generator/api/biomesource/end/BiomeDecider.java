package org.betterx.wover.generator.api.biomesource.end;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.generator.impl.biomesource.end.BiomeDeciderImpl;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

/**
 * Used to extend the BiomePlacement in the {@link WoverEndBiomeSource}
 */
public abstract class BiomeDecider {

    /**
     * used to create new {@link BiomeMap} instances
     */
    @FunctionalInterface
    public interface BiomeMapBuilderFunction {
        /**
         * Constructs a new {@link BiomeMap}
         *
         * @param picker    The picker the BiomeMap should use
         * @param biomeSize The biomeSize the map will use or -1 for the default size
         * @return a new {@link BiomeMap} instance
         */
        BiomeMap create(WoverBiomePicker picker, int biomeSize);
    }

    /**
     * used to determine wether or not a decider can provide this biome
     */
    @FunctionalInterface
    public interface BiomePredicate {
        boolean test(BiomeData biome);
    }

    protected WoverBiomePicker picker;
    protected BiomeMap map;
    private final BiomePredicate predicate;

    /**
     * Register a high priority Decider for the {@link WoverEndBiomeSource}.
     * Normally you should not need to register a high priority decider and instead use
     * {@link BiomeDecider#registerDecider(ResourceLocation, BiomeDecider)}.
     * BetterEnd (for example) will add
     *
     * @param location The {@link ResourceLocation} for the decider
     * @param decider  The initial decider Instance. Each Instance of the {@link WoverEndBiomeSource}
     *                 will call {@link BiomeDecider#createInstance(WoverBiomeSource)} to build a
     *                 new instance of this decider
     */
    public static void registerHighPriorityDecider(ResourceLocation location, BiomeDecider decider) {
        BiomeDeciderImpl.registerHighPriorityDecider(location, decider);
    }

    /**
     * Register a new Decider for the {@link WoverEndBiomeSource}
     *
     * @param location The {@link ResourceLocation} for the decider
     * @param decider  The initial decider Instance. Each Instance of the {@link WoverEndBiomeSource}
     *                 will call {@link BiomeDecider#createInstance(WoverBiomeSource)} to build a
     *                 new instance of this decider
     */
    public static void registerDecider(ResourceLocation location, BiomeDecider decider) {
        BiomeDeciderImpl.registerDecider(location, decider);
    }

    protected BiomeDecider(BiomePredicate predicate) {
        this(null, null, predicate);
    }

    /**
     * @param biomeRegistry The biome registry assigned to the creating BiomeSource
     * @param fallbackBiome The fallback biome in case picking fails
     * @param predicate     A predicate that decides if a given Biome can be provided by this decider
     */
    protected BiomeDecider(
            HolderGetter<Biome> biomeRegistry, ResourceKey<Biome> fallbackBiome, BiomePredicate predicate
    ) {
        this.predicate = predicate;
        this.map = null;
        if (biomeRegistry == null) {
            this.picker = null;
        } else {
            this.picker = new WoverBiomePicker(biomeRegistry, fallbackBiome);
        }
    }

    /**
     * Called to test, if a decider is suitable for the given BiomeSource.
     *
     * @param source The BiomeSource that wants to use the decider
     * @return true, if this decider is usable by that source
     */
    public abstract boolean canProvideFor(BiomeSource source);

    /**
     * Called from the BiomeSource whenever it needs to create a new instance of this decider.
     * <p>
     * Inheriting classes should overwrite this method and return Instances of the class. For
     * the base {@link BiomeDecider} you would return <em>new BiomeDecider(biomeSource.biomeRegistry, this.predicate);</em>
     *
     * @param biomeSource The biome source this decider is used from
     * @return A new instance
     */
    public abstract BiomeDecider createInstance(WoverBiomeSource biomeSource);

    /**
     * Called when the BiomeSources needs to construct a new {@link BiomeMap} for the picker.
     * <p>
     * The default implementation creates a new map with the instances picker and a default biome size
     *
     * @param mapBuilder A function you can use to create a new {@link BiomeMap} that conforms to the settings
     *                   of the current BiomeSource.
     */
    public void createMap(BiomeMapBuilderFunction mapBuilder) {
        this.map = mapBuilder.create(picker, -1);
    }

    /**
     * called whenever the BiomeSource needs to clear caches
     */
    public void clearMapCache() {
        map.clearCache();
    }

    /**
     * This method get's called whenever the BiomeSource populates the Biome Pickers. You need to
     * determine if the passed Biome is valid for your picker.
     * <p>
     * If this method returns false, the Biome wil not get added to any other Deciders/Pickers.
     * <p>
     * The default implementation will use the instances {@link BiomeDecider#predicate} to determine if
     * a biome should get added and return true if it was added.
     *
     * @param biome The biome that should get added if it matches the criteria of the picker
     * @return false, if other pickers/deciders are allowed to use the biome as well
     */
    public boolean addToPicker(BiomeData biome) {
        if (predicate.test(biome)) {
            picker.addBiome(biome);
            return true;
        }

        return false;
    }

    /**
     * Called whenever the picker needs to rebuild it's contents
     */
    public void rebuild() {
        //TODO: 1.19.3 test if this rebuilds once we have biomes
        if (picker != null)
            picker.rebuild();
    }

    /**
     * Called from the BiomeSource to determine the type of Biome it needs to place.
     *
     * @param originalType  The original biome type the source did select
     * @param suggestedType The currently suggested type. This will differ from <em>originalType</em> if other
     *                      {@link BiomeDecider} instances already had a new suggestion. You implementation should return the
     *                      <em>suggestedType</em> if it does not want to provide the Biome for this location
     * @param maxHeight     The maximum terrain height for this world
     * @param blockX        The block coordinate where we are at
     * @param blockY        The block coordinate where we are at
     * @param blockZ        The block coordinate where we are at
     * @param quarterX      The quarter Block Coordinate (which is blockX/4)
     * @param quarterY      The quarter Block Coordinate (which is blockY/4)
     * @param quarterZ      The quarter Block Coordinate (which is blockZ/4)
     * @return The <em>suggestedType</em> if this decider does not plan to provide a Biome, or a unique BiomeType.
     * The Biome Source will call {@link BiomeDecider#canProvideBiome(TagKey)} with the finally chosen type
     * for all available Deciders.
     */
    public TagKey<Biome> suggestType(
            TagKey<Biome> originalType,
            TagKey<Biome> suggestedType,
            int maxHeight,
            int blockX,
            int blockY,
            int blockZ,
            int quarterX,
            int quarterY,
            int quarterZ
    ) {
        return suggestType(
                originalType,
                suggestedType,
                0,
                maxHeight,
                blockX,
                blockY,
                blockZ,
                quarterX,
                quarterY,
                quarterZ
        );
    }

    /**
     * Called from the BiomeSource to determine the type of Biome it needs to place.
     *
     * @param originalType  The original biome type the source did select
     * @param suggestedType The currently suggested type. This will differ from <em>originalType</em> if other
     *                      {@link BiomeDecider} instances already had a new suggestion. You implementation should return the
     *                      <em>suggestedType</em> if it does not want to provide the Biome for this location
     * @param density       The terrain density at this location. Currently only valid if for {@link WoverEndBiomeSource}
     *                      that use the {@link WoverEndConfig.EndBiomeGeneratorType#VANILLA}
     * @param maxHeight     The maximum terrain height for this world
     * @param blockX        The block coordinate where we are at
     * @param blockY        The block coordinate where we are at
     * @param blockZ        The block coordinate where we are at
     * @param quarterX      The quarter Block Coordinate (which is blockX/4)
     * @param quarterY      The quarter Block Coordinate (which is blockY/4)
     * @param quarterZ      The quarter Block Coordinate (which is blockZ/4)
     * @param maxHeight
     * @return The <em>suggestedType</em> if this decider does not plan to provide a Biome, or a unique BiomeType.
     * The Biome Source will call {@link BiomeDecider#canProvideBiome(TagKey)} with the finally chosen type
     * for all available Deciders.
     */
    public abstract TagKey<Biome> suggestType(
            TagKey<Biome> originalType,
            TagKey<Biome> suggestedType,
            double density,
            int maxHeight,
            int blockX,
            int blockY,
            int blockZ,
            int quarterX,
            int quarterY,
            int quarterZ
    );


    /**
     * Called to check if this decider can place a biome for the specified type
     *
     * @param suggestedType The type of biome we need to place
     * @return true, if this type of biome can be provided by the current picker. If true
     * is returned, the BiomeSource will call {@link BiomeDecider#provideBiome(TagKey, int, int, int)}
     * next
     */
    public abstract boolean canProvideBiome(TagKey<Biome> suggestedType);

    /**
     * Called to check if this decider can place a biome for the specified type
     * <p>
     * The default implementation will return <em>map.getBiome(posX, posY, posZ)</em>
     *
     * @param suggestedType The type of biome we need to place
     * @return The methode should return a Biome from its {@link BiomeMap}. If null is returned, the next
     * decider (or the default map) will provide the biome
     */
    public WoverBiomePicker.PickableBiome provideBiome(TagKey<Biome> suggestedType, int posX, int posY, int posZ) {
        return map.getBiome(posX, posY, posZ);
    }
}

