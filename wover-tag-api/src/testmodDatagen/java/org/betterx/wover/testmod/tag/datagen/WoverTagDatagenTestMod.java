package org.betterx.wover.testmod.tag.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.WoverTagTestMod;

public class WoverTagDatagenTestMod extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        
    }

    @Override
    protected ModCore modCore() {
        return WoverTagTestMod.C;
    }

}
