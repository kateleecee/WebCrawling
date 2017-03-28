package com.qili.crawlertest;

public class RechieveThread extends Thread {

	private DbOp dp = null;
	private CrawlingAction ca = null;
	
	public RechieveThread(DbOp dpp) {
		this.dp = dpp;
		this.ca = new CrawlingAction();
	}
	
	@Override
	public void run() {
		if(dp!=null)
		{
			ca.parseProject(dp);
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
