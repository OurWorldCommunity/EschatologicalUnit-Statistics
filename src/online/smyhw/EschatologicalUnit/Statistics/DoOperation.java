package online.smyhw.EschatologicalUnit.Statistics;

class DoOperation extends Thread
{
	String type;
	String PlayerID;
	int num;
	public DoOperation(String type,String PlayerID,int num)
	{
		this.type = type;
		this.num = num;
		this.PlayerID = PlayerID;
		this.start();
	}
	
	public void run()
	{
		smyhw.configer.set("data."+type+"."+PlayerID, num);
//		smyhw.s.saveConfig();
	}
}