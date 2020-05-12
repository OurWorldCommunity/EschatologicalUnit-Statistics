package online.smyhw.EschatologicalUnit.Statistics;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class smyhw extends JavaPlugin implements Listener 
{
	public static JavaPlugin smyhw_;
	public static Logger loger;
	public static FileConfiguration configer;
	public static List cmd_pre_Wave;
	public static String prefix;
	@Override
    public void onEnable() 
	{
		
		getLogger().info("EschatologicalUnit.Statistics加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		saveDefaultConfig();
		configer = getConfig();
		smyhw_ = this;
		getLogger().info("正在加载配置...");
		cmd_pre_Wave = configer.getStringList("config.cmd_pre_Wave");
		prefix = configer.getString("config.prefix");
		saveConfig();
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("EschatologicalUnit.Statistics加载完成");
    }

	@Override
    public void onDisable() 
	{
		getLogger().info(prefix+"EschatologicalUnit.Statistics卸载");
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if (cmd.getName().equals("euSt"))
        {
                if(!sender.hasPermission("eu.plugin")) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{权限不足}");
                	return true;
                }
                if(args.length<1) {LPS(sender,cmd.getName());return true;}
                switch(args[0])
                {
                case"start":
                	//侧边栏
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives remove side" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add side dummy §nOurWorld_|_末日小队" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set ______ side -16" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set EschatologicalUnit side -17" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set 波数:0 side -12" );
                	ChangeMoney("smyhw",0);
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setdisplay sidebar side" );
                	//end 侧边栏
                	
                	//人头上的血条
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives remove H" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add H health" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setdisplay belowName H" );
                	//end 
                	
                	//tab分数表scoreboard objectives add Point dummy
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives remove Point" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add Point dummy" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setdisplay list Point " );
                	return true;
                	
                case "P":
                case "point":
                	if(args.length<3) {LPS(sender,cmd.getName());return true;}
                	API.AddPoint(args[1], Integer.parseInt(args[2]));
                	return true;
                	
                case "M":
                case "Money":
                	if(args.length<3) {LPS(sender,cmd.getName());return true;}
                	API.AddMoney(args[1], Integer.parseInt(args[2]));
                	return true;
                	
                case"PW":
                case"PassWave":
                	API.PassWave();
                	return true;
                	
                case"reload":
                	reloadConfig();
                	configer = getConfig();
                	sender.sendMessage(prefix+"重载配置文件...");
                	return true;
                	
                case "test":
                	Collection<Monster> temp1 = Bukkit.getWorlds().get(0).getEntitiesByClass(org.bukkit.entity.Monster.class);
                	Object[] temp2= temp1.toArray();
                    for (int i = 0; i < temp2.length; i++) 
                    {
                    	Monster temp3 = (Monster) temp2[i];
                    	sender.sendMessage(temp3.getName());
                    }
                	return true;
                default:
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{参数错误}");
                }
                
                return true;                                                       
        }
       return false;
	}
	
	
	public void LPS(CommandSender sender,String cmd)
	{
    	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
    	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+cmd+">{参数不足}");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e)
	{
		
	}
	
	static int MobNum=0;
	public static void RefreshMobs()
	{
		
	}
	
	public static void ChangeMoney(String PlayerID,int num)
	{
		int temp2 = smyhw.configer.getInt("data.Money."+PlayerID);
       	Collection<? extends Player> Players = Bukkit.getOnlinePlayers();
    	int temp1=0;
        for(Player p :Players)
        {
        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players reset "+p.getName()+":"+API.GetMoney(p.getName())+" side" );
        	temp1++;
        }
        
        smyhw.configer.set("data.Money."+PlayerID, num+temp2);
        
        temp1=0;
        for(Player p :Players)
        {
        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players add "+p.getName()+":"+API.GetMoney(p.getName())+" side "+(12+temp1) );
        	temp1++;
        }
        
        
	}
	
}