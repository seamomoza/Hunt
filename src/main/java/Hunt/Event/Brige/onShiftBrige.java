package Hunt.Event.Brige;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class onShiftBrige implements Listener {

    private static int cobble = 1;
    @EventHandler
    public void onBrige(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT")) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            // 손에 든 아이템이 조약돌이고, 앉아있을 때
            if (player.isSneaking() && item != null && item.getType() == Material.COBBLESTONE) {
                if (item.getAmount() < cobble) {
                    return;
                }

                Vector dir = player.getLocation().getDirection().setY(0).normalize();
                Location baseLoc = player.getEyeLocation().clone().subtract(0, 2, 0);

                int placed = 0;
                for (int i = 0; i < 100; i++) {
                    Location checkLoc = baseLoc.clone().add(dir.clone().multiply(i));
                    Block block = checkLoc.getBlock();

                    if (block.getType() == Material.AIR) {
                        block.setType(Material.COBBLESTONE);

                        // 손에 든 아이템 1개 깎기
                        item.setAmount(item.getAmount() - cobble);
                        placed++;

                        player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1f, 1f);

                        if (placed >= cobble || item.getAmount() <= 0) break;
                    }
                }
            }
        }
    }
}
