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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;


public class smyhw extends JavaPlugin implements Listener 
{
	public static JavaPlugin smyhw_;
	public static Logger loger;
	public static FileConfiguration configer;
	public static List cmd_pre_Wave;
	public static String prefix;
	public static int EndWaveNum;
	public static String ReportDir;
	public static List<Player> PlayerList;//有效玩家列表
	public static List<Player> fullPlayer;//全部玩家列表
	public static TimeOut_PassWave TimeOutThread;
	
	@Override
    public void onEnable() 
	{
		
		getLogger().info("EschatologicalUnit.Statistics加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		saveDefaultConfig();
		configer = getConfig();
		smyhw_ = this;
		PlayerList = Collections.synchronizedList(new ArrayList<Player>());
		getLogger().info("正在加载配置...");
		cmd_pre_Wave = configer.getStringList("config.cmd_pre_Wave");
		prefix = configer.getString("config.prefix");
		EndWaveNum = configer.getInt("config.EndWaveNum");
		ReportDir = configer.getString("config.ReportDir");
		saveConfig();
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("EschatologicalUnit.Statistics加载完成");
    }

	@Override
    public void onDisable() 
	{
		if(TimeOutThread!=null) {TimeOutThread.cancel();TimeOutThread=null;}
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
                	//重载配置，抛弃上一对局的临时数据
                	reloadConfig();
                	configer = getConfig();
                	this.MobNum=0;
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives remove side" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add side dummy §c§n§o§lOurWorld:末日小队" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set ~ side 35" );
//                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §e玩家货币 side 34" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §7§m--------------- side 33" );

                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §7--------- side -11" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §l波数：0 side -12" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §l§c剩余怪物:§d0 side -13" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §n______________________ side -16" );
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §o§lEschatological_Unit side -17" );
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
                	PlayerList.clear();
                	PlayerList.addAll(Bukkit.getOnlinePlayers());
                	fullPlayer = Collections.synchronizedList(new ArrayList<Player>()); 
                	fullPlayer.addAll(Bukkit.getOnlinePlayers());
                	ChangeMoney("smyhw",0);
                	smyhw.configer.set("data.Wave",0);
                	if(TimeOutThread!=null) {TimeOutThread.cancel();}
                	TimeOutThread=new TimeOut_PassWave();
                	API.PassWave();
                	return true;
                	
                case "P":
                case "point":
                	if(args.length<3) {LPS(sender,cmd.getName());return true;}
                	API.AddPoint(args[1], Integer.parseInt(args[2]));
                	sender.sendMessage("为玩家<"+args[1]+">添加<"+args[2]+">分数");
                	return true;
                	
                case "M":
                case "Money":
                	if(args.length<3) {LPS(sender,cmd.getName());return true;}
                	API.AddMoney(args[1], Integer.parseInt(args[2]));
                	sender.sendMessage("为玩家<"+args[1]+">添加<"+args[2]+">货币");
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
                    	return true;
                    }
                case "rplayer":
                {
                	if(args.length<2) {LPS(sender,"rplayer");return true;}
                	Player temp3 = Bukkit.getPlayer(args[1]);
                	API.getPlayerList(temp3, 0);
                	sender.sendMessage(prefix+"玩家<"+temp3.getName()+">已出局，目前玩家列表:"+smyhw.PlayerList.toArray().toString());
                	Bukkit.broadcastMessage("队员<"+temp3.getName()+">倒在的血泊中....");
                	return true;
                }
                case "cost_cmd":
                {
                	if(args.length<4) {LPS(sender,"cost_cmd");return true;}
                	Player player = Bukkit.getPlayer(args[1]);
                	int cost = Integer.parseInt(args[2]);
                	if(API.GetMoney(player.getName()) < cost) {player.sendMessage("§e[提示]§r:你的货币不足,这需要§d<§e"+cost+"§d>");return true;}
                	String[] tar_cmd_arr = Arrays.copyOfRange(args, 3,args.length);
                	String tar_cmd = "";
                	for(String temp3 : tar_cmd_arr)
                	{
                		if(temp3.equals("|||"))
                		{
                        	tar_cmd.replaceAll("%player%", player.getName());
                        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),tar_cmd);
                        	tar_cmd="";
                        	continue;
                		}
                		tar_cmd = tar_cmd+temp3+" ";
                	}
                	tar_cmd.replaceAll("%player%", player.getName());
                	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),tar_cmd);
                	API.AddMoney(player.getName(), 0-cost);
                	return true;
                }
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
		Location temp1 = e.getPlayer().getWorld().getSpawnLocation();
		Boolean temp2 = e.getPlayer().teleport(temp1);
		if(!temp2) {e.getPlayer().kickPlayer("内部错误，请联系管理员");}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		API.getPlayerList(e.getPlayer(), 0);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		RefreshMobs(2);
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e)
	{
		RefreshMobs(1);
	}
	
	//这里可以同步执行，反正是在一局结束之后，操作IO卡一下问题不大
	public static String SaveReport()
	{
		HashMap data = new HashMap();
		if(!new File(smyhw.ReportDir).exists()) {new File(smyhw.ReportDir).mkdirs();}
		String Ran = getRandomString(27);
		File SaveFile = new File(smyhw.ReportDir+Ran+".html");
		while(SaveFile.exists())
		{
			Ran = getRandomString(27);
			SaveFile = new File(smyhw.ReportDir+Ran+".html");
		}
		Collection<? extends Player> Players = fullPlayer;
		for(Player p :Players)//写入玩家信息
		{
			data.put(p.getName(), API.GetPoint(p.getName())+"");
		}
		String PlayerList="";
		for(Player p :Players)//创建玩家列表
		{
			PlayerList = PlayerList+","+p.getName();
		}
		PlayerList = PlayerList.substring(1);//去除首部多余的逗号
		data.put("PlayerList", PlayerList);
		data.put("Wave", API.GetWave()+"");
		String JsonData = online.smyhw.localnet.lib.Json.Create(data);
		try 
		{
			FileWriter temp1 = new FileWriter(SaveFile);
			temp1.write(JsonData);
			temp1.close();
		} catch (IOException e) 
		{
			Bukkit.broadcastMessage(prefix+"发生IO错误，请立即汇报管理员，该对局数据已丢失！");
        	loger.warning(prefix+"发生IO错误，请立即汇报管理员，该对局数据已丢失！");
			e.printStackTrace();
		}
		return Ran;
	}

	 public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	
	static int MobNum=0;
	public static void RefreshMobs(int type)
	{
		Collection<Monster> temp1 = Bukkit.getWorlds().get(0).getEntitiesByClass(org.bukkit.entity.Monster.class);
		int newNum = temp1.size();
		if(type==1) {newNum=newNum+1;}//如果是生物生成时，那么这个怪其实还不存在
		if(type==2) {newNum=newNum-1;}
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players reset §l§c剩余怪物:§d"+MobNum+" side" );
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §l§c剩余怪物:§d"+newNum+" side -13" );
		MobNum=newNum;
		if(newNum<=0) {API.PassWave();}
	}
	
	public static void ChangeMoney(String PlayerID,int num)
	{
		int temp2 = smyhw.configer.getInt("data.Money."+PlayerID);
       	Collection<? extends Player> Players = fullPlayer;
    	int temp1=0;
        for(Player p :Players)
        {
        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players reset "+p.getName()+"§r：§e"+API.GetMoney(p.getName())+" side" );
        	temp1++;
        }
        
        smyhw.configer.set("data.Money."+PlayerID, num+temp2);
        
        temp1=0;
        for(Player p :Players)
        {
        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players add "+p.getName()+"§r：§e"+API.GetMoney(p.getName())+" side "+(12+temp1) );
        	temp1++;
        }
        
        
	}
	
}


class TimeOut_PassWave extends BukkitRunnable
{
	int time=20*60*5;
	public TimeOut_PassWave()
	{
		this.runTaskTimer(Bukkit.getPluginManager().getPlugin("EschatologicalUnit.Statistics"), 0, 200);
	}
	@Override
	public void run()
	{
		this.time = time-200;
		if(time<=0)
		{//触发下一波
			API.PassWave();
			this.time = 20*60*5;
		}
		System.out.println("time_out="+this.time);

	}
}