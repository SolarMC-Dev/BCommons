package me.bradleysteele.commons.worker;

import me.bradleysteele.commons.inventory.BInventory;
import me.bradleysteele.commons.register.worker.BWorker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Handles the necessary inventory events for {@link BInventory} objects.
 *
 * @author Bradley Steele
 */
public class WorkerBInventory extends BWorker {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof BInventory) {
            ((BInventory) holder).onClick(event, (Player) event.getWhoClicked(), event.getCurrentItem());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof BInventory) {
            ((BInventory) holder).onClose(event, (Player) event.getPlayer());
        }
    }
}