package me.vonr.cheateravoider;

import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Events {
    private static int ticks = 0;
    public static HashMap<String, UUIDReport> reportedUUIDs = new HashMap<>();
    public static HashMap<String, Report> reportedNames = new HashMap<>();
    public static File reportsFile;
    public static File nameReportsFile;
    public static ArrayList<NetworkPlayerInfo> networkPlayerInfoMap;

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiDownloadTerrain) {
            ticks = 0;
        }
    }

    public String formatTimestamp(long epoch) {
        Calendar calendar = new Calendar.Builder().setInstant(epoch).build();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return calendar.get(Calendar.HOUR_OF_DAY) + "h" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "; " + (dayOfWeek == 1 ? 8 : dayOfWeek) + " | " + calendar.get(
            Calendar.DATE) + "/" + (calendar.get(
            Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap() != null) {
            if (networkPlayerInfoMap != null) {
                for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                    if (networkPlayerInfoMap.contains(info)) continue;
                    this.onPlayerJoin(info.getGameProfile());
                }
            }
            networkPlayerInfoMap = new ArrayList<>(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap());
        }
        if (!Minecraft.getMinecraft().inGameHasFocus) {
            return;
        }
        if (ticks >= 600) {
            this.warnCheaters();
            ticks = 0;
            return;
        }
        ++ticks;
    }

    public void onPlayerJoin(GameProfile gameProfile) {
        if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        }
        if (gameProfile.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
            this.warnCheaters();
            return;
        }
        Report report = reportedUUIDs.get(gameProfile.getId().toString().replace("-", ""));
        boolean cracked = false;
        if (report == null) {
            report = reportedNames.get(gameProfile.getName());
            if (report == null) {
                return;
            }
            cracked = true;
        } else {
            ((UUIDReport) report).username = gameProfile.getName();
        }
        String playerName = gameProfile.getName();
        StringBuilder message = new StringBuilder("[\"\",\"" + EnumChatFormatting.RED + "WARNING: " + EnumChatFormatting.GRAY + "One player you reported, \",{\"text\":\"" + playerName + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + EnumChatFormatting.RED + playerName + "\n" + EnumChatFormatting.GRAY + "Reported at: " + EnumChatFormatting.RESET + this.formatTimestamp(report.timestamp) + "\n" + EnumChatFormatting.GRAY + "Reported for: ");
        boolean isNotFirst = false;
        for (String reason : report.reasons) {
            if (isNotFirst) {
                message.append(", ");
            }
            message.append(EnumChatFormatting.RESET).append(reason).append(EnumChatFormatting.GRAY);
            isNotFirst = true;
        }
        if (cracked) {
            message.append("\n\n" + EnumChatFormatting.YELLOW + "Click to stop warning about this player.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ccunreport ").append(gameProfile.getName()).append("\"}}, \"" + EnumChatFormatting.GRAY + ", has just joined.\"]");
        } else {
            message.append("\n\n" + EnumChatFormatting.YELLOW + "Click to stop warning about this player.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cunreport ").append(gameProfile.getId().toString().replace("-", "")).append("\"}}, \"" + EnumChatFormatting.GRAY + ", has just joined.\"]");
        }
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(Serializer.jsonToComponent(message.toString()));
    }

    public static void saveReportedPlayers() {
        try {
            BufferedWriter uuidWriter = new BufferedWriter(new FileWriter(reportsFile));
            BufferedWriter nameWriter = new BufferedWriter(new FileWriter(nameReportsFile));
            for (String uuid : reportedUUIDs.keySet()) {
                UUIDReport report = reportedUUIDs.get(uuid);
                uuidWriter.write(uuid + " " + report.username + " " + report.timestamp);
                for (String reason : report.reasons) {
                    uuidWriter.write(" " + reason);
                }
                uuidWriter.write("\n");
            }
            for (String name : reportedNames.keySet()) {
                Report report = reportedNames.get(name);
                nameWriter.write(name + " " + report.timestamp);
                for (String reason : report.reasons) {
                    nameWriter.write(" " + reason);
                }
                nameWriter.write("\n");
            }
            uuidWriter.close();
            nameWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadReportedPlayers() {
        if (!reportsFile.exists()) {
            return;
        }
        try {
            ArrayList<String> reasons;
            String[] split;
            BufferedReader uuidReader = new BufferedReader(new FileReader(reportsFile));
            BufferedReader nameReader = new BufferedReader(new FileReader(nameReportsFile));
            long timestamp = 0L;
            String line = uuidReader.readLine();
            while (line != null) {
                block11: {
                    split = line.split(" ");
                    if (split.length >= 4) {
                        try {
                            timestamp = Long.parseLong(split[1]);
                        }
                        catch (Exception e2) {
                            break block11;
                        }
                        reasons = new ArrayList<>(Arrays.asList(split).subList(2, split.length));
                        String name = split[1];
                        reportedUUIDs.put(split[0], new UUIDReport(name, timestamp, reasons));
                    }
                }
                line = uuidReader.readLine();
            }
            line = nameReader.readLine();
            while (line != null) {
                block12: {
                    split = line.split(" ");
                    if (split.length >= 3) {
                        try {
                            timestamp = Long.parseLong(split[1]);
                        }
                        catch (Exception e2) {
                            break block12;
                        }
                        reasons = new ArrayList<String>(Arrays.asList(split).subList(2, split.length));
                        reportedNames.put(split[0], new Report(timestamp, reasons));
                    }
                }
                line = nameReader.readLine();
            }
            uuidReader.close();
            nameReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void warnCheaters() {
        try {
            StringBuilder message = new StringBuilder("[\"\",\"" + EnumChatFormatting.RED + "WARNING: " + EnumChatFormatting.GRAY + " Your game has some players you have reported:" + EnumChatFormatting.RESET + " \"");
            boolean hasPlayer = false;
            for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                GameProfile p = networkPlayerInfo.getGameProfile();
                Report report = reportedUUIDs.get(p.getId().toString().replace("-", ""));
                boolean cracked = false;
                if (report == null) {
                    report = reportedNames.get(p.getName());
                    if (report == null) continue;
                    cracked = true;
                }
                hasPlayer = true;
                String playerName = p.getName();
                message.append(",{\"text\":\"\n" + playerName + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + EnumChatFormatting.RED + playerName + "\n" + EnumChatFormatting.GRAY + "Reported at: " + EnumChatFormatting.RESET + this.formatTimestamp(report.timestamp) + "\n" + EnumChatFormatting.RED + "Reported for: ");
                boolean isNotFirst = false;
                for (String reason : report.reasons) {
                    if (isNotFirst) {
                        message.append(", ");
                    }
                    message.append(EnumChatFormatting.RESET).append(reason).append(EnumChatFormatting.GRAY);
                    isNotFirst = true;
                }
                if (cracked) {
                    message.append("\n\n" + EnumChatFormatting.YELLOW + "Click to stop warning about this player.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ccunreport ").append(p.getName()).append("\"}}");
                    continue;
                }
                message.append("\n\n" + EnumChatFormatting.YELLOW + "Click to stop warning about this player.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cunreport ").append(p.getId().toString().replace("-", "")).append("\"}}");
            }
            message.append(",{\"text\":\"\n\n" + EnumChatFormatting.YELLOW + "It is recommended that you leave this game.\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/leave\"}}]");
            if (hasPlayer) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(Serializer.jsonToComponent(message.toString()));
            }
        }
        catch (Exception ignored) {
        }
    }
}
