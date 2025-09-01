package org.endangeredplants.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.*;

import java.util.List;



public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> COASTAL_ROSE_BUSH =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "coastal_rose_bush"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> GLEHNIA =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "glehnia"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> CYATHEA =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "cyathea"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_KUMQUAT =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "wild_kumquat"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> NYPA =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "nypa"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SAND_FRITILLARIA =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "sand_fritillaria"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_SOYA =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "wild_soya"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> KINGDONIA =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "kingdonia"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> BAISHANZU_FIR_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "baishanzu_fir_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ZANGNAN_CUPRESSUS_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "zangnan_cupressus_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> FALSE_LITCHI_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "false_litchi_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> YEW_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "yew_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> HONGHE_PAPEDA_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "honghe_papeda_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> MALE_GINKGO_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "male_ginkgo_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> FEMALE_GINKGO_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "female_ginkgo_tree"));

    // 添加石斛配置特征
    public static final ResourceKey<ConfiguredFeature<?, ?>> DENDROBIUM =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "dendrobium"));



    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {

        // 首先创建一个用于斑块内部的简单方块配置功能
        ConfiguredFeature<?, ?> coastalRoseSingle = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(CoastalRosebushBlock.COASTAL_ROSE_BUSH_BLOCK.get())
                )
        );

        ConfiguredFeature<?, ?> glehniaSingle = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(GlehniaBlock.GLEHNIA_BLOCK.get())
                )
        );

        // 创建对应的放置功能
        PlacedFeature coastalRosePlaced = new PlacedFeature(
                Holder.direct(coastalRoseSingle),
                List.of(
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.allOf(
                                        BlockPredicate.replaceable(), // 当前位置可替换
                                        BlockPredicate.matchesBlocks( // 下方是合适的方块
                                                net.minecraft.core.BlockPos.ZERO.below(),
                                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                                Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.SAND,
                                                Blocks.GRAVEL, Blocks.STONE
                                        ),
                                        BlockPredicate.not( // 不在水或岩浆中
                                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA)
                                        )
                                )
                        )
                )
        );

        PlacedFeature glehniaPlaced = new PlacedFeature(
                Holder.direct(glehniaSingle),
                List.of(
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.allOf(
                                        BlockPredicate.replaceable(), // 当前位置可替换
                                        BlockPredicate.matchesBlocks( // 下方是合适的方块
                                                net.minecraft.core.BlockPos.ZERO.below(),
                                                Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL,
                                                Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.SAND,
                                                Blocks.GRAVEL, Blocks.STONE
                                        ),
                                        BlockPredicate.not( // 不在水或岩浆中
                                                BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA)
                                        )
                                )
                        )
                )
        );

        // Coastal Rose Bush - 使用RandomPatchConfiguration生成小斑块
        register(context, COASTAL_ROSE_BUSH, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        32, // 每个区块尝试生成的次数
                        7, // x和z方向的扩散半径
                        3, // y方向的扩散半径
                        Holder.direct(coastalRosePlaced)
                ));

        // Glehnia - Single block generation
        register(context, GLEHNIA, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        32, // 每个区块尝试生成的次数
                        4, // x和z方向的扩散半径
                        3, // y方向的扩散半径
                        Holder.direct(glehniaPlaced)
                ));

        // Cyathea - Single block generation with age 0
        register(context, CYATHEA, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(CyatheaTop.CYATHEA_TOP.get().defaultBlockState().setValue(CyatheaTop.AGE, 0))
                ));

        // Wild Kumquat - Single block generation
        register(context, WILD_KUMQUAT, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(WildKumquatBlock.WILD_KUMQUAT_BLOCK.get())
                ));

        register(context, NYPA, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(Nypa.NYPA.get())
                ));

        register(context, SAND_FRITILLARIA, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(SandFritillariaBlock.SAND_FRITILLARIA_BLOCK.get())
                ));

        register(context, WILD_SOYA, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(WildSoyaBlock.WILD_SOYA_BLOCK.get())
                ));

        register(context, KINGDONIA, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(Kingdonia.KINGDONIA_BLOCK.get())
                ));

        // Dendrobium - 使用简单方块配置，生成时会自动附着到附近的表面
        register(context, DENDROBIUM, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(DendrobiumBlock.DENDROBIUM_BLOCK.get()
                                .defaultBlockState()
                                .setValue(DendrobiumBlock.AGE, 0)
                                .setValue(DendrobiumBlock.BLOOMING, false)
                                .setValue(DendrobiumBlock.NORTH, true)) // 默认附着北面
                ));

        // Baishanzu Fir Tree - 修复：添加dirt提供者和基础高度
        register(context, BAISHANZU_FIR_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(BaishanzuFirLog.BAISHANZU_FIR_LOG.get()),
                        new StraightTrunkPlacer(6, 2, 0), // 6-8 blocks high (稍微降低高度)
                        BlockStateProvider.simple(BaishanzuFirLeaves.BAISHANZU_FIR_LEAVES.get()),
                        new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(2)),
                        new TwoLayersFeatureSize(2, 0, 2)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT)) // 添加土壤提供者
                        .ignoreVines() // 忽略藤蔓生成
                        .build());

        // Zangnan Cupressus Tree - 修复：调整高度和配置
        register(context, ZANGNAN_CUPRESSUS_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(ZangnanCupressusLog.ZANGNAN_CUPRESSUS_LOG.get()),
                        new StraightTrunkPlacer(20, 10, 0), // 15-20 blocks high (降低高度以便测试)
                        BlockStateProvider.simple(ZangnanCupressusLeaves.ZANGNAN_CUPRESSUS_LEAVES.get()),
                        new SpruceFoliagePlacer(
                                UniformInt.of(2, 3),  // Radius at bottom layers
                                UniformInt.of(1, 2),  // Radius at top layers
                                UniformInt.of(5, 7)   // 减少叶子层数
                        ),
                        new TwoLayersFeatureSize(2, 1, 2)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .ignoreVines()
                        .build());

        // False Litchi Tree - 修复：添加土壤提供者
        register(context, FALSE_LITCHI_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(FalseLitchiLog.FALSE_LITCHI_LOG.get()),
                        new StraightTrunkPlacer(4, 2, 0), // 4-6 blocks high
                        BlockStateProvider.simple(FalseLitchiLeaves.FALSE_LITCHI_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .ignoreVines()
                        .build());

        // Yew Tree - 修复：添加土壤提供者
        register(context, YEW_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(YewLog.YEW_LOG.get()),
                        new StraightTrunkPlacer(6, 2, 0), // 6-8 blocks high
                        BlockStateProvider.simple(YewLeaves.YEW_LEAVES.get()),
                        new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(2)),
                        new TwoLayersFeatureSize(2, 0, 2)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .ignoreVines()
                        .build());

        // Honghe Papeda Tree - 修复：添加土壤提供者
        register(context, HONGHE_PAPEDA_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(HonghePapedaLog.HONGHE_PAPEDA_LOG.get()),
                        new StraightTrunkPlacer(8, 3, 0), // 8-11 blocks high
                        BlockStateProvider.simple(HonghePapedaLeaves.HONGHE_PAPEDA_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 3),
                        new TwoLayersFeatureSize(2, 0, 2)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .ignoreVines()
                        .build());

        // Male Ginkgo Tree - 修复：添加土壤提供者
        register(context, MALE_GINKGO_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(GinkgoLog.GINKGO_LOG.get()),
                        new StraightTrunkPlacer(4, 2, 0), // 4-6 blocks high
                        BlockStateProvider.simple(GinkgoLeaves.GINKGO_MALE_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .ignoreVines()
                        .build());

        // Female Ginkgo Tree - 修复：添加土壤提供者
        register(context, FEMALE_GINKGO_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(GinkgoLog.GINKGO_LOG.get()),
                        new StraightTrunkPlacer(4, 2, 0), // 4-6 blocks high
                        BlockStateProvider.simple(GinkgoLeaves.GINKGO_FEMALE_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .ignoreVines()
                        .build());


    }

    private static <FC extends net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}