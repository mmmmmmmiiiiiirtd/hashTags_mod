package io.github.mmmmmmmiiiiiirtd;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "hashtags";

    private static final Logger LOGGER = LogManager.getLogger();

    public Main(IEventBus modEventBus){
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event){
        LOGGER.info("Start mod Hash Tags");
    }
}