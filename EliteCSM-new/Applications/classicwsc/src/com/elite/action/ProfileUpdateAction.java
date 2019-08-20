package com.elite.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elite.model.LoginModal;
import com.elite.user.UserOtherDetail;
import com.elite.user.Userbean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ProfileUpdateAction extends ActionSupport {
	Logger logger = Logger.getLogger("wsc");
	public ProfileUpdateAction() {
		questionlist.add("Which sports do you like?");
		questionlist.add("Where did you first meet your spouse?");
		questionlist.add("What was the name of your first school?");
		questionlist.add("Who was your childhood hero?");
	}
	public String getSltQuestion() {
		return sltQuestion;
	}
	public void setSltQuestion(String sltQuestion) {
		this.sltQuestion = sltQuestion;
	}
	
	public String getC_strEmailAddress() {
		return c_strEmailAddress;
	}
	public void setC_strEmailAddress(String emailAddress) {
		c_strEmailAddress = emailAddress;
	}
	
	public String getC_strAnswer() {
		return c_strAnswer;
	}
	public void setC_strAnswer(String answer) {
		c_strAnswer = answer;
	}
	public String getC_strMobileNo() {
		return c_strMobileNo;
	}
	public void setC_strMobileNo(String mobileNo) {
		c_strMobileNo = mobileNo;
	}
	public String getC_strBillAddress1() {
		return c_strBillAddress1;
	}
	public void setC_strBillAddress1(String billAddress1) {
		c_strBillAddress1 = billAddress1;
	}
	public String getC_strBillAddress2() {
		return c_strBillAddress2;
	}
	public void setC_strBillAddress2(String billAddress2) {
		c_strBillAddress2 = billAddress2;
	}
	public String getC_strBillZip() {
		return c_strBillZip;
	}
	public void setC_strBillZip(String billZip) {
		c_strBillZip = billZip;
	}
	public String getC_strHomePhone() {
		return c_strHomePhone;
	}
	public void setC_strHomePhone(String homePhone) {
		c_strHomePhone = homePhone;
	}
	
	public List<String> getQuestionlist() {
		return questionlist;
	}
	public void setQuestionlist(List<String> questionlist) {
		this.questionlist = questionlist;
	}
	private String c_strEmailAddress;
	private String sltQuestion;
	private String c_strBillAddress2;
	private String c_strBillZip;
	private String c_strHomePhone;
	private String c_strAnswer;
	private String c_strMobileNo;
	private String c_strBillAddress1;
	private List<String> questionlist = new ArrayList<String>();
	@Override
	public String execute()  {
		Userbean user = (Userbean)ActionContext.getContext().getSession().get("user");
		c_strEmailAddress = user.getUserotherdetail().getEmailaddress();
		sltQuestion = user.getUserotherdetail().getKeyquestion();
		c_strBillAddress2 = user.getUserotherdetail().getAddressline2();
		c_strBillZip = user.getUserotherdetail().getPostalcode();
		c_strHomePhone = user.getUserotherdetail().getHomephone();
		c_strAnswer = user.getUserotherdetail().getKeyanswer();
		c_strMobileNo = user.getUserotherdetail().getMobilephone();
		c_strBillAddress1 = user.getUserotherdetail().getAddressline1();
		return SUCCESS;
	}
	
	public String update()
	{
		LoginModal loginmodel = new LoginModal();
		UserOtherDetail userdetail = new UserOtherDetail();
		userdetail.setEmailaddress(c_strEmailAddress);
		userdetail.setKeyquestion(sltQuestion);
		userdetail.setAddressline2(c_strBillAddress2);
		userdetail.setPostalcode(c_strBillZip);
		userdetail.setHomephone(c_strHomePhone);
		userdetail.setKeyanswer(c_strAnswer);
		userdetail.setMobilephone(c_strMobileNo);
		userdetail.setAddressline1(c_strBillAddress1);
		try{
			Userbean user = (Userbean)ActionContext.getContext().getSession().get("user");
			loginmodel.profileUpdate(userdetail, user);
			this.addActionMessage("Profile Updated Successfully");
		}
		catch (Exception e) {
			logger.error(e);
			this.addActionMessage("Profile Updated Change Unsuccessful");
		}
		return SUCCESS;
	}
	
}
