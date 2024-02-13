package me.loving11ish.clans.commands.commandTabCompleters;

import me.loving11ish.clans.utils.ClansStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClanAdminTabCompleter implements TabCompleter {

    private final List<String> arguments = new ArrayList<>();

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (arguments.isEmpty()){
            arguments.add("save");
            arguments.add("reload");
            arguments.add("disband");
            arguments.add("about");

        }

        List<String> result = new ArrayList<>();
        List<String> result2 = new ArrayList<>();
        List<String> result3 = new ArrayList<>();

        if (args.length == 1){
            for (String a : arguments){
                if (a.toLowerCase().startsWith(args[0].toLowerCase())){
                    result.add(a);
                }
            }
            return result;
        }

        else if (args.length == 2) {
            List<String> onlinePlayerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> onlinePlayerNames.add(player.getName()));
            onlinePlayerNames.add("byclanname:");

            for (String a : onlinePlayerNames){
                if (a.toLowerCase().startsWith(args[1].toLowerCase())){
                    result2.add(a);
                }
            }
            return result2;
        }

        else if (args.length == 3) {
            List<String> clanNames = new ArrayList<>();
            ClansStorageUtil.getClanList().forEach(clan -> clanNames.add(clan.getClanFinalName()));

            for (String a : clanNames){
                if (a.toLowerCase().startsWith(args[2].toLowerCase())){
                    result3.add(a);
                }
            }

            return result3;
        }
        return null;
    }
}
