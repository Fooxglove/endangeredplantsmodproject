package org.endangeredplants.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import org.endangeredplants.Endangeredplants;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> COASTAL_ROSE_BUSH_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "coastal_rose_bush_placed"));

    public static final ResourceKey<PlacedFeature> GLEHNIA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "glehnia_placed"));

    public static final ResourceKey<PlacedFeature> CYATHEA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "cyathea_placed"));

    public static final ResourceKey<PlacedFeature> WILD_KUMQUAT_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "wild_kumquat_placed"));

    public static final ResourceKey<PlacedFeature> BAISHANZU_FIR_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "baishanzu_fir_tree_placed"));

    public static final ResourceKey<PlacedFeature> ZANGNAN_CUPRESSUS_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "zangnan_cupressus_tree_placed"));

    public static final ResourceKey<PlacedFeature> FALSE_LITCHI_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "false_litchi_tree_placed"));

    public static final ResourceKey<PlacedFeature> YEW_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "yew_tree_placed"));

    public static final ResourceKey<PlacedFeature> HONGHE_PAPEDA_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "honghe_papeda_tree_placed"));

    public static final ResourceKey<PlacedFeature> MALE_GINKGO_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "male_ginkgo_tree_placed"));

    public static final ResourceKey<PlacedFeature> FEMALE_GINKGO_TREE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "female_ginkgo_tree_placed"));

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Coastal Rose Bush - 保持原有设置，单方块植物工作正常
        register(context, COASTAL_ROSE_BUSH_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "coastal_rose_bush"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        CountPlacement.of(20),
                        BiomeFilter.biome()
                ));

        // Glehnia - 保持原有设置，单方块植物工作正常
        register(context, GLEHNIA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "glehnia"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BiomeFilter.biome()
                ));

        // Cyathea - 保持原有设置，单方块植物工作正常
        register(context, CYATHEA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "cyathea"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BiomeFilter.biome()
                ));

        // Wild Kumquat - 保持原有设置，单方块植物工作正常
        register(context, WILD_KUMQUAT_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "wild_kumquat"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BiomeFilter.biome()
                ));

        // Baishanzu Fir Tree - 修复树木生成
        register(context, BAISHANZU_FIR_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "baishanzu_fir_tree"))),
                List.of(
                        // 提高生成频率用于调试
                        CountPlacement.of(1), // 每个区块尝试1次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 使用更宽松的方块谓词
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT,
                                Blocks.STONE, Blocks.DEEPSLATE, Blocks.SNOW_BLOCK)),
                        BiomeFilter.biome()
                ));

        // Zangnan Cupressus Tree - 修复树木生成
        register(context, ZANGNAN_CUPRESSUS_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "zangnan_cupressus_tree"))),
                List.of(
                        CountPlacement.of(2), // 每个区块尝试2次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 更宽松的方块谓词
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT,
                                Blocks.MOSS_BLOCK, Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES)),
                        BiomeFilter.biome()
                ));

        // False Litchi Tree - 修复树木生成
        register(context, FALSE_LITCHI_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "false_litchi_tree"))),
                List.of(
                        CountPlacement.of(2), // 每个区块尝试2次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 更宽松的方块谓词
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT,
                                Blocks.RED_SAND, Blocks.SAND)),
                        BiomeFilter.biome()
                ));

        // Yew Tree - 修复树木生成
        register(context, YEW_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "yew_tree"))),
                List.of(
                        CountPlacement.of(1), // 每个区块尝试1次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 更宽松的方块谓词，适合针叶林
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT,
                                Blocks.PODZOL, Blocks.SNOW_BLOCK)),
                        BiomeFilter.biome()
                ));

        // Honghe Papeda Tree - 修复树木生成
        register(context, HONGHE_PAPEDA_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "honghe_papeda_tree"))),
                List.of(
                        CountPlacement.of(1), // 每个区块尝试1次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 适合热带草原的方块谓词
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT,
                                Blocks.RED_SAND, Blocks.SAND)),
                        BiomeFilter.biome()
                ));

        // Male Ginkgo Tree - 修复树木生成
        register(context, MALE_GINKGO_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "male_ginkgo_tree"))),
                List.of(
                        CountPlacement.of(1), // 每个区块尝试1次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 适合平原的方块谓词
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT)),
                        BiomeFilter.biome()
                ));

        // Female Ginkgo Tree - 修复树木生成
        register(context, FEMALE_GINKGO_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "female_ginkgo_tree"))),
                List.of(
                        CountPlacement.of(1), // 每个区块尝试1次
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), // 确保在陆地上
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        // 适合平原的方块谓词
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT)),
                        BiomeFilter.biome()
                ));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}