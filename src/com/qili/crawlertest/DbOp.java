package com.qili.crawlertest;

import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import static com.mongodb.client.model.Filters.*;

public class DbOp {
	private Process process = null;
	private String filePath = "";
	private MongoDatabase mDB = null;
	private MongoCollection<Document> mcollect = null;
	private MongoCollection<Document> todolist = null;
	public static final String CNAME = "cname";
	public static final String CSCHOOL = "cschool";
	public static final String CLOCATION = "cLocation";
	public static final String CCATEGORY = "cCategory";
	public static final String CLINEWORK = "cLineWork";
	public static final String ISINVEST = "isInvest";
	public static final String PROJCATEGORY = "pCate";
	public static final String CSTATUS = "cStatus";
	public static final String CSTATE = "cState";
	public static final String CINTRO = "cIntro";
	public static final String DLINK = "dLink";
	public static final String DOCNAME = "DocName";
	public static final String DOCLINK = "DcoLink";
	public static final String TEAMNAME = "teamName";
	public static final String MEMBER = "member";
	public static final String MEMBER_NAME = "member_name";
	public static final String MEMBER_JOB = "member_job";
	public static final String MEMBER_MAJOR = "major";
	public static final String MEMBER_DEGREE = "degree";
	public static final String MEMBER_YEARS = "acaYears";
	public static final String MEMBER_INCHARGE = "inCharge";
	public static final String MEMBER_NUM = "member_number";
	public static final String PROF = "prof";
	public static final String PROF_NAME = "prof_name";
	public static final String PROF_TITLE = "prof_title";
	public static final String DATABASENAME = "CProjectDB";
	public static final String COLLECTIONNAME = "projInfo";
	public static final String TODOLIST = "todolist";
	public static final String TODOLINK = "todolink";
	
	public boolean startDB()
	{
		try {
			String path = System.getProperty("user.dir");
			path = path.replace(" ", "\\\\ ");
			Runtime.getRuntime().exec("cd "+path);
			System.out.println(path);
			filePath = "./mongodb-osx-x86_64-3.4.2/bin/mongod --dbpath ./data/db";
			Process p = Runtime.getRuntime().exec(filePath);
			process = p;
			if(p.isAlive())
			{
				connectDBInstance();
				return true;
			}
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void connectDBInstance()
	{
		//Connect to a Single instance
		MongoClient mc = new MongoClient();
//		MongoClient mc = new MongoClient("localhost");
//		MongoClient mc = new MongoClient( "localhost" , 27017 );
		//You can specify the MongoClientURI connection string:
//		MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
//		MongoClient mc = new MongoClient(connectionString);
		//Access DB
		mDB = mc.getDatabase(DATABASENAME);
		mcollect = mDB.getCollection(COLLECTIONNAME);
		todolist = mDB.getCollection(TODOLIST);
	}
	
	public void insertLink(String link, String school, String tname, boolean invest)
	{
		try{
		if(selectByString(DLINK, link)==null&&selectLink(link)==null)
		{
			todolist.insertOne(new Document(TODOLINK, link)
					.append(CSCHOOL, school)
					.append(TEAMNAME, tname)
					.append(ISINVEST, invest));
			System.out.println("insert yes");
		}
		else{
			System.out.println("insert no no");
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Document selectLink(String link)
	{
		if(todolist!=null)
		{
			return todolist.find(eq(TODOLINK, link)).first();
		}
		return null;
	}
	
	public String selectLinkandDelete(ContestEntry ce)
	{
		String str = "";
		if(todolist!=null)
		{
			Document d = todolist.find().first();
			str = d.getString(TODOLINK);
			ce.setCschool(d.getString(CSCHOOL));
			ce.setInvest(d.getBoolean(ISINVEST, false));
			ce.setTeamName(d.getString(TEAMNAME));
			ce.setdLink(str);
			todolist.deleteOne(eq(TODOLINK,str));
		}
		return str;
	}
	
	public boolean linkEmpty()
	{
		return todolist.count() == 0;
	}
	
	public boolean collectionEmpty()
	{
		return mcollect.count() == 0;
	}
	
	public Document parseEntry(ContestEntry ce)
	{
		if(ce==null)
		{
			return null;
		}
		Document doc = new Document(CNAME, ce.getCname())
				.append(CSCHOOL, ce.getCschool())
				.append(CLOCATION, ce.getcLocation())
				.append(CCATEGORY, ce.getcCategory())
				.append(CLINEWORK, ce.getcLineWork())
				.append(ISINVEST, ce.isInvest())
				.append(PROJCATEGORY, ce.getpCate())
				.append(CSTATUS, ce.getcStatus())
				.append(CSTATE, ce.getcState())
				.append(CINTRO, ce.getcIntro())
				.append(DLINK, ce.getdLink())
				.append(DOCNAME, ce.getDocName())
				.append(DOCLINK, ce.getDcoLink())
				.append(TEAMNAME, ce.getTeamName());
		
		ArrayList<Member> ms = ce.getMembers();
		for(int i = 0; i < ms.size(); i++)
		{
			int index = i+1;
			doc.append(MEMBER+index, new Document(MEMBER_NAME, ms.get(i).getName())
					.append(MEMBER_JOB, ms.get(i).getJob())
					.append(MEMBER_JOB, ms.get(i).getMajor())
					.append(MEMBER_DEGREE, ms.get(i).getDegree())
					.append(MEMBER_YEARS, ms.get(i).getAcaYears())
					.append(MEMBER_INCHARGE, ms.get(i).isInCharge()));
		}
		doc.append(MEMBER_NUM, ms.size());
		ProfLead pl = ce.getProf();
		if(pl!=null){
			doc.append(PROF, new Document(PROF_NAME, pl.getName())
					.append(PROF_TITLE, pl.getTitle()));
		}
		return doc;
	}
	
	public void insert(Document _doc)
	{
		if(mcollect!=null)
		{
			mcollect.insertOne(_doc);
		}
	}
	
	public void insertMultiple(ArrayList<Document> _doc)
	{
		if(mcollect!=null)
		{
			mcollect.insertMany(_doc);
		}
	}
	
	public long getCount()
	{
		if(mcollect!=null)
		{
			return mcollect.count();
		}
		return 0;
	}
	
	public ContestEntry selectByString(String key, String val)
	{
		ContestEntry entry = new ContestEntry();
		Document result;
		if(mcollect!=null)
		{
			try{
				result = mcollect.find(eq(key, val)).first();
				System.out.println(result.toJson());
			}catch(Exception e)
			{
				return null;
			}
			if(result!=null){
			entry.setCname(result.getString(CNAME));
			entry.setCschool(result.getString(CSCHOOL));
			entry.setcLocation(result.getString(CLOCATION));
			entry.setcCategory(result.getString(CCATEGORY));
			entry.setcLineWork(result.getString(CLINEWORK));
			entry.setInvest(result.getBoolean(ISINVEST));
			entry.setpCate(result.getString(PROJCATEGORY));
			entry.setcStatus(result.getString(CSTATUS));
			entry.setcState(result.getString(CSTATE));
			entry.setcIntro(result.getString(CINTRO));
			entry.setdLink(result.getString(DLINK));
			entry.setDocName(result.getString(DOCNAME));
			entry.setDcoLink(result.getString(DOCLINK));
			entry.setTeamName(result.getString(TEAMNAME));
			int num = result.getInteger(MEMBER_NUM, 0);
			ArrayList<Member> mems = new ArrayList<Member>();
			for(int i = 1; i <= num; i++)
			{
				Document mdoc = (Document)result.get(MEMBER+i);
				Member mb = new Member();
				mb.setName(mdoc.getString(MEMBER_NAME));
				System.out.println(mb.getName());
				mb.setJob(mdoc.getString(MEMBER_JOB));
				mb.setMajor(mdoc.getString(MEMBER_MAJOR));
				mb.setDegree(mdoc.getString(MEMBER_DEGREE));
				mb.setAcaYears(mdoc.getString(MEMBER_YEARS));
				mb.setInCharge(mdoc.getBoolean(MEMBER_INCHARGE));
				mems.add(mb);
			}
			entry.setMembers(mems);
			ProfLead lead = new ProfLead();
			Document ldoc = (Document)result.get(PROF);
			lead.setName(ldoc.getString(PROF_NAME));
			lead.setTitle(ldoc.getString(PROF_TITLE));
			
			return entry;
			}
			else{
				return null;
			}
		}
		return null;
	}
	
	public FindIterable<Document> selectAll()
	{
		if(mcollect!=null)
		{
			return mcollect.find();
		}
		return null;
	}
	
	public void update(String key, String val, ContestEntry entry)
	{
		if(mcollect!=null)
		{
			mcollect.updateOne(eq(key, val), parseEntry(entry));
		}
	}
	
	public void deleteMC(String key, String val)
	{
		if(mcollect!=null)
		{
			mcollect.deleteOne(eq(key, val));
		}
	}
	
	public void deleteAllMC()
	{
		mcollect.drop();
	}
	
	public void deleteAllTD()
	{
		todolist.drop();
	}
	
//	public void noRedundency()
//	{
//		mcollect.distinct(arg0, arg1)
//	}
	
	public boolean closeDB()
	{
		if(process!=null)
		{
			try{
			if(process.isAlive())
			{
				process.destroy();
				int endInt = process.waitFor();
				if(endInt==0)
				{
					return true;
				}
				else
				{
					process.destroyForcibly();
				}
			}
			}catch(Exception e)
			{
				return false;
			}
				
		}
		return false;
	}
}
