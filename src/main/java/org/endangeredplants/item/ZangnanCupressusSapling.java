package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.ZangnanCupressusSaplingBlock;

public class ZangnanCupressusSapling {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    public static final RegistryObject<Item> ZANGNAN_CUPRESSUS_SAPLING = ITEMS.register("zangnancupressussapling",
            () -> new Item(new Item.Properties()) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    BlockState clickedBlock = context.getLevel().getBlockState(context.getClickedPos());

                    // Check if clicked block is grass, moss or dirt
                    if (clickedBlock.is(Blocks.GRASS_BLOCK) ||
                            clickedBlock.is(Blocks.MOSS_BLOCK) ||
                            clickedBlock.is(Blocks.DIRT)) {

                        // Get position above
                        var posAbove = context.getClickedPos().above();

                        // If above is air, place ZangnanCupressusSaplingBlock
                        if (context.getLevel().getBlockState(posAbove).isAir()) {
                            context.getLevel().setBlockAndUpdate(posAbove, ZangnanCupressusSaplingBlock.ZANGNAN_CUPRESSUS_SAPLING_BLOCK.get().defaultBlockState());

                            // Consume item if not in creative mode
                            if (!context.getPlayer().isCreative()) {
                                context.getItemInHand().shrink(1);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.PASS;
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}