package Hunt.Event.Death;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class BarrierKeep implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void item(ItemSpawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();
        if (item.getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void Int(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if ((clicked != null && clicked.getType() == Material.BARRIER) ||
                (cursor != null && cursor.getType() == Material.BARRIER)) {
            event.setCancelled(true); // 배리어 관련 모든 행동 취소
        }
    }

    @EventHandler
    public void Swap(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem() != null && event.getMainHandItem().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
        if (event.getOffHandItem() != null && event.getOffHandItem().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // 인벤토리 유지 설정
        event.setKeepInventory(true);

        // 기본 드롭 클리어
        List<ItemStack> drops = event.getDrops();
        drops.clear();

        // 인벤토리 순회
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item != null && item.getType() != Material.BARRIER) {
                // 배리어가 아닌 아이템만 드롭 추가
                drops.add(item);

                // 인벤토리에서 제거 (그래야 진짜 드롭됨)
                inventory.setItem(i, null);
            }
        }
    }
}
