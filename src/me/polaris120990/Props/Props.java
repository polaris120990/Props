package me.polaris120990.Props;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Props extends JavaPlugin
{
	public final Logger logger = Logger.getLogger("Minecraft");
	static File PlayerDataFile;
	public static FileConfiguration PlayerData;
	public void onEnable()
	{
		PlayerDataFile = new File(getDataFolder(), "player.yml");
		 try
		 {
			 firstRun();
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
		 PlayerData = new YamlConfiguration();
		 loadYamls();
		 PluginDescriptionFile pdfFile = this.getDescription();
		 this.logger.info("[" + pdfFile.getName() + "] v" + pdfFile.getVersion() + " has been enabled.");
	}
	
	public void onDisable()
	{
	    PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() + "] has been disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args)
	{
		readCommand((Player) sender, CommandLabel, args);
		return false;
	}
	
	public void readCommand(Player sender, String command, String[] args)
	{
		if(command.equalsIgnoreCase("prop"))
		{
			if(args.length == 1)
			{
				if(Bukkit.getPlayer(args[0]) == null)
				{
					sender.sendMessage(ChatColor.RED + "Sorry, that player could not be found!");
					return;
				}
				if(Bukkit.getPlayer(args[0]).getName().equalsIgnoreCase(sender.getName()))
				{
					sender.sendMessage(ChatColor.RED + "You cannot give yourself props!");
					return;
				}
				else
				{
					Player[] p = Bukkit.getOnlinePlayers();
					int i = 0;
					while(i < p.length)
					{
						if(Bukkit.getPlayer(args[0]).getName().equalsIgnoreCase(p[i].getName()))
						{
							if(PlayerData.get(p[i].getName()) == null)
							{
								PlayerData.set(p[i].getName(), 1);
								sender.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.AQUA + p[i].getName() + ChatColor.GREEN + " a prop!");
								Bukkit.broadcastMessage(ChatColor.AQUA + sender.getName() + ChatColor.GREEN + " has given " + ChatColor.AQUA + p[i].getName() + ChatColor.GREEN + " a prop!");
								p[i].sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.GREEN + " has given you a prop!");
							}
							else
							{
								int x = PlayerData.getInt(p[i].getName());
								x++;
								PlayerData.set(p[i].getName(), x);
								sender.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.AQUA + p[i].getName() + ChatColor.GREEN + " a prop!");
								Bukkit.broadcastMessage(ChatColor.AQUA + sender.getName() + ChatColor.GREEN + " has given " + ChatColor.AQUA + p[i].getName() + ChatColor.GREEN + " a prop!");
								p[i].sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.GREEN + " has given you a prop!");
								
							}
							saveYamls();
							return;
						}
						i++;
					}
					sender.sendMessage(ChatColor.RED + "The player you are looking for is either not online or doesn't exist");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Usage: /prop (playername)");
			}
		}
		if(command.equalsIgnoreCase("propcheck"))
		{
			if(args.length == 0)
			{
				if(PlayerData.get(sender.getName()) == null)
				{
					sender.sendMessage(ChatColor.AQUA + "You have no props!!");
				}
				else
				{
					Integer q = PlayerData.getInt(sender.getName());
					sender.sendMessage(ChatColor.AQUA + "You currently have " + ChatColor.GOLD + q.toString() + ChatColor.AQUA + " prop(s)!");
				}
			}
			else if(args.length == 1)
			{
				if(Bukkit.getPlayer(args[0]) == null)
				{
					sender.sendMessage(ChatColor.RED + "Sorry, that player could not be found!");
					return;
				}
				String[] p = PlayerData.getKeys(true).toArray(new String[PlayerData.getKeys(true).size()]);
				int i = 0;
				while(i < p.length)
				{
					if(Bukkit.getPlayer(args[0]).getName().equalsIgnoreCase(p[i]))
					{
						Integer q = PlayerData.getInt(Bukkit.getPlayer(args[0]).getName());
						sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(args[0]).getName() + ChatColor.AQUA + " currently has " + ChatColor.GOLD + q.toString() + ChatColor.AQUA + " prop(s)!");		
						return;
					}
					i++;
				}
				sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(args[0]).getName() + ChatColor.AQUA + " has no props!!");
				
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Usage: /propcheck (playername)");
			}
		}
		if(command.equalsIgnoreCase("propclear"))
		{
			if(sender.isOp() == false)
			{
				sender.sendMessage(ChatColor.RED + "You must be an OP to use this command!");
			}
			else
			{
				if(args.length == 1)
				{
					if(Bukkit.getPlayer(args[0]) == null)
					{
						sender.sendMessage(ChatColor.RED + "Sorry, that player could not be found!");
						return;
					}
					String[] p = PlayerData.getKeys(true).toArray(new String[PlayerData.getKeys(true).size()]);
					int i = 0;
					while(i < p.length)
					{
						if(Bukkit.getPlayer(args[0]).getName().equalsIgnoreCase(p[i]))
						{
							PlayerData.set(p[i], null);
							saveYamls();
						}
						i++;
					}
					sender.sendMessage(ChatColor.AQUA + Bukkit.getPlayer(args[0]).getName() + ChatColor.DARK_RED + "'s props have been reset to 0!!");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Usage: /propclear (playername)");
				}
			}
		}
	}
	
	private void firstRun() throws Exception
	{
	    if(!PlayerDataFile.exists()){
	        PlayerDataFile.getParentFile().mkdirs();
	        copy(getResource("player.yml"), PlayerDataFile);
	    }
	}
	
	public static void saveYamls() {
	    try {
	        PlayerData.save(PlayerDataFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void loadYamls() {
	    try {
	        PlayerData.load(PlayerDataFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
