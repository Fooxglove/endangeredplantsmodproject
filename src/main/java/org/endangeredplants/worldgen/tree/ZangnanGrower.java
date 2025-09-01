package org.endangeredplants.worldgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.endangeredplants.worldgen.ModConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public class ZangnanGrower extends AbstractTreeGrower {

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean hasFlowers) {
        return ModConfiguredFeatures.ZANGNAN_CUPRESSUS_TREE;
    }
}