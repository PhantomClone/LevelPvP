package me.phantomclone.levelpvp.util;

import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.eventmanager.EventManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class ClickableItem {

    private ItemStack item;
    private EventManager eventManager;

    private EventListener<PlayerInteractEvent> interactevent;
    private EventListener<PlayerDropItemEvent> dropEvent;

    private BiConsumer<Player, Block> consumer;

    private boolean destroyOnUse = false;

    public ClickableItem(ItemStack item, BiConsumer<Player, Block> consumer, EventManager eventManager) {
        this.item = item;
        this.consumer = consumer;
        this.eventManager = eventManager;
        this.interactevent = event -> {
            if (event.getPlayer().getInventory().getItemInHand().equals(item)) {
                if (!event.getAction().equals(Action.PHYSICAL)) {
                    event.setCancelled(true);
                    consumer.accept(event.getPlayer(), (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) ? event.getClickedBlock() : null);
                    if (destroyOnUse) {
                        destroy();
                    }
                }
            }
        };
        this.dropEvent = event -> {
            if (event.getItemDrop().getItemStack().equals(item)) {
                event.setCancelled(true);
            }
        };
        eventManager.registerEvent(PlayerInteractEvent.class, interactevent);
        eventManager.registerEvent(PlayerDropItemEvent.class, dropEvent);
    }

    public void giveItemToPlayer(Player player, int slot) {
        player.getInventory().setItem(slot, this.item);
    }

    public void destroy() {
        eventManager.unregisterEvent(PlayerInteractEvent.class, interactevent);
        eventManager.unregisterEvent(PlayerDropItemEvent.class, dropEvent);
    }

    public boolean isDestroyOnUse() {
        return destroyOnUse;
    }

    public void setDestroyOnUse(boolean destroyOnUse) {
        this.destroyOnUse = destroyOnUse;
    }
}
