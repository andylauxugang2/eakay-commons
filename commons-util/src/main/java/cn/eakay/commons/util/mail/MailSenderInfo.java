package cn.eakay.commons.util.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * 
 * @author hymagic
 *
 */
public class MailSenderInfo {   
    // 发送邮件的服务器的IP和端口   
    private String mailServerHost="http://mail.shanhaishu.com.cn/";   
    private String mailServerPort = "25";   
    // 邮件发送者的地址   
    private String fromAddress="yishengshu@shanhaishu.com.cn";   
    // 邮件接收者的地址   
    private String toAddress;   
    // 邮件抄送   
    private List<String> ccAddress;    
    // 登陆邮件发送服务器的用户名和密码   
    private String userName="yishengshu";   
    private String password="hello1";   
    // 是否需要身份验证   
    private boolean validate = true;   
    // 邮件主题   
    private String subject;   
    // 邮件的文本内容   
    private String content;   
    // 邮件附件的文件名   
    private String[] attachFileNames;     
    
    public MailSenderInfo(){
    	ccAddress = new ArrayList<String>();
    }
    
    public void addccAddress(String cc){
    	ccAddress.add(cc);
    }
    public String[] getccAddress(){
    	return ccAddress.toArray(new String[]{});
    }
    /**  
      * 获得邮件会话属性  
      */   
    public Properties getProperties(){   
      Properties p = new Properties();   
      p.put("mail.smtp.host", this.mailServerHost);   
      p.put("mail.smtp.port", this.mailServerPort);   
      p.put("mail.smtp.auth", validate ? "true" : "false");   
      return p;   
    }   
    public String getMailServerHost() {   
      return mailServerHost;   
    }   
    public void setMailServerHost(String mailServerHost) {   
      this.mailServerHost = mailServerHost;   
    }  
    public String getMailServerPort() {   
      return mailServerPort;   
    }  
    public void setMailServerPort(String mailServerPort) {   
      this.mailServerPort = mailServerPort;   
    }  
    public boolean isValidate() {   
      return validate;   
    }  
    public void setValidate(boolean validate) {   
      this.validate = validate;   
    }  
    public String[] getAttachFileNames() {   
      return attachFileNames;   
    }  
    public void setAttachFileNames(String[] fileNames) {   
      this.attachFileNames = fileNames;   
    }  
    public String getFromAddress() {   
      return fromAddress;   
    }   
    public void setFromAddress(String fromAddress) {   
      this.fromAddress = fromAddress;   
    }  
    public String getPassword() {   
      return password;   
    }  
    public void setPassword(String password) {   
      this.password = password;   
    }  
    public String getToAddress() {   
      return toAddress;   
    }   
    public void setToAddress(String toAddress) {   
      this.toAddress = toAddress;   
    }   
    public String getUserName() {   
      return userName;   
    }  
    public void setUserName(String userName) {   
      this.userName = userName;   
    }  
    public String getSubject() {   
      return subject;   
    }  
    public void setSubject(String subject) {   
      this.subject = subject;   
    }  
    public String getContent() {   
      return content;   
    }  
    public void setContent(String textContent) {   
      this.content = textContent;   
    }   
    
    
    public static void main(String[] args) {
		System.out.println((int) Math.rint(10000*0.05));
	}
}   
