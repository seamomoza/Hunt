package Hunt.Event.Death;

import Hunt.Command.HuntCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
            int inventorySize = targetInventory.getSize();
            Random random = new Random();

            // 방벽 아이템을 이미 가진 슬롯들을 확인
            boolean allBarrier = true;
            for (int i = 0; i < inventorySize; i++) {
                if (targetInventory.getItem(i) == null || targetInventory.getItem(i).getType() != Material.BARRIER) {
                    allBarrier = false;
                    break;
                }
            }

            // 모든 슬롯이 방벽 아이템이라면 실행하지 않음
            if (allBarrier) {
                Bukkit.getOnlinePlayers().forEach(player ->
                        player.sendMessage(ChatColor.RED + "타겟이 패배하였습니다!"));
                Bukkit.getOnlinePlayers().forEach(player ->
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.5f, 2));
                target.sendMessage("패배함ㅋ");
                return;
            }

            // 방벽 아이템을 랜덤 슬롯에 설정, 만약 그 슬롯이 방벽이라면 다른 슬롯으로
            int randomSlot = random.nextInt(inventorySize);
            while (targetInventory.getItem(randomSlot) != null && targetInventory.getItem(randomSlot).getType() == Material.BARRIER) {
                randomSlot = random.nextInt(inventorySize);
            }

            // 랜덤 슬롯에 BARRIER 아이템 설정
            ItemStack barrier = new ItemStack(Material.BARRIER);
            targetInventory.setItem(randomSlot, barrier);
        }
    }
}
