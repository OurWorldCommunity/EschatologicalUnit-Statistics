package online.smyhw.EschatologicalUnit.Statistics;

import java.util.Iterator;

import org.bukkit.Bukkit;

public class API 
{
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
	
	public static void PassWave()
	{
		int temp1 = smyhw.configer.getInt("data.Wave");
		if(temp1>smyhw.EndWaveNum)
		{//触发游戏结束
			String re = smyhw.SaveReport();
			Bukkit.broadcastMessage(smyhw.prefix+"战绩报告已生成:");
			Bukkit.broadcastMessage("https://hanhz.smyhw.online/smyhw/EschatologicalUnit.php?ID="+re);
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


