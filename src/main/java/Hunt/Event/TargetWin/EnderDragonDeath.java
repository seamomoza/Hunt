package Hunt.Event.TargetWin;

import Hunt.Command.HuntCommand;
import Hunt.Main.Hunt;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class EnderDragonDeath implements Listener {
    private static final Random random = new Random();


    @EventHandler
    public void onDragon(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Entity dragon = event.getEntity();

            Player targetPlayer = HuntCommand.currentTarget;
            if (targetPlayer == null) return; // 널 체크는 기본!

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getWorld().equals(dragon.getWorld())) {
                    // 타겟이 아닌 플레이어들에게만 메시지 전송
                    if (!player.equals(targetPlayer)) {
                        player.sendMessage(ChatColor.GREEN + "엔더 드래곤이 처치되었습니다!");
                        player.sendMessage(ChatColor.GOLD + "타겟이 승리하였습니다!");
                    }
                }
            });

            // 타겟 플레이어에게는 별도로 승리 메시지
            targetPlayer.sendMessage(ChatColor.GOLD + "당신이 승리하였습니다!");
            firework(targetPlayer); // 축하 폭죽
            HuntCommand.currentTarget = null; // 타겟 초기화
        }
    }

    public void firework(Player player) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 100) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation();
                Firework fw = loc.getWorld().spawn(loc, Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();

                // 폭죽 효과 생성
                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(getRandomColor())
                        .withFade(getRandomColor())
                        .with(getRandomType())
                        .flicker(random.nextBoolean())
                        .trail(random.nextBoolean())
                        .build();

                meta.addEffect(effect);
                meta.setPower(1); // power는 높이 관련. 속도 아님.
                fw.setFireworkMeta(meta);

                // ✅ 랜덤한 방향(퍼지는 느낌)으로 속도 설정
                Vector randomVelocity = getRandomVelocity();
                fw.setVelocity(randomVelocity);

                count++;
            }
        }.runTaskTimer(Hunt.getInstance(), 0L, 1L);
    }

    private static Color getRandomColor() {
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private static Type getRandomType() {
        Type[] types = Type.values();
        return types[random.nextInt(types.length)];
    }

    private static Vector getRandomVelocity() {
        // 퍼지는 느낌으로 랜덤한 방향 속도 생성 (약간 위로 날아가게 Y값 설정)
        double x = (random.nextDouble() - 0.5) * 1.2; // -0.6 ~ 0.6
        double y = 0.5 + (random.nextDouble() * 0.5); // 0.5 ~ 1.0 (살짝 위로)
        double z = (random.nextDouble() - 0.5) * 1.2;
        return new Vector(x, y, z);
    }
}