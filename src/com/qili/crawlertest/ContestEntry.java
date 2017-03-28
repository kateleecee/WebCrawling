package com.qili.crawlertest;

import java.util.ArrayList;

public class ContestEntry {
	private String cname;
	private String cschool;
	private String cLocation;
	private String cCategory;
	private String cLineWork;
	private boolean isInvest;
	private String pCate;
	private String cStatus;
	private String cState;
	private String cIntro;
	private String dLink;
	private String DocName;
	private String DcoLink;
	private String teamName;
	private ArrayList<Member> members;
	private ProfLead prof;
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public ArrayList<Member> getMembers() {
		return members;
	}
	public void setMembers(ArrayList<Member> members) {
		this.members = members;
	}
	public ProfLead getProf() {
		return prof;
	}
	public void setProf(ProfLead prof) {
		this.prof = prof;
	}
	public String getdLink() {
		return dLink;
	}
	public void setdLink(String dLink) {
		this.dLink = dLink;
	}
	public String getDocName() {
		return DocName;
	}
	public void setDocName(String docName) {
		DocName = docName;
	}
	public String getDcoLink() {
		return DcoLink;
	}
	public void setDcoLink(String dcoLink) {
		DcoLink = dcoLink;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCschool() {
		return cschool;
	}
	public void setCschool(String cschool) {
		this.cschool = cschool;
	}
	public String getcLocation() {
		return cLocation;
	}
	public void setcLocation(String cLocation) {
		this.cLocation = cLocation;
	}
	public String getcCategory() {
		return cCategory;
	}
	public void setcCategory(String cCategory) {
		this.cCategory = cCategory;
	}
	public String getcLineWork() {
		return cLineWork;
	}
	public void setcLineWork(String cLineWork) {
		this.cLineWork = cLineWork;
	}
	public boolean isInvest() {
		return isInvest;
	}
	public void setInvest(boolean isInvest) {
		this.isInvest = isInvest;
	}
	public String getpCate() {
		return pCate;
	}
	public void setpCate(String pCate) {
		this.pCate = pCate;
	}
	public String getcStatus() {
		return cStatus;
	}
	public void setcStatus(String cStatus) {
		this.cStatus = cStatus;
	}
	public String getcState() {
		return cState;
	}
	public void setcState(String cState) {
		this.cState = cState;
	}
	public String getcIntro() {
		return cIntro;
	}
	public void setcIntro(String cIntro) {
		this.cIntro = cIntro;
	}
}
