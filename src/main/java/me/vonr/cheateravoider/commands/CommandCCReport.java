package me.vonr.cheateravoider.commands;

import java.util.ArrayList;
import java.util.Date;
import me.vonr.cheateravoider.CheaterAvoider;
import me.vonr.cheateravoider.Events;
import me.vonr.cheateravoider.Report;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandCCReport extends CommandBase {

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ccreport";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Usage: /ccreport <playerName> <reasons...>"));
            return;
        }
        StringBuilder message = new StringBuilder("/ccreport " + args[0]);
        ArrayList<String> cheats = CheaterAvoider.getInstance().constructCheatsArray(args, message);
        long timestamp = new Date().getTime();
        Events.reportedNames.put(args[0], new Report(timestamp, cheats));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GREEN + "You have reported " + args[0] + " and will receive warnings about them in-game."));
        Events.saveReportedPlayers();
    }
}