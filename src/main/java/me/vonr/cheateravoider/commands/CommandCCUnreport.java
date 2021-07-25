package me.vonr.cheateravoider.commands;

import me.vonr.cheateravoider.Events;
import me.vonr.cheateravoider.Report;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandCCUnreport extends CommandBase {

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ccunreport";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Usage: /ccunreport <playerName>"));
            return;
        }
        for (String arg : args) {
            Report report = Events.reportedNames.remove(arg);
            if (report == null) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
                return;
            }
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will no longer receive warnings for " + arg + "."));
            Events.saveReportedPlayers();
        }
    }
}