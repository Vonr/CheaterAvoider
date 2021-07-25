package me.vonr.cheateravoider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.vonr.cheateravoider.commands.CommandCCReport;
import me.vonr.cheateravoider.commands.CommandCCUnreport;
import me.vonr.cheateravoider.commands.CommandCCReportList;
import me.vonr.cheateravoider.commands.CommandCReport;
import me.vonr.cheateravoider.commands.CommandCUnreport;
import me.vonr.cheateravoider.commands.CommandCReportList;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CheaterAvoider.MOD_ID, name = CheaterAvoider.MOD_NAME, version = CheaterAvoider.MOD_VERSION, clientSideOnly = true)
public class CheaterAvoider {

    public static final String MOD_NAME = "${GRADLE_MOD_NAME}";
    public static final String MOD_ID = "${GRADLE_MOD_ID}";
    public static final String MOD_VERSION = "${GRADLE_MOD_VERSION}";

    private static final Pattern pattern = Pattern.compile("[0-9a-f]{32}");

    @Mod.Instance(CheaterAvoider.MOD_ID)
    private static CheaterAvoider instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        Events.reportsFile = new File(event.getModConfigurationDirectory(), "uuidreports.txt");
        Events.nameReportsFile = new File(event.getModConfigurationDirectory(), "namereports.txt");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Events());
        ClientCommandHandler cch = ClientCommandHandler.instance;
        cch.registerCommand(new CommandCCReport());
        cch.registerCommand(new CommandCCUnreport());
        cch.registerCommand(new CommandCCReportList());
        cch.registerCommand(new CommandCReport());
        cch.registerCommand(new CommandCUnreport());
        cch.registerCommand(new CommandCReportList());

        Runtime.getRuntime().addShutdownHook(new Thread(Events::saveReportedPlayers));
        Events.loadReportedPlayers();
    }

    public static CheaterAvoider getInstance() {
        return instance;
    }

    public String getUUIDFromName(ICommandSender sender, String name) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                "https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status == 204) {
                sender.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "Cannot find player with username " + name
                        + "."));
                return null;
            }
            if (status != 200) {
                sender.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "Could not retrieve " + name + "'s information."));
                return null ;
            }
            Matcher matcher = pattern.matcher(
                new BufferedReader(new InputStreamReader(connection.getInputStream()))
                    .readLine());
            if (matcher.find()) {
                return matcher.group();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Could not retrieve " + name + "'s information."));
            return null;
        }
        return null;
    }

    public ArrayList<String> constructCheatsArray(String[] args, StringBuilder message) {
        ArrayList<String> reasons = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            reasons.add(args[i]);
            message.append(" ").append(args[i]);
        }
        return reasons;
    }

}
