package net.linkedsquash.nocturne.item.custom;

import net.linkedsquash.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

// inherits the class values from Minecraft's item class
public class WhipItem extends Item{

    //the class constructor that tells java how this class behaves,
    public WhipItem(final Properties properties)
    {
        super(properties);
        //calls the constructor of the parent class, Item, and passes it the item settings. Expressions from the ModItem class eventually end up here
    }


    //Whip weapon class

    //overrides a method from another class
    @Override
    //the return TYPE that tells Minecraft that it is for an item stack
    // Declares the use method and tells it to accept the Level, Player,and interaction hand as parameters,
    // Minecraft calls the use method when the whip is used
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        WhipEntity tip = WhipEntity.findActive(player);

        // An existing tip can be sent out again only while it is already returning.
        if (tip != null && !tip.checkRETRACTING()) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (!level.isClientSide) {
            if (tip == null) {
                level.addFreshEntity(new WhipEntity(player, level));
            } else if (!tip.relaunch(player)) {
                return InteractionResultHolder.fail(itemStack);
            }

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.1F
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        return false;
    }

}


