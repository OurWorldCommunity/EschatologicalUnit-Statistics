package online.smyhw.EschatologicalUnit.Statistics;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class API 
{
	/**
	 * 
	 * 操作有效玩家列表</br>
	 * type=0 删除</br>
	 * type=1 增加</br>
	 * type=2 不做任何操作</br>
	 * @param player
	 * @param type
	 * @return 操作完成后的列表
	 */
	public static List<Player> getPlayerList(Player player,int type)
	{
		switch(type)
		{
		case 0:
			smyhw.PlayerList.remove(player);
			if(smyhw.PlayerList.isEmpty()) {EndGame();}
			break;
		case 1:
			smyhw.PlayerList.add(player);
			break;
		case 2:
			return smyhw.PlayerList;
		}
		return smyhw.PlayerList;
	}
	public static void AddPoint(String PlayerID,int num)
	{
		smyhw.configer.set("data.Point."+PlayerID, num);
		if(num>=0)
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players add "+PlayerID+" Point "+num );
			return;
		}
		else
		{
			num = 0-num;
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players remove "+PlayerID+" Point "+num );
			return;
		}
		
//			new DoOperation("Point",PlayerID,num);
	}
	
	public static void AddMoney(String PlayerID,int num)
	{
		smyhw.ChangeMoney(PlayerID, num);
//		new DoOperation("Money",PlayerID,num);
	}
	
	/**
	 * 分数
	 * @param PlayerID
	 * @return
	 */
	public static int GetPoint(String PlayerID)
	{
		return smyhw.configer.getInt("data.Point."+PlayerID);
	}
	
	public static int GetMoney(String PlayerID)
	{
		return smyhw.configer.getInt("data.Money."+PlayerID);
	}
	
	public static int GetWave()
	{
		return smyhw.configer.getInt("data.Wave");
	}
	
	public static void EndGame()
	{
		String re = smyhw.SaveReport();
		Bukkit.broadcastMessage(smyhw.prefix+"战绩报告已生成:");
		Bukkit.broadcastMessage("https://hanhz.smyhw.online/smyhw/EschatologicalUnit.php?ID="+re);
		//执行结束指令
		for(String temp2:smyhw.configer.getStringList("config.cmd_last"))
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),temp2);
		}
	}
	
	public static void PassWave()
	{
		int temp1 = smyhw.configer.getInt("data.Wave");
		//对局开始的第一波
		if(temp1==0)
		{
			//执行开始指令
			for(String temp2:smyhw.configer.getStringList("config.cmd_first"))
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),temp2);
			}
		}
		if(temp1>smyhw.EndWaveNum)
		{//触发游戏结束
			EndGame();
			return;
		}
		smyhw.configer.set("data.Wave",temp1+1 );
		 Iterator<String> temp2 =  smyhw.cmd_pre_Wave.iterator();
	     while(temp2.hasNext())
	     {
	        	String cmd  = temp2.next();
	        	cmd = cmd.replace("%num%", (temp1+1)+"");
	        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd );
	     }
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players reset §l波数："+temp1+" side" );
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set §l波数："+(temp1+1)+" side -12" );
	}

}


