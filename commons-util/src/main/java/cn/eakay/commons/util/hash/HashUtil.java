package cn.eakay.commons.util.hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

 
/**
 * 
 * @author hymagic
 *
 */
public class UtilHash
{
	
	public  final static  String SHA1="SHA-1";
	public  final static  String SHA256="SHA-256";
	public  final static  String MD5="MD5";
	public  final static  String UTF8="UTF-8";
	public  final static  String GBK="GBK";
	/**
	 * 
	 * @param text 需要转换成哈希的文本
	 * @param hashStr  算法参数
	 * @param code  编码格式
	 * @return
	 */
	public  static String getHash(String text,String hashStr,String code)
	{
		        String output=null;
		        MessageDigest digest;
		        try {
		            digest = MessageDigest.getInstance(hashStr);
		            byte[] hash = digest.digest(text.getBytes(code));
		            output = Hex.encodeHexString(hash);
		            return output;
		            
		        } catch (NoSuchAlgorithmException e) {
		           
		            e.printStackTrace();
		        }catch (UnsupportedEncodingException e) {

		        	e.printStackTrace();
		        }
		        return output;
		       
	}
}
