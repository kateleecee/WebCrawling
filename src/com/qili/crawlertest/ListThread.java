package com.qili.crawlertest;

public class ListThread extends Thread {
	int pages = -1;
	CrawlingAction ca = null;
	DbOp DP = null;
	
	public ListThread(int page, DbOp dp) {
		this.pages = page;
		this.ca = new CrawlingAction();
		this.DP = dp;
	}
	
	@Override
	public void run() {
		if(this.pages!=-1)
		{
			ca.parseParentDoc(ca.connectParent(pages), DP);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			return;
		}
	}
	
}
