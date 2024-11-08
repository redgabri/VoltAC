package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.manager.init.start.SuperDebug;
import com.volt.voltac.utils.anticheat.LogUtil;
import com.volt.voltac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.command.CommandSender;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@CommandAlias("grim|grimac")
public class GrimLog extends BaseCommand {

    @Subcommand("log|logs")
    @CommandPermission("grim.log")
    @CommandAlias("gl")
    public void onLog(CommandSender sender, int flagId) {
        StringBuilder builder = SuperDebug.getFlag(flagId);

        if (builder == null) {
            String failure = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-not-found", "%prefix% &cUnable to find that log");
            sender.sendMessage(MessageUtil.format(failure));
        } else {
            String uploading = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-start", "%prefix% &fUploading log... please wait");
            String success = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%");
            String failure = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-upload-failure", "%prefix% &cSomething went wrong while uploading this log, see console for more information.");

            sender.sendMessage(MessageUtil.format(uploading));

            FoliaScheduler.getAsyncScheduler().runNow(VoltAPI.INSTANCE.getPlugin(), (dummy) -> {
                try {
                    URL mUrl = new URL("https://paste.grim.ac/data/post");
                    HttpURLConnection urlConn = (HttpURLConnection) mUrl.openConnection();
                    urlConn.setDoOutput(true);
                    urlConn.setRequestMethod("POST");
                    urlConn.addRequestProperty("User-Agent", "GrimAC/" + VoltAPI.INSTANCE.getExternalAPI().getGrimVersion());
                    urlConn.addRequestProperty("Content-Type", "text/yaml"); // Not really yaml, but looks nicer than plaintext
                    urlConn.setRequestProperty("Content-Length", Integer.toString(builder.length()));
                    urlConn.getOutputStream().write(builder.toString().getBytes(StandardCharsets.UTF_8));

                    urlConn.getOutputStream().close();

                    int response = urlConn.getResponseCode();

                    if (response == HttpURLConnection.HTTP_CREATED) {
                        String responseURL = urlConn.getHeaderField("Location");
                        sender.sendMessage(MessageUtil.format(success.replace("%url%", "https://paste.grim.ac/" + responseURL)));
                    } else {
                        sender.sendMessage(MessageUtil.format(failure));
                        LogUtil.error("Returned response code " + response + ": " + urlConn.getResponseMessage());
                    }

                    urlConn.disconnect();
                } catch (Exception e) {
                    sender.sendMessage(MessageUtil.format(failure));
                    e.printStackTrace();
                }
            });
        }
    }
}
