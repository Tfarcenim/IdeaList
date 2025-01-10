package tfar.idealist.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.idealist.client.ModClient;

public class BingoCardItem extends Item {
    public BingoCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ModClient.openBingoScreen();
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

}
