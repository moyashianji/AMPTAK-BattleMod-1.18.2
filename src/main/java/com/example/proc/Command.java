package com.example.proc;

import com.example.config.HealthconfigConfiguration;
import com.example.examplemod.ExampleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


public class Command {
/**
    public static int timer = HealthconfigConfiguration.GAMETIME.get();
    private static boolean isTicking = false;

    public static boolean GAMEFLAG = false;
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gamestart")

                .executes(arguments -> {
                    ServerLevel world = arguments.getSource().getLevel();

                    double x = arguments.getSource().getPosition().x();
                    double y = arguments.getSource().getPosition().y();
                    double z = arguments.getSource().getPosition().z();


                    if(GAMEFLAG == false) {
                        WorldBorder worldBorder = world.getWorldBorder();
                        worldBorder.setCenter(x, y);
                        worldBorder.setSize(HealthconfigConfiguration.RADIUS.get());

                        worldBorder.lerpSizeBetween(worldBorder.getSize(), 10, HealthconfigConfiguration.GAMETIME.get() * 1000L);
                        timer = HealthconfigConfiguration.GAMETIME.get();
                        GAMEFLAG = true;

                    }
                    return 0;
                }));
        event.getDispatcher().register(Commands.literal("join")

                .executes(arguments -> {
                    ServerLevel world = arguments.getSource().getLevel();

                    Entity entity = arguments.getSource().getEntity();

                    Entity _entityTeam = entity;
                    PlayerTeam _pt = _entityTeam.level.getScoreboard().getPlayerTeam("gameplayer");
                    if (_pt != null) {
                        if (_entityTeam instanceof Player _player)
                            _entityTeam.level.getScoreboard().addPlayerToTeam(_player.getGameProfile().getName(), _pt);
                        else
                            _entityTeam.level.getScoreboard().addPlayerToTeam(_entityTeam.getStringUUID(), _pt);
                    }

                    return 0;
                }));
        event.getDispatcher().register(Commands.literal("gameset")

                .executes(arguments -> {
                    ServerLevel worldd = arguments.getSource().getLevel();

                    Entity entity = arguments.getSource().getEntity();

                    if(GAMEFLAG == true) {
                        Direction direction = entity.getDirection();

                        MinecraftServer server = worldd.getServer();

                        Scoreboard scoreboard = server.getScoreboard();

                        Objective objective = scoreboard.getObjective("TeamCount");

                        server.getScoreboard().removeObjective(objective);


                        GAMEFLAG = false;

                        Entity _entityTeam = entity;
                        PlayerTeam _pt = worldd.getScoreboard().getPlayerTeam("gameplayer");
                        if (_pt != null)
                            worldd.getScoreboard().removePlayerTeam(_pt);
                        new Object() {
                            private int ticks = 0;
                            private float waitTicks;
                            private LevelAccessor world;

                            public void start(LevelAccessor world, int waitTicks) {
                                this.waitTicks = waitTicks;
                                MinecraftForge.EVENT_BUS.register(this);
                                this.world = world;
                            }

                            @SubscribeEvent
                            public void tick(TickEvent.ServerTickEvent event) {
                                if (event.phase == TickEvent.Phase.END) {

                                    this.ticks += 1;

                                    if (this.ticks >= this.waitTicks)
                                        run();
                                }
                            }

                            private void run() {
                                Scoreboard scoreboard = server.getScoreboard();
                                Objective objective = scoreboard.getObjective("TeamCount");

                                //scoreboard.removeObjective(objective);

                                if (objective == null) {
                                    objective = scoreboard.addObjective("TeamCount", ObjectiveCriteria.DUMMY, Component.nullToEmpty(""), ObjectiveCriteria.RenderType.INTEGER);
                                }

                                scoreboard.setDisplayObjective(1, objective); // 1 is the display slot for sidebar

                                Score playerCountScoree = scoreboard.getOrCreatePlayerScore("残り時間（秒）:", objective);

                                playerCountScoree.setScore(HealthconfigConfiguration.GAMETIME.get());

                                worldd.getScoreboard().addPlayerTeam("gameplayer");

                                MinecraftForge.EVENT_BUS.unregister(this);

                            }
                        }.start(worldd, 20);
                    }

                        return 0;

                }));

    }
    private static int tickss = 0;


    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective("TeamCount");

        //scoreboard.removeObjective(objective);

        if (objective == null) {
            objective = scoreboard.addObjective("TeamCount", ObjectiveCriteria.DUMMY, Component.nullToEmpty(""), ObjectiveCriteria.RenderType.INTEGER);
        }

        scoreboard.setDisplayObjective(1, objective); // 1 is the display slot for sidebar
    }


    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            if(!event.world.isClientSide) {


                MinecraftServer server = event.world.getServer();

                Scoreboard scoreboard = server.getScoreboard();
                Objective objective = scoreboard.getObjective("TeamCount");

                if (objective != null) {
                    //Score score = scoreboard.getOrCreatePlayerScore("残り人数:", objective);
                    //score.setScore(1);

                    int playerCount = 0;
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (player.getTeam() != null && player.getTeam().getName().equals("gameplayer")) {
                            playerCount++;
                        }
                    }

                    Score playerCountScore = scoreboard.getOrCreatePlayerScore("残り人数:", objective);
                    Score playerCountScoree = scoreboard.getOrCreatePlayerScore("残り時間（秒）:", objective);

                    playerCountScore.setScore(playerCount);

                    if (GAMEFLAG == true) {
                        if(tickss%60 == 0) {
                            timer--;
                            playerCountScoree.setScore(timer);

                            if (timer == 0) {
                                GAMEFLAG = false;
                                timer = HealthconfigConfiguration.GAMETIME.get();

                            }
                        }


                    }
                    if(!event.world.isClientSide) {

                        tickss++;
                    }

                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!event.getEntity().level.isClientSide) {
                ServerPlayer player = (ServerPlayer) event.getEntity();

                Scoreboard scoreboard = player.getScoreboard();
                if (player.getTeam() != null) {
                    System.out.println(player.getTeam());
                    scoreboard.removePlayerFromTeam(player.getName().getString(), (PlayerTeam) player.getTeam());
                    player.setGameMode(GameType.SPECTATOR);

                }
            }
        }
    }
**/
}
