package org.endangeredplants.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
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

    public static final ResourceKey<PlacedFeature> NYPA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "nypa_placed"));

    public static final ResourceKey<PlacedFeature> SAND_FRITILLARIA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "sand_fritillaria_placed"));

    public static final ResourceKey<PlacedFeature> WILD_SOYA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "wild_soya_placed"));

    public static final ResourceKey<PlacedFeature> KINGDONIA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "kingdonia_placed"));

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

    // 添加石斛放置特征
    public static final ResourceKey<PlacedFeature> DENDROBIUM_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "dendrobium_placed"));



    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // 单方块植物的放置修饰符 - 基于可工作的版本，但添加陆地检测
        List<PlacementModifier> singleBlockPlacement = List.of(
                RarityFilter.onAverageOnceEvery(10),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                // 确保不在水上或岩浆上生成
                BlockPredicateFilter.forPredicate(BlockPredicate.not(
                        BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                // 确保下方是固体陆地方块
                BlockPredicateFilter.forPredicate(
                        BlockPredicate.matchesBlocks(
                                net.minecraft.core.BlockPos.ZERO.below(),
                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.SAND,
                                Blocks.GRAVEL, Blocks.STONE
                        )
                ),
                BiomeFilter.biome()
        );

        // Coastal Rose Bush - 恢复地面检测过滤器以防止浮空生成
        register(context, COASTAL_ROSE_BUSH_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "coastal_rose_bush"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(50), // 稍微降低生成频率，因为每个斑块包含多个玫瑰
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        // 确保不在水上或岩浆上生成
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        // 确保下方是指定的固体陆地方块
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                        Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.SAND,
                                        Blocks.GRAVEL, Blocks.STONE
                                )
                        ),
                        BiomeFilter.biome()
                ));

        // Glehnia
        register(context, GLEHNIA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "glehnia"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(50),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        // 确保不在水上或岩浆上生成
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        // 确保下方是指定的固体陆地方块
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.SAND
                                )
                        ),
                        BiomeFilter.biome()
                ));

        // Cyathea - 使用修复后的单方块植物设置
        register(context, CYATHEA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "cyathea"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(70),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                        Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT
                                )
                        ),
                        BiomeFilter.biome()
                ));

        // Wild Kumquat - 使用修复后的单方块植物设置
        register(context, WILD_KUMQUAT_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "wild_kumquat"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(60),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                        Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT
                                )
                        ),
                        BiomeFilter.biome()
                ));

        register(context, NYPA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "nypa"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.MUD
                                )
                        ),
                        BiomeFilter.biome()
                ));

        register(context, SAND_FRITILLARIA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "sand_fritillaria"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(80),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.SAND,
                                        Blocks.GRAVEL
                                )
                        ),
                        BiomeFilter.biome()
                ));

        register(context, WILD_SOYA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "wild_soya"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(30),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.not(
                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA))),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                        Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT
                                )
                        ),
                        BiomeFilter.biome()
                ));

        register(context, KINGDONIA_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "kingdonia"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(1),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(120), VerticalAnchor.top()),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.anyOf(
                                        BlockPredicate.replaceable(), // 可替换方块（包括积雪）
                                        BlockPredicate.matchesBlocks(Blocks.SNOW) // 明确可以顶掉积雪
                                )
                        ),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                        Blocks.COARSE_DIRT, Blocks.STONE
                                )
                        ),
                        BiomeFilter.biome()
                ));

        // Dendrobium - 特殊的藤蔓植物放置逻辑，成小群生成
        register(context, DENDROBIUM_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "dendrobium"))),
                List.of(
                        RarityFilter.onAverageOnceEvery(1), // 增加生成频率便于测试
                        InSquarePlacement.spread(),
                        CountPlacement.of(ConstantInt.of(30)), // 每个区块尝试生成30次
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(150)), // 地面以上，绝对高度150格以下
                        // 允许在空气方块和藤蔓位置生成
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.anyOf(
                                        BlockPredicate.matchesBlocks(Blocks.AIR),
                                        BlockPredicate.matchesBlocks(Blocks.VINE),
                                        BlockPredicate.matchesBlocks(Blocks.CAVE_AIR)
                                )
                        ),
                        // 确保至少有一个水平方向的相邻方块是指定类型的方块
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.anyOf(
                                        // 检查北侧相邻方块
                                        BlockPredicate.anyOf(
                                                BlockPredicate.matchesBlocks(BlockPos.ZERO.north(),
                                                        Blocks.JUNGLE_LOG, Blocks.DARK_OAK_LOG, Blocks.STONE,
                                                        Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
                                                        Blocks.GRANITE, Blocks.ANDESITE),
                                                // 检查注册名以log结尾的方块
                                                BlockPredicate.matchesTag(BlockPos.ZERO.north(),
                                                        BlockTags.LOGS)
                                        ),
                                        // 检查南侧相邻方块
                                        BlockPredicate.anyOf(
                                                BlockPredicate.matchesBlocks(BlockPos.ZERO.south(),
                                                        Blocks.JUNGLE_LOG, Blocks.DARK_OAK_LOG, Blocks.STONE,
                                                        Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
                                                        Blocks.GRANITE, Blocks.ANDESITE),
                                                BlockPredicate.matchesTag(BlockPos.ZERO.south(),
                                                        BlockTags.LOGS)
                                        ),
                                        // 检查东侧相邻方块
                                        BlockPredicate.anyOf(
                                                BlockPredicate.matchesBlocks(BlockPos.ZERO.east(),
                                                        Blocks.JUNGLE_LOG, Blocks.DARK_OAK_LOG, Blocks.STONE,
                                                        Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
                                                        Blocks.GRANITE, Blocks.ANDESITE),
                                                BlockPredicate.matchesTag(BlockPos.ZERO.east(),
                                                        BlockTags.LOGS)
                                        ),
                                        // 检查西侧相邻方块
                                        BlockPredicate.anyOf(
                                                BlockPredicate.matchesBlocks(BlockPos.ZERO.west(),
                                                        Blocks.JUNGLE_LOG, Blocks.DARK_OAK_LOG, Blocks.STONE,
                                                        Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
                                                        Blocks.GRANITE, Blocks.ANDESITE),
                                                BlockPredicate.matchesTag(BlockPos.ZERO.west(),
                                                        BlockTags.LOGS)
                                        )
                                )
                        ),
                        BiomeFilter.biome()
                ));


        // 树木的通用放置修饰符 - 保持原样不变
        List<PlacementModifier> treePlacement = List.of(
                RarityFilter.onAverageOnceEvery(60),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                SurfaceWaterDepthFilter.forMaxDepth(0),
                BlockPredicateFilter.forPredicate(
                        BlockPredicate.allOf(
                                BlockPredicate.replaceable(),
                                BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), net.minecraft.core.BlockPos.ZERO),
                                BlockPredicate.matchesBlocks(net.minecraft.core.BlockPos.ZERO.below(),
                                        Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                        Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.SAND)
                        )
                ),
                BiomeFilter.biome()
        );

        // Baishanzu Fir Tree
        register(context, BAISHANZU_FIR_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "baishanzu_fir_tree"))),
                treePlacement);

        // Zangnan Cupressus Tree
        register(context, ZANGNAN_CUPRESSUS_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "zangnan_cupressus_tree"))),
                treePlacement);

        // False Litchi Tree
        register(context, FALSE_LITCHI_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "false_litchi_tree"))),
                treePlacement);

        // Yew Tree
        register(context, YEW_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "yew_tree"))),
                treePlacement);

        // Honghe Papeda Tree
        register(context, HONGHE_PAPEDA_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "honghe_papeda_tree"))),
                treePlacement);

        // Male Ginkgo Tree
        register(context, MALE_GINKGO_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "male_ginkgo_tree"))),
                treePlacement);

        // Female Ginkgo Tree
        register(context, FEMALE_GINKGO_TREE_PLACED, configuredFeatures.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                new ResourceLocation(Endangeredplants.MODID, "female_ginkgo_tree"))),
                treePlacement);


    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}