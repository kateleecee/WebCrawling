package com.qili.crawlertest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainWindow {
	JFrame f = new JFrame("xixi");
	JButton b1 = new JButton("开始爬");
	JButton b5 = new JButton("开始爬pdf");
	JButton b2 = new JButton("导出？");
	JButton b3 = new JButton("删除todo？");
	JButton b4 = new JButton("删除collection？");
	JLabel label = new JLabel("Hello Xixi, miss me?");
	DbOp dbop = new DbOp();
	boolean crawfinished = false;
	ExecutorService listpool = null;
	ExecutorService collectionpool = null;
	ExecutorService pdfpool = null;
	ExecutorService pagepool = null;
	
	public void init()
	{
		JPanel jp = new JPanel();
		jp.add(b1);
		jp.add(b5);
		jp.add(b2);
		jp.add(b3);
		jp.add(b4);
		jp.add(label);
		f.add(jp);
		b5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread th = new Thread(new Runnable() {
					
					@Override
					public void run() {
						while(true){
							System.out.println("RR");
							try{
						while(!dbop.linkEmpty())
						{
							RechieveThread rt = new RechieveThread(dbop);
							pdfpool.submit(rt);
							System.out.println("RRRR");
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(dbop.linkEmpty()&&crawfinished)
						{
							break;
						}}catch(Exception e){}
						}
						}
				});
				collectionpool.submit(th);
			}
		});
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				crawfinished = true;
				label.setText("Start to crawl...");
				CrawlingAction ca = new CrawlingAction();
				Thread th = new Thread(new Runnable() {
					
					@Override
					public void run() {
						int page = ca.pagesSum();
						for(int i = 1; i<=page; i++)
						{
							ListThread lt = new ListThread(i, dbop);
							listpool.submit(lt);
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} 

						label.setText("Craw complete...");
						crawfinished = false;
						}
				});
				pagepool.submit(th);
			}
		});
		b2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				label.setText("Start to export...");
			}
		});
		b3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				label.setText("delete todolist...");
				dbop.deleteAllTD();
				if(dbop.linkEmpty())
				{
					label.setText("empty");
				}
				else
				{
					label.setText("delete fail");
				}
			}
		});
		b4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				label.setText("delete collection...");
				dbop.deleteAllMC();
				if(dbop.linkEmpty())
				{
					label.setText("empty");
				}
				else
				{
					label.setText("delete fail");
				}
			}
		});
		
		f.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				
				//Start the mongoDB
				if(dbop.startDB())
				{
					System.out.println("start");
				}
				else{
					System.out.println("fail");
				}
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(dbop.closeDB())
				{
					System.out.println("closed");
					e.getWindow().dispose();
					System.exit(0);
				}else{
					System.out.println("closed fail");
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		f.pack();
		f.setVisible(true);
		//ThreadPool
		listpool = Executors.newFixedThreadPool(400);
		collectionpool = Executors.newFixedThreadPool(400);
		pdfpool = Executors.newFixedThreadPool(400);
		pagepool = Executors.newFixedThreadPool(400);
	}
	
	public static void main(String[] args) throws IOException
	{
		MainWindow m = new MainWindow();
		m.init();
		
	}
}
