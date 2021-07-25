package me.vonr.cheateravoider.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.vonr.cheateravoider.Events;
import me.vonr.cheateravoider.UUIDReport;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandCReport extends CommandBase {

    private static final Pattern pattern = Pattern.compile("[0-9a-f]{32}");

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "creport";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Usage: /creport <playerName> <reasons...>"));
            return;
        }
        new Thread(() -> {
            StringBuilder message = new StringBuilder("/creport " + args[0]);
            ArrayList<String> reasons = CommandCReport.constructCheatsArray(args, message);
            String uuid = getUUIDFromName(sender, args[0]);
            if (uuid == null) return;

            long timestamp = new Date().getTime();
            Events.reportedUUIDs.put(uuid, new UUIDReport(args[0], timestamp, reasons));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have reported " + args[0]
                + " and will receive warnings about them in-game."));
            Events.saveReportedPlayers();
        }).start();
    }

    public static String getUUIDFromName(ICommandSender sender, String name) {
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

    public static ArrayList<String> constructCheatsArray(String[] args, StringBuilder message) {
        ArrayList<String> reasons = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            reasons.add(args[i]);
            message.append(" ").append(args[i]);
        }
        return reasons;
    }
}