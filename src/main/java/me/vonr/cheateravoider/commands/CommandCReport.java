package me.vonr.cheateravoider.commands;

import java.util.ArrayList;
import java.util.Date;
import me.vonr.cheateravoider.CheaterAvoider;
import me.vonr.cheateravoider.Events;
import me.vonr.cheateravoider.UUIDReport;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandCReport extends CommandBase {

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
            ArrayList<String> reasons = CheaterAvoider.getInstance().constructCheatsArray(args, message);
            String uuid = CheaterAvoider.getInstance().getUUIDFromName(sender, args[0]);
            if (uuid == null) return;

            long timestamp = new Date().getTime();
            Events.reportedUUIDs.put(uuid, new UUIDReport(args[0], timestamp, reasons));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have reported " + args[0]
                + " and will receive warnings about them in-game."));
            Events.saveReportedPlayers();
        }).start();
    }
}