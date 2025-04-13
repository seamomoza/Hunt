package Hunt.Event.Death;

import Hunt.Command.HuntCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;

public class KillTheTarget implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player target = HuntCommand.currentTarget;

        if (target != null && event.getEntity().getUniqueId().equals(target.getUniqueId())) {
            event.setDeathMessage(ChatColor.RED + "타겟이 사망했습니다!");

            // 타겟의 인벤토리에서 랜덤 슬롯을 선택
            PlayerInventory targetInventory = target.getInventory();
            int randomSlot = new Random().nextInt(targetInventory.getSize());
            ItemStack barrier = new ItemStack(Material.BARRIER);

            // 랜덤 슬롯에 BARRIER 아이템 설정
            targetInventory.setItem(randomSlot, barrier);
        }
    }
}
