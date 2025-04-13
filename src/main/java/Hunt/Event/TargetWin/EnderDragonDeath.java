package Hunt.Event.TargetWin;

import Hunt.Command.HuntCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EnderDragonDeath implements Listener {

    @EventHandler
    public void onDragon(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Entity dragon = event.getEntity();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getWorld().equals(dragon.getWorld())) {
                    player.sendMessage(ChatColor.GREEN + "엔더 드래곤이 처치되었습니다!");
                    player.sendMessage(ChatColor.GOLD + "타겟이 승리하였습니다!");
                }
            });
            Player targetPlayer = HuntCommand.currentTarget;
            targetPlayer.sendMessage(ChatColor.GOLD + "당신이 승리하였습니다!");
        }
    }
}
