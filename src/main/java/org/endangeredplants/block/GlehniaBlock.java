package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CoastalRoseBushWithoutRosehips;
import org.endangeredplants.item.Glehnia;
import org.endangeredplants.item.GlehniaRoot;
import org.endangeredplants.item.GlehniaSeeds;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GlehniaBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 修改后的珊瑚菜丛方块类
    public static class PlantBlock extends Block {
        public PlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        // 添加掉落物逻辑（剪刀破坏时）
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            // 检查是否使用了剪刀
            ItemStack tool = lootparams.getParameter(LootContextParams.TOOL);
            if (tool != null && tool.is(Items.SHEARS)) {
                // 使用剪刀时掉落根部
                return Collections.singletonList(new ItemStack(
                        GlehniaRoot.GLEHNIA_ROOT.get()
                ));
            }

            // 非剪刀破坏时不掉落任何物品
            return Collections.emptyList();
        }

        // 添加右键点击交互逻辑
        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
            if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
                // 随机掉落1-2个种子
                Random random = new Random();
                int seedCount = 1 + random.nextInt(2); // 1或2

                // 在玩家位置生成种子物品
                player.addItem(new ItemStack(GlehniaSeeds.GLEHNIA_SEEDS.get(), seedCount));

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
    }

    // 注册珊瑚菜丛方块
    public static final RegistryObject<Block> GLEHNIA_BLOCK = BLOCKS.register("glehniablock",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> GLEHNIA_BLOCK_ITEM = ITEMS.register(
            "glehniablock",
            () -> new BlockItem(
                    GLEHNIA_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}