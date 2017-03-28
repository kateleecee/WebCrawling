package com.qili.crawlertest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class CrawlingAction {
	public static final String rootLink = "http://cy.ncss.org.cn";
	//craw the parent page first
	public Document connectParent(int pageNum)
	{
		Document doc = null;
		try{
			Map<String, String> datamap = new HashMap<String, String>();
			datamap.put("ecCode", "CYDS_2TH");
			datamap.put("ec_p", String.valueOf(pageNum));
			datamap.put("p", "page");
			doc = Jsoup.connect("http://cy.ncss.org.cn/search.html").userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").timeout(30000).data(datamap).post();
			}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return doc;
	}
	
	public void parseProject(DbOp db)
	{
		Document doc = null;
		ContestEntry conEntry = new ContestEntry();
		String sonLink;
		synchronized (db) {
			sonLink = db.selectLinkandDelete(conEntry);
		}
		try {
			doc = Jsoup.connect(rootLink+sonLink).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
			if(doc!=null)
			{
				String cName = doc.select("h3.the-obj-name").first().text();
				conEntry.setCname(cName);
				Elements projInfo = doc.select("div.obj-bs-lst").first().select("span.b");
				Elements contestInfo = doc.select("div.obj-side-r").select("div.the-obj-tb").first().select("td");
				String location = projInfo.last().text();
				conEntry.setcLocation(location);
				String cCate = contestInfo.last().text();
				conEntry.setcCategory(cCate);
				String cLWork = projInfo.first().text();
				conEntry.setcLineWork(cLWork);
				String pCate = projInfo.eq(1).first().text();
				conEntry.setpCate(pCate);
				String cstatus = contestInfo.first().text();
				conEntry.setcStatus(cstatus);
				String cstate = projInfo.eq(2).first().text();
				conEntry.setcState(cstate);
				String intro = doc.select("div.obj-bs-intro").first().text();
				conEntry.setcIntro(intro);
				Elements doclinks = doc.select("div.xmjhs-box").first().select("a.s_bluebtn");
				String doclink = doclinks.first().attr("href");
//				for(Element d: doclinks)
//				{
//					if(d.attr("href").endsWith("pdf"))
//					{
//						doclink = d.attr("href");
//					}
//				}
				String[] docx = doclink.split("/");
				int lastIdx = docx.length-1;
				String docName = docx[lastIdx];
				conEntry.setDocName(docName);
				conEntry.setDcoLink(docName);
				downloadDoc(doclink, docName);
				//Members
				String chargeName = doc.select("div.the-obj-father").first().select("div.the-obj-tb").first().select("th").first().text();
				ArrayList<Member> mb = new ArrayList<>();
				Elements els = doc.select("div#m_info").select("tr");
				for(Element el: els)
				{
					Member m = new Member();
					m.setName(el.select("th").first().text());
					m.setJob(el.select("td").eq(0).first().text());
					m.setMajor(el.select("td").eq(2).first().text());
					m.setDegree(el.select("td").eq(3).first().text());
					m.setAcaYears(el.select("td").last().text());
					if(chargeName.equals(m.getName()))
					{
						m.setInCharge(true);
					}
					else{
						m.setInCharge(false);
					}
					mb.add(m);
				}
				conEntry.setMembers(mb);
				ProfLead prof = new ProfLead();
				String profname = doc.select("div.the-obj-tb").last().select("th").text();
				String ptitle = doc.select("div.the-obj-tb").last().select("td").first().text();
				prof.setName(profname);
				prof.setTitle(ptitle);
				conEntry.setProf(prof);
				synchronized (db) {
					db.insert(db.parseEntry(conEntry));
					System.out.println("insert");
					System.out.println(db.selectByString(DbOp.DLINK,conEntry.getdLink()).getCname());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void killFirefox()
	{
	    String username = System.getProperty("user.name");
	    try {
			Runtime.getRuntime().exec("pkill -U "+username+" firefox");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void downloadDoc(String link, String name) throws IOException
	{
//		String mine = link.split(".")[1];
//		String mimeType = "";
//		switch (mine) {
//		case "doc":
//			mimeType = "application/msword";
//			break;
//		case "docx":
//			mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
//			break;
//		case "pdf":
//			mimeType = "application/pdf";
//			break;
//
//		default:
//			break;
//		}
		String wgetPath = "/usr/local/Cellar/wget/1.19.1/bin/";
		String path = System.getProperty("user.dir").replace(" ", "\\\\ ");
//		String file_path = System.getProperty("user.dir").replace(" ", "\\\\ ")+"/data/doc/";
		Runtime.getRuntime().exec("cd "+path);
		Runtime.getRuntime().exec(wgetPath+"wget -c --user-agent=\"Mozilla/5.0\" --referer http://cy.ncss.org.cn/ -P ./data/doc "+link);
//	    System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"/geckodriver");
	    
//	    FirefoxProfile fxProfile = new FirefoxProfile();
//	    fxProfile.setPreference("browser.download.folderList",2);
//	    fxProfile.setPreference("browser.download.manager.showWhenStarting",false);
//	    fxProfile.setPreference("browser.download.dir",file_path);
//	    fxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
//	    fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",mimeType);
//	    fxProfile.setPreference("pdfjs.disabled", true);
//	    fxProfile.setPreference("plugin.scan.plid.all", false);
//	    fxProfile.setPreference("plugin.scan.Acrobat", "99.0");
	    
//	    WebDriver driver = new FirefoxDriver(fxProfile);
//	    driver.get(rootLink+link);
//	    WebElement el = driver.findElement(By.cssSelector("a.s_bluebtn:nth-child(1)"));
//	    el.click();
//	    Actions actions = new Actions(driver);
//	    actions.moveToElement(el);
//	    actions.contextClick(el).build().perform();
//	    WebElement el_save = driver.findElement(By.partialLinkText("Save"));
//	    el_save.click();
//	    JavascriptExecutor js = (JavascriptExecutor)driver;
//	    js.executeScript("var el = document.querySelectorAll('a[href*=\"."+mine+"\"]')[0];"
//	    		+ "el.click();"
//	    		+ "document.execCommand('SaveAs', true, el.href)"
//	    		);
	    
//	    Path fpath = FileSystems.getDefault().getPath(file_path, name);
//	    while(true)
//	    {
//	    	if(Files.exists(fpath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
//	    	{
//	    		break;
//	    	}
//	    }
	}
	
	public int pagesSum()
	{
		try {
			Map<String, String> datamap = new HashMap<String, String>();
			datamap.put("ecCode", "CYDS_2TH");
			datamap.put("ec_p", "1");
			datamap.put("p", "page");
			Document doc  = Jsoup.connect("http://cy.ncss.org.cn/search.html").userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").timeout(30000).data(datamap).post();
			Elements p = doc.select("head");
			Element script = Jsoup.parse(p.html()).select("script").first();
			
			String jsonString = script.html();
			String jsoninner = jsonString.substring(jsonString.indexOf("{"), jsonString.indexOf("seajs")-1).replace(";", "").trim();
			
			System.out.println(jsoninner);
			
			//转json
			JSONObject jsonObject = new JSONObject(jsoninner);
			return jsonObject.getInt("totalPage");
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	public void parseParentDoc(Document doc, DbOp db)
	{
		Elements p = doc.select("head");
		Element script = Jsoup.parse(p.html()).select("script").first();
		//link
		Elements a = doc.select("p.name");
		boolean invest = false;
		String alink = "";
		String schoolName = "";
		String teamName = "";
		
		String jsonString = script.html();
		String jsoninner = jsonString.substring(jsonString.indexOf("{"), jsonString.indexOf("seajs")-1).replace(";", "").trim();
		
		System.out.println(jsoninner);
		
		//转json
		JSONObject jsonObject;
		JSONArray jsonArr;
		try {
			jsonObject = new JSONObject(jsoninner);
			jsonArr = jsonObject.getJSONArray("pageResults");
			for(int j = 0; j < jsonArr.length(); j++)
			{
				//name
//				try{
//				String name = jsonArr.getJSONObject(j).getString("name");
//				System.out.println(name);
//				
//				}catch(Exception e)
//				{
//					e.printStackTrace();
//				}
				//schoolName
				try{
				schoolName = jsonArr.getJSONObject(j).getString("schoolName");
				System.out.println(schoolName);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				//项目所在地
//				try{
//					String location = jsonArr.getJSONObject(j).getString("locationName");
//					row.createCell(1).setCellValue(location);
//					
//					}catch(Exception e)
//					{
//						e.printStackTrace();
//					}
				//invest
				try{
				int invest_int = jsonArr.getJSONObject(j).getInt("wasInvest");
				if(invest_int == 0)
				{
					invest = false;
				}
				else
				{
					invest = true;
				}
				}catch(Exception e)
				{
//					e.printStackTrace();
					invest = false;
				}
				System.out.println(invest);
				//teamName
				try{
					teamName = jsonArr.getJSONObject(j).getString("teamName");
				}catch(Exception e)
				{
//					e.printStackTrace();
					invest = false;
				}
				System.out.println(invest);
				//introduction
//				try{
//				String intro = jsonArr.getJSONObject(j).getString("synopsis");
//				row.createCell(7).setCellValue(intro);
//				
//				}catch(Exception e)
//				{
//					e.printStackTrace();
//				}
				//<a>
				alink = a.get(j).select("a").attr("href");
				System.out.println(alink);
				synchronized (db) {
					System.out.println("db");
					db.insertLink(alink, schoolName,teamName,invest);
					if(db.selectLink(alink)!=null)
					{
						System.out.println(db.selectLink(alink).toJson());
					}
					else{
						System.out.println("null");
					}
				}
		}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
