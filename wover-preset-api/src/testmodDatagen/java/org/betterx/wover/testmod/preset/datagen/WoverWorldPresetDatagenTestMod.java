package org.betterx.wover.testmod.preset.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.WoverWorldPresetTestMod;

public class WoverWorldPresetDatagenTestMod extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(PresetProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return WoverWorldPresetTestMod.C;
    }

}
