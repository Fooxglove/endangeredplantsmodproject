package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CoastalRoseBushWithoutRosehips;
import org.endangeredplants.item.CoastalRoseSucker; // 假设你的coastalrosesucker物品在ModItems类中

import java.util.Collections;
import java.util.List;

import static org.endangeredplants.item.CoastalRoseSucker.COASTAL_ROSE_SUCKER;

public class CoastalRosebushBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 添加生长阶段属性
    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, 2);

    // 带伤害的玫瑰丛方块类（无掉落逻辑）
    public static class ThornyBushBlock extends Block {
        public ThornyBushBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(GROWTH_STAGE, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(GROWTH_STAGE);
            super.createBlockStateDefinition(builder);
        }

        @Override
        public void entityInside(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, Entity entity) {
            if (entity instanceof Player) {
                entity.hurt(level.damageSources().cactus(), 1.0F);
            }
        }

        // 添加右键剪刀交互逻辑
        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack itemStack = player.getItemInHand(hand);

            if (itemStack.is(Items.SHEARS)) {
                // 播放剪刀声音
                level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);

                // 掉落coastalrosesucker物品
                if (!level.isClientSide) {
                    popResource(level, pos, new ItemStack(COASTAL_ROSE_SUCKER.get()));
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, pos, player, hand, hit);
        }

        // 添加随机生长逻辑
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return state.getValue(GROWTH_STAGE) < 2;
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            int currentStage = state.getValue(GROWTH_STAGE);
            if (currentStage < 2 && random.nextInt(10) > 0) {
                // 随机生长
                level.setBlock(pos, state.setValue(GROWTH_STAGE, currentStage + 1), 2);
                // 如果达到最终阶段，转换为开花状态
                if (currentStage + 1 == 2) {
                    level.setBlock(pos, CoastalRosebushFloweredBlock.COASTAL_ROSE_BUSH_FLOWERED_BLOCK.get().defaultBlockState(), 3);
                }
            }
        }

        // 添加掉落物逻辑
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            // 添加必要的参数
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);
            // 检查是否使用了剪刀
            ItemStack tool = lootparams.getParameter(LootContextParams.TOOL);
            if (tool != null && tool.is(Items.SHEARS)) {
                // 使用剪刀时掉落完整植株
                return Collections.singletonList(new ItemStack(
                        CoastalRoseBushWithoutRosehips.COASTAL_ROSE_BUSH_WITHOUT_ROSEHIPS.get()
                ));
            }
            // 非剪刀破坏时不掉落任何物品
            return Collections.emptyList();
        }

        // 添加玩家破坏时的伤害逻辑
        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            // 检查玩家是否使用了剪刀
            if (!player.isCreative() && !player.getMainHandItem().is(Items.SHEARS)) {
                // 非剪刀破坏时造成4点伤害
                player.hurt(level.damageSources().cactus(), 4.0F);
            }
            super.playerWillDestroy(level, pos, state, player);
        }
    }

    // 注册基础玫瑰丛方块（带伤害属性）
    public static final RegistryObject<Block> COASTAL_ROSE_BUSH_BLOCK = BLOCKS.register("coastalrosebushblock",
            () -> new ThornyBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks() // 启用随机刻
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> COASTAL_ROSE_BUSH_BLOCK_ITEM = ITEMS.register(
            "coastalrosebushblock",
            () -> new BlockItem(
                    COASTAL_ROSE_BUSH_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}