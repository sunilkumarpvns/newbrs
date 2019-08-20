package com.elitecore.aaa.radius.util.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class RadiusCertificateCommand extends EliteBaseCommand {
	
	private final static String TRUSTED_CERTIFICATE_ACRONYM = "-t";
	private final static String SERVER_CERTIFICATE_ACRONYM  = "-s";	
	private final static String TRUSTED_CERTIFICATE = "-trustedCertificate";
	private final static String SERVER_CERTIFICATE  = "-serverCertificate";
	//private final static String HELP = "?";
	private String TRUSTED_CERTIFICATE_PATH = null;
	private String SERVER_CERTIFICATE_PATH = null;
	private ArrayList<String> certificateList ;

	public RadiusCertificateCommand(String serverHome) {		
		TRUSTED_CERTIFICATE_PATH = serverHome + File.separator + "system" + File.separator + "cert" + File.separator + "trustedcertificates";
		SERVER_CERTIFICATE_PATH = serverHome + File.separator + "system" + File.separator + "cert" + File.separator + "server";
	}
	private void loadServerCertificates(){
		try {
			certificateList = new ArrayList<String>();
			File serverCertificates = new File(SERVER_CERTIFICATE_PATH);
			loadFile(serverCertificates);			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private void loadTrustedCertificates(){
		try {
			certificateList = new ArrayList<String>();
			File trustedCertificates = new File(TRUSTED_CERTIFICATE_PATH);
			loadFile(trustedCertificates);				
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	private void loadFile(File file) throws Exception{
		if(file.isDirectory()){
			File[] fileList = file.listFiles();
			for(int i=0;i<fileList.length;i++){
				loadFile(fileList[i]);
			}
		}else{
			if(file.getName().endsWith(".pem")){
				certificateList.add(file.getName());
			}
		}
	}
	private String getCertificateDetail(String filePath){
		try {
			StringBuilder strBuilder = new StringBuilder();		
			InputStream is = new FileInputStream(filePath);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			java.security.cert.Certificate cert = cf.generateCertificate(is);
			X509Certificate x509cert = (X509Certificate) cert;
			strBuilder.append(x509cert.toString());
			return ("Detail: "+strBuilder.toString());
		} catch (FileNotFoundException e) {
		} catch (CertificateException e) {
		}
		return "This Certificate does not exist.";
	}
	private String displayCertificateList(ArrayList<String> certificateList){
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		out.println("List of Certificates");
		out.println("--------------------");
		for(int i=0;i<certificateList.size();i++){
			out.println((i+1)+". "+ certificateList.get(i));
		}
		
		return sw.toString();
	}
	@Override
	public String execute(String parameter) {
		String response = "--";
		
		if(parameter != null){
			
			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
			String certName = null;	
			
			if (tokenizer.hasMoreTokens())
				parameter = tokenizer.nextToken();
			
			if (tokenizer.hasMoreTokens())
				certName = tokenizer.nextToken().trim();
			
			if(parameter.trim().equals(HELP_OPTION) || parameter.trim().equals("?")){
				response = getHelp();
			}else if(parameter.trim().equals(TRUSTED_CERTIFICATE_ACRONYM) || parameter.trim().equals(TRUSTED_CERTIFICATE)){
				if(certName != null){
					if(certName.endsWith(".pem"))
						response = getCertificateDetail(TRUSTED_CERTIFICATE_PATH + File.separator + certName);
					else
						response = getCertificateDetail(TRUSTED_CERTIFICATE_PATH + File.separator + certName + ".pem");
				}else{
					loadTrustedCertificates();
					response = displayCertificateList(certificateList);
				}
			}else if(parameter.trim().equals(SERVER_CERTIFICATE_ACRONYM) || parameter.trim().equals(SERVER_CERTIFICATE)){
				if(certName != null){
					if(certName.endsWith(".pem"))
						response = getCertificateDetail(SERVER_CERTIFICATE_PATH + File.separator +  certName);
					else
						response = getCertificateDetail(SERVER_CERTIFICATE_PATH + File.separator +  certName + ".pem");
				}else{
					loadServerCertificates();
					response = displayCertificateList(certificateList);
				}
			}else{
				StringWriter stringWriter = new StringWriter();
				stringWriter.append("Required parameter missing/invalid.");
				stringWriter.append("\n");
				stringWriter.append(getHelp().toString());
				response = stringWriter.toString();
			}			
		}else{
			StringWriter stringWriter = new StringWriter();
			stringWriter.append("Required parameter missing.");
			stringWriter.append("\n");
			stringWriter.append(getHelp().toString());
			response = stringWriter.toString();
		}
		return response;
	}
	@Override
	public String getCommandName() {
		return "cert";
	}

	@Override
	public String getDescription() {
		return "Display list of server/trusted X.509 V3 certificates of type '.pem'.";
	}
	private String getHelp() {
		/*StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		String paramName[] = {TRUSTED_CERTIFICATE + " [Certificate Name]",SERVER_CERTIFICATE + " [Certificate Name]"};
		String paramDesc[] = {"Displays list of trusted certificates.If supplied with the Certificate Name then, it displays the details of that Certificate. ",
							  "Displays list of Server certificates.If supplied with the Certificate Name then, it displays the details of that Certificate. "};
		
		out.println("Usage : "+getCommandName()+" <options>");
		out.println("Possible options"  );
		for(int i=0;i<paramDesc.length;i++){
			out.println("    " + fillChar(paramName[i],23) +  paramDesc[i] );
		}
		out.close();
		return stringWriter.toString();*/
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<options>] ");
		out.println("Description : " + getDescription());
		out.println();
		out.println(fillChar("where options include:", 30));		
		out.println("    " + fillChar("-t | -trustedCertificate  [certificate-name]", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Displays list of trusted certificates.If supplied with the Certificate Name then, it displays the details of that Certificate. ", 30));
		out.println("    " + fillChar("-s | -serverCertificate [certiifcate-name]", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Displays list of Server certificates.If supplied with the Certificate Name then, it displays the details of that Certificate. ", 30));
		out.close();
		return stringWriter.toString();
	}
	@Override
	public String getHotkeyHelp() {
		return "{'cert':{'-trustedCertificate':{},'-serverCertificate':{},'-help':{}}}";
	}
}
