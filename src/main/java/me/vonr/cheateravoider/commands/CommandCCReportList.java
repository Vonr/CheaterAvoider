package me.vonr.cheateravoider.commands;

import me.vonr.cheateravoider.Events;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandCCReportList extends CommandBase {

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ccreportlist";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        new Thread(() -> {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Players you have reported:"));
            StringBuilder message = new StringBuilder("");
            Events.reportedNames.forEach((name, report) -> {
                message.append(EnumChatFormatting.RESET + name + " ");
            });
            sender.addChatMessage(new ChatComponentText(message.toString()));
        }).start();
    }
}
