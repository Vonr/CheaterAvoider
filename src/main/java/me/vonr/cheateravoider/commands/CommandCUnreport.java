package me.vonr.cheateravoider.commands;

import me.vonr.cheateravoider.CheaterAvoider;
import me.vonr.cheateravoider.Events;
import me.vonr.cheateravoider.Report;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandCUnreport extends CommandBase {

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "cunreport";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /cunreport <playerName>"));
            return;
        }
        for (String name : args) {
            String uuid = CheaterAvoider.getInstance().getUUIDFromName(sender, name);
            if (uuid == null) continue;

            Report report = Events.reportedUUIDs.remove(uuid);
            if (report == null) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found.\n" + EnumChatFormatting.RED + "Did you mean /ccunreport?"));
                return;
            }
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will no longer receive warnings from this player."));
            Events.saveReportedPlayers();
        }

    }
}