package me.vonr.cheateravoider;

import java.io.File;
import me.vonr.cheateravoider.commands.CommandCCReport;
import me.vonr.cheateravoider.commands.CommandCCUnreport;
import me.vonr.cheateravoider.commands.CommandCReport;
import me.vonr.cheateravoider.commands.CommandCUnreport;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = CheaterAvoider.MOD_ID, name = CheaterAvoider.MOD_NAME, version = CheaterAvoider.MOD_VERSION, clientSideOnly = true)
public class CheaterAvoider {

    public static final String MOD_NAME = "${GRADLE_MOD_NAME}";
    public static final String MOD_ID = "${GRADLE_MOD_ID}";
    public static final String MOD_VERSION = "${GRADLE_MOD_VERSION}";

    @Mod.Instance(CheaterAvoider.MOD_ID)
    private static CheaterAvoider instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Events());
        ClientCommandHandler cch = ClientCommandHandler.instance;
        cch.registerCommand(new CommandCCReport());
        cch.registerCommand(new CommandCCUnreport());
        cch.registerCommand(new CommandCReport());
        cch.registerCommand(new CommandCUnreport());
        Events.reportsFile = new File(Minecraft.getMinecraft().mcDataDir, "uuidreports.txt");
        Events.nameReportsFile = new File(Minecraft.getMinecraft().mcDataDir, "namereports.txt");
        Runtime.getRuntime().addShutdownHook(new Thread(Events::saveReportedPlayers));
        Events.loadReportedPlayers();
    }

    public static CheaterAvoider getInstance() {
        return instance;
    }

}
