package net.ase.climbers_dlc;

import com.mojang.logging.LogUtils;
import net.ase.climbers_dlc.config.DOTFClimbersDLCConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

/*
@Author = ASEStefan
 */

@Mod(DOTFClimbersDLC.MODID)
public class DOTFClimbersDLC
{
    public static final String MODID = "dotf_climbers_dlc";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DOTFClimbersDLC()
    {

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DOTFClimbersDLCConfig.SERVER_SPEC ,"dotf_climbers_dlc_config.toml");
        DOTFClimbersDLCConfig.loadConfig(DOTFClimbersDLCConfig.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("dotf_climbers_dlc_config.toml").toString());

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }
}