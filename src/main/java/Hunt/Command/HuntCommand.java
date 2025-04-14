package Hunt.Command;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HuntCommand implements CommandExecutor {

    private final Plugin plugin;
    private BukkitRunnable currentTask = null;

    public static Player currentTarget; // 💥 여기에 저장!

    public HuntCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "/hunt <플레이어>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "해당 플레이어를 찾을 수 없습니다.");
            return true;
        }

        World world = target.getWorld(); // 타겟 기준으로 월드 지정
        int borderSize = 8000;

        // 월드보더 설정
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(borderSize);

        // 타이머 시작 (타이틀)
        new BukkitRunnable() {
            int countdown = 3;

            @Override
            public void run() {
                if (countdown <= 0) {
                    // 3초 후 플레이어 산개
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        double x = (Math.random() - 0.5) * borderSize;
                        double z = (Math.random() - 0.5) * borderSize;
                        Location loc = new Location(world, x, world.getHighestBlockYAt((int) x, (int) z) + 1, z);
                        p.teleport(loc);
                        p.sendTitle(ChatColor.GREEN + "게임 시작!", "", 10, 40, 10);
                        p.getInventory().addItem(new ItemStack(Material.COMPASS));
                        giveCustomBook(target);// 책 지급
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        giveCustomBook(p);
                        p = null;
                        Player hunter = (Bukkit.getOnlinePlayers().stream()
                                .filter(player -> !player.equals(target))
                                .findFirst()
                                .orElse(null));
                        if (hunter != null) {
                            giveCustomBook2(hunter);
                        }
                    }

                    // 타겟 저장
                    currentTarget = target;

                    // 나침반 실시간 추적 시작
                    if (currentTask != null) currentTask.cancel();
                    target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3 * 20, 0, false, false));

                    currentTask = new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (!target.isOnline()) {
                                Bukkit.broadcastMessage(ChatColor.RED + "[Hunt] 타겟이 오프라인 상태입니다. 추적을 중단합니다.");
                                cancel();
                                return;
                            }

                            Location targetLoc = target.getLocation();
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.setCompassTarget(targetLoc);
                            }
                        }
                    };
                    currentTask.runTaskTimer(plugin, 0L, 1L);

                    Bukkit.broadcastMessage(ChatColor.YELLOW + "나침반이 " + target.getName() + "을(를) 실시간으로 추적합니다!");

                    cancel();
                    return;
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(String.valueOf(countdown), "", 0, 20, 10);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }

                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 1초 간격

        return true;
    }
    public static void giveCustomBook(Player player) {
        // 책 아이템 생성
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta == null) return;

        // 책 메타데이터 설정
        meta.setTitle("도망자 설명서");
        meta.setAuthor("seamo");
        meta.addPage(
                "도망자는 헌터들에게서 쫓기는 사람입니다.\n" +
                        "도망자는 사망시 인벤토리 한칸이 잠깁니다.\n" +
                        "도망자는 모든 인벤토리 봉인시 패배합니다.\n" +
                        "또한 giveup명령어를 통해서 패배할수있습니다.\n" +
                        "마지막으로 도망자는 엔더드래곤이 처치된다면 (아무나 죽여도 상관 없음)\n" +
                        "승리합니다!.\n"
        );

        book.setItemMeta(meta);

        // 플레이어에게 책 지급
        player.getInventory().addItem(book);
    }

    public static void giveCustomBook2(Player player) {
        // 책 아이템 생성
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta == null) return;

        // 책 메타데이터 설정
        meta.setTitle("헌터 설명서");
        meta.setAuthor("seamo");
        meta.addPage(
                "헌터들은 도망자를 잡는 사람입니다.\n" +
                        "헌터들은 도망자를 40번 이상 잡을시에 승리합니다.\n " +
                        "또한 헌터들은 giveup명령어를 사용하거나 엔더드래곤 사망시 패배합니다"
        );

        book.setItemMeta(meta);
        player.getInventory().addItem(book);
    }
}