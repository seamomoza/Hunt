package Hunt.Event.Compass;

import Hunt.Command.HuntCommand;
import Hunt.Main.Hunt;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassInteract implements Listener {

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.COMPASS) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player target = HuntCommand.currentTarget;
        if (event.getPlayer().hasCooldown(Material.COMPASS)) {
            event.getPlayer().sendActionBar(ChatColor.RED + "쿨타임이 남아있습니다: " + event.getPlayer().getCooldown(Material.COMPASS) / 20 + "초");
            return;
        }
        if (target == null || !target.isOnline()) {
            event.getPlayer().sendMessage(ChatColor.RED + "현재 추적 중인 타겟이 없습니다.");
            return;
        }
        event.getPlayer().setCooldown(Material.COMPASS, 20 * 60 * 10); // 10분 쿨타임

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 3) {
                    cancel();
                    return;
                }
                Location loc = target.getLocation();
                Firework fw = loc.getWorld().spawn(loc, Firework.class);
                fw.setMetadata("hunt_firework", new FixedMetadataValue(Hunt.getInstance(), true));
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder().withColor(Color.RED).trail(true).build());
                meta.setPower(1);
                fw.setFireworkMeta(meta);
                count++;
            }
        }.runTaskTimer(Hunt.getInstance(), 0L, 40L); // 1초마다 발사
    }
}
