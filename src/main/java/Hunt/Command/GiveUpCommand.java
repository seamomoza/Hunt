package Hunt.Command;

import Hunt.Main.Hunt;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class GiveUpCommand implements CommandExecutor {
    private static final Random random = new Random();
    private final Plugin plugin;

    public GiveUpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        Player target = HuntCommand.currentTarget;
        if (target == null) {
            player.sendMessage(ChatColor.RED + "현재 타겟이 지정되어 있지 않습니다.");
            return true;
        }

        // 타겟 초기화는 제일 나중에!
        if (player.equals(target)) {
            // 타겟이 포기한 경우 → 패배 처리 + 추적자 승리
            handleTargetWin(target);

            // 타겟 제외한 나머지 플레이어들에게 승리 처리
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!p.equals(player)) handleTargetWin(p);
            });
        } else {
            // 추적자가 포기한 경우 → 타겟 승리 처리
            handleTargetLose(player);
        }

        HuntCommand.currentTarget = null; // 마지막에 초기화
        return true;
    }

    private void handleTargetWin(Player target) {
        Bukkit.getOnlinePlayers().forEach(player ->
                player.sendMessage(ChatColor.RED + "타겟이 패배하였습니다!"));
        Bukkit.getOnlinePlayers().forEach(player ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.5f, 2));
        target.sendMessage("패배함ㅋ");
    }

    private void handleTargetLose(Player target) {
        Bukkit.getOnlinePlayers().forEach(player -> {
                    // 타겟이 아닌 플레이어들에게만 메시지 전송
                    if (!player.equals(target)) {
                        player.sendMessage(ChatColor.GREEN + "엔더 드래곤이 처치되었습니다!");
                        player.sendMessage(ChatColor.GOLD + "타겟이 승리하였습니다!");
                    }
                });

        // 타겟 플레이어에게는 별도로 승리 메시지
        target.sendMessage(ChatColor.GOLD + "당신이 승리하였습니다!");
        firework(target); // 축하 폭죽
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
                fw.setMetadata("hunt_firework", new FixedMetadataValue(Hunt.getInstance(), true));
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

    private static FireworkEffect.Type getRandomType() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
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
