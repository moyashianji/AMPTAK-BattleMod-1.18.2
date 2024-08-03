package com.example.examplemod;

import com.example.TimeSyncPacket;
import com.example.config.HealthconfigConfiguration;
import com.example.init.ConfigInit;
import com.example.init.IjijModItems;
import com.example.init.NetworkInit;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("untitled7")
public class ExampleMod
{
    // Directly reference a slf4j logger
    public static int timer = HealthconfigConfiguration.GAMETIME.get();
    private int tickCounter = 0;  // 追加：ティックカウンター

    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean gameRunning = false;
    private Set<ServerPlayer> playersInGame = new HashSet<>();
    private Objective sidebarObjective;
    private Random random = new Random();
    private static final LevelResource SERVER_CONFIG_FOLDER = new LevelResource("serverconfig");
    private static final String CONFIG_FILE_NAME = "game.config";
    private static final int DEFAULT_TIME = 300;
    private static final int DEFAULT_RADIUS = 1000;
    private static final int DEFAULT_X = 100;
    private static final int DEFAULT_Y = 160;
    private static final int DEFAULT_Z = 200;
    private static int time = DEFAULT_TIME;
    private int radius = DEFAULT_RADIUS;
    private int xx = DEFAULT_X;
    private int yy = DEFAULT_Y;
    private int zz = DEFAULT_Z;
    private  int COUNTDOWN_TIME =  time;
    public static int CLIENT_TIME = time;

    private  final int INITIAL_BORDER_SIZE = 1000;
    private  final int FINAL_BORDER_SIZE = 2;
    private int remainingTime = COUNTDOWN_TIME;
    public ExampleMod()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IjijModItems.REGISTRY.register(bus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        NetworkInit.registerMessages();
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("game")
                .then(Commands.literal("join").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    joinGame(player);
                    return 1;
                }))
                .then(Commands.literal("start").executes(context -> {
                    if (!gameRunning && playersInGame.size() >= 1) {
                        startGame(context.getSource().getServer());
                        return 1;
                    } else {
                        context.getSource().sendFailure(new TextComponent("ゲームを開始するには、少なくとも2人のプレイヤーが必要です。"));
                        return 0;
                    }
                }))
                .then(Commands.literal("reset").executes(context -> {
                    resetGame(context.getSource().getServer());
                    return 1;
                }))
                .then(Commands.literal("reload").executes(context -> {
                    loadConfig(context.getSource().getServer());
                    syncTimeWithClients(context.getSource().getServer());

                    context.getSource().sendSuccess(new TextComponent("設定ファイルをリロードしました。"), true);
                    return 1;
                }))
        );
    }
    private void loadConfig(MinecraftServer server) {
        File configFolder = server.getWorldPath(SERVER_CONFIG_FOLDER).toFile();
        File configFile = new File(configFolder, CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            time = Integer.parseInt(reader.readLine().split(":")[1].trim());
            radius = Integer.parseInt(reader.readLine().split(":")[1].trim());
            xx = Integer.parseInt(reader.readLine().split(":")[1].trim());
            yy = Integer.parseInt(reader.readLine().split(":")[1].trim());
            zz = Integer.parseInt(reader.readLine().split(":")[1].trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultConfig(File configFile) {
        try {
            if (configFile.getParentFile() != null) {
                configFile.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write("時間:" + DEFAULT_TIME);
                writer.newLine();
                writer.write("半径:" + DEFAULT_RADIUS);
                writer.newLine();
                writer.write("x:" + DEFAULT_X);
                writer.newLine();
                writer.write("y:" + DEFAULT_Y);
                writer.newLine();
                writer.write("z:" + DEFAULT_Z);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinGame(ServerPlayer player) {
        Scoreboard scoreboard = player.getLevel().getScoreboard();
        Team team = scoreboard.getPlayerTeam(player.getScoreboardName());
        if (team == null) {
            team = scoreboard.addPlayerTeam("gameTeam");
            scoreboard.addPlayerToTeam(player.getScoreboardName(), (PlayerTeam) team);
            playersInGame.add(player);
            player.sendMessage(new TextComponent("ゲームに参加しました。"), player.getUUID());
        }

        // サイドバーに表示を強制する
        if (sidebarObjective == null) {
            Component a = new TextComponent("A")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00)).withBold(true).withItalic(true));  // 赤色で太字
            Component m = new TextComponent("M")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xB376FF)).withBold(true).withItalic(true));  // 赤色で太字
            Component p = new TextComponent("P")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x8CD732)).withBold(true).withItalic(true));  // 赤色で太字
            Component t = new TextComponent("T")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x2DBEF7)).withBold(true).withItalic(true));  // 赤色で太字
            Component aa = new TextComponent("A")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFC4645)).withBold(true).withItalic(true));  // 赤色で太字
            Component k = new TextComponent("K")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF61F3)).withBold(true).withItalic(true));  // 赤色で太字
            Component gun = new TextComponent("銃撃戦")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withBold(true).withItalic(true));  // 赤色で太字
            // コンポーネントを連結
            Component combined = new TextComponent("")
                    .append(a)
                    .append(m)
                    .append(p)
                    .append(t)
                    .append(aa)
                    .append(k)
                    .append(gun);
            sidebarObjective = scoreboard.addObjective("sidebar", ObjectiveCriteria.DUMMY,  combined, ObjectiveCriteria.RenderType.INTEGER);
        }
        scoreboard.setDisplayObjective(1, sidebarObjective);

        updateSidebar(scoreboard);
    }

    private void startGame(MinecraftServer server) {
        gameRunning = true;
        remainingTime = COUNTDOWN_TIME;

        WorldBorder worldBorder = server.overworld().getWorldBorder();
        worldBorder.setCenter(xx, zz);
        worldBorder.setSize(radius);  // 初期半径1000ブロック
        worldBorder.lerpSizeBetween(worldBorder.getSize(), 20,  time * 1000L);  // ゲーム時間に応じて縮小
        // プレイヤーをランダムな位置にテレポート
        // プレイヤーをランダムな位置にテレポート
        for (ServerPlayer player : playersInGame) {
            double x = xx + (random.nextDouble() * radius - radius/2); // 中心から-1000から+1000の範囲
            double z =  zz + (random.nextDouble() * radius - radius/2); // 中心から-1000から+1000の範囲
            double y = 160; // 高さ160

            player.teleportTo(server.overworld(), x, y, z, player.getYRot(), player.getXRot());
            player.sendMessage(new TextComponent("ランダムな位置にテレポートされました: " + x + ", " + y + ", " + z), player.getUUID());
        }

        MinecraftForge.EVENT_BUS.register(this);

        updateSidebar(server.getScoreboard());
    }

    private void resetGame(MinecraftServer server) {
        gameRunning = false;
        remainingTime = COUNTDOWN_TIME;

        for (ServerPlayer player : playersInGame) {
            player.teleportTo(server.overworld(), xx, yy, zz, player.getYRot(), player.getXRot());
        }
        WorldBorder worldBorder = server.overworld().getWorldBorder();
        worldBorder.setCenter(xx, zz);
        worldBorder.setSize(radius);  // 初期半径1000ブロックに戻す

        playersInGame.clear();
        MinecraftForge.EVENT_BUS.unregister(this);

        Scoreboard scoreboard = server.getScoreboard();
        if (sidebarObjective != null) {
            scoreboard.removeObjective(sidebarObjective);
            sidebarObjective = null;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (gameRunning && event.phase == TickEvent.Phase.START) {
            tickCounter++;
            if (tickCounter >= 20) {  // 1秒経過
                tickCounter = 0;
                if (remainingTime > 0) {
                    remainingTime--;
                    syncTimeWithClients(ServerLifecycleHooks.getCurrentServer());

                    updateSidebar(ServerLifecycleHooks.getCurrentServer().getScoreboard());
                } else {

                    endGame("終了");
                }
            }
        }
    }
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            if (playersInGame.contains(player)) {
                player.setGameMode(GameType.SPECTATOR);
                playersInGame.remove(player);
                updateSidebar(player.getLevel().getScoreboard());

                if (playersInGame.size() == 1) {
                    ServerPlayer winner = playersInGame.iterator().next();


                    endGame(winner.getName().getString() + " の勝利！");
                }
            }
        }
    }

    private void endGame(String message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> player.sendMessage(new TextComponent(message), player.getUUID()));
            Component title = new TextComponent(message)
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555)).withBold(true)); // 赤色で太字
            server.getPlayerList().getPlayers().forEach(player -> player.connection.send(new ClientboundSetTitleTextPacket(title)));
            server.getPlayerList().getPlayers().forEach(player -> player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20))); // タイトルのフェードイン、表示、フェードアウトの時間を設定

        }
        resetGame(server);
    }

    private void resetPlayerScores(Scoreboard scoreboard) {
        scoreboard.resetPlayerScore("――残り時間――", sidebarObjective);
        scoreboard.resetPlayerScore("――残り人数――", sidebarObjective);
        for (int i = 0; i <= COUNTDOWN_TIME; i++) {
            scoreboard.resetPlayerScore("残り: " + i + "秒", sidebarObjective);
        }
        for (int i = 0; i <= 6; i++) {
            scoreboard.resetPlayerScore("人数: " + i, sidebarObjective);
        }
    }
    private void updateSidebar(Scoreboard scoreboard) {
        if (sidebarObjective != null) {
            resetPlayerScores(scoreboard);
            Component timeLabel = new TextComponent("――残り時間――")
                    .setStyle(Style.EMPTY.withBold(true)); // 太字
            // "残り時間" 行を追加
            Score timeLabelScore = scoreboard.getOrCreatePlayerScore(timeLabel.getString(), sidebarObjective);
            timeLabelScore.setScore(3);

            // 残り時間の秒数
            Score timeValueScore = scoreboard.getOrCreatePlayerScore("残り: " + remainingTime  + "秒", sidebarObjective);
            timeValueScore.setScore(2);

            Component playercount = new TextComponent("――残り人数――")
                    .setStyle(Style.EMPTY.withBold(true)); // 太字
            // "残り人数" 行を追加
            Score playersLabelScore = scoreboard.getOrCreatePlayerScore(playercount.getString(), sidebarObjective);
            playersLabelScore.setScore(1);

            // 残り人数
            Score playersValueScore = scoreboard.getOrCreatePlayerScore("人数: " + playersInGame.size(), sidebarObjective);
            playersValueScore.setScore(0);
        }
    }
    public static void updateClientSidebar() {
        Minecraft.getInstance().execute(() -> {
            Scoreboard scoreboard = Minecraft.getInstance().level.getScoreboard();
            Objective sidebarObjective = scoreboard.getObjective("sidebar");
            if (sidebarObjective != null) {
                // サイドバーの"残り時間"の行を更新
                scoreboard.getOrCreatePlayerScore("残り: " + CLIENT_TIME + "秒", sidebarObjective).setScore(2);
            }
        });
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        loadConfig(event.getServer());
        syncTimeWithClients(event.getServer());

        LOGGER.info("HELLO from server starting");
    }

    private void syncTimeWithClients(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkInit.INSTANCE.sendTo(new TimeSyncPacket(remainingTime), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            System.out.println(CLIENT_TIME);
        }
    }
    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
