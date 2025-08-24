package org.endangeredplants.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.*;

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

    public static final ResourceKey<ConfiguredFeature<?, ?>> BAISHANZU_FIR_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "baishanzu_fir_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ZANGNAN_CUPRESSUS_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(Endangeredplants.MODID, "zangnan_cupressus_tree"));

    // 添加假荔枝树配置
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

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {

        // Coastal Rose Bush - Simple block generation (clustering handled in placement)
        register(context, COASTAL_ROSE_BUSH, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(CoastalRosebushBlock.COASTAL_ROSE_BUSH_BLOCK.get())
                ));

        // Glehnia - Single block generation
        register(context, GLEHNIA, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(GlehniaBlock.GLEHNIA_BLOCK.get())
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

        // Baishanzu Fir Tree - Custom tree with 8-9 blocks height, spruce-like foliage
        register(context, BAISHANZU_FIR_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(BaishanzuFirLog.BAISHANZU_FIR_LOG.get()),
                        new StraightTrunkPlacer(8, 1, 0), // 8-9 blocks high
                        BlockStateProvider.simple(BaishanzuFirLeaves.BAISHANZU_FIR_LEAVES.get()),
                        new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(2)),
                        new TwoLayersFeatureSize(2, 0, 2)
                ).build());

        // Zangnan Cupressus Tree - Very tall tree (40-50 blocks) with 8-11 layers of foliage
        register(context, ZANGNAN_CUPRESSUS_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(ZangnanCupressusLog.ZANGNAN_CUPRESSUS_LOG.get()),
                        new StraightTrunkPlacer(20, 10, 0), // 40-50 blocks high (40 base + 0-10 random)
                        BlockStateProvider.simple(ZangnanCupressusLeaves.ZANGNAN_CUPRESSUS_LEAVES.get()),
                        new SpruceFoliagePlacer(
                                UniformInt.of(2, 3),  // Radius at bottom layers
                                UniformInt.of(1, 2),  // Radius at top layers
                                UniformInt.of(5, 7)  // Number of foliage layers
                        ),
                        new TwoLayersFeatureSize(2, 1, 2)
                ).ignoreVines().build());

        // False Litchi Tree - 正常橡木大小，使用 blob 叶子
        register(context, FALSE_LITCHI_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(FalseLitchiLog.FALSE_LITCHI_LOG.get()),
                        new StraightTrunkPlacer(4, 2, 0), // 4-6 blocks high (橡木大小)
                        BlockStateProvider.simple(FalseLitchiLeaves.FALSE_LITCHI_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), // blob 形状叶子
                        new TwoLayersFeatureSize(1, 0, 1)
                ).build());

        // Yew Tree
        register(context, YEW_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(YewLog.YEW_LOG.get()),
                        new StraightTrunkPlacer(8, 1, 0), // 8-9 blocks high
                        BlockStateProvider.simple(YewLeaves.YEW_LEAVES.get()),
                        new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(2)),
                        new TwoLayersFeatureSize(2, 0, 2)
                ).build());

        // Honghe Papeda Tree
        register(context, HONGHE_PAPEDA_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(HonghePapedaLog.HONGHE_PAPEDA_LOG.get()),
                        new StraightTrunkPlacer(10, 5, 0), // 10-15 blocks high
                        BlockStateProvider.simple(HonghePapedaLeaves.HONGHE_PAPEDA_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 3),
                        new TwoLayersFeatureSize(3, 0, 2)
                ).build());

        register(context, MALE_GINKGO_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(GinkgoLog.GINKGO_LOG.get()),
                        new StraightTrunkPlacer(4, 2, 0), // 4-6 blocks high (橡木大小)
                        BlockStateProvider.simple(GinkgoLeaves.GINKGO_MALE_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), // blob 形状叶子
                        new TwoLayersFeatureSize(1, 0, 1)
                ).build());

        register(context, FEMALE_GINKGO_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(GinkgoLog.GINKGO_LOG.get()),
                        new StraightTrunkPlacer(4, 2, 0), // 4-6 blocks high (橡木大小)
                        BlockStateProvider.simple(GinkgoLeaves.GINKGO_FEMALE_LEAVES.get()),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), // blob 形状叶子
                        new TwoLayersFeatureSize(1, 0, 1)
                ).build());
    }

    private static <FC extends net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}