package org.endangeredplants.worldgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.endangeredplants.worldgen.ModConfiguredFeatures;

import javax.annotation.Nullable;

public class MaleGinkgoGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return ModConfiguredFeatures.MALE_GINKGO_TREE;
    }
}