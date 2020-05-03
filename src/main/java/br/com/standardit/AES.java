package br.com.standardit;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
	 public static String decrypt2(String strToDecrypt,String key) {
		 	try {
		 		
		 		byte[] fullCipher  = Base64.getDecoder().decode(strToDecrypt);
		 		byte[] iv = new byte[16];
		 		byte[] cipher = new byte[fullCipher.length - iv.length];
		 		
		 		System.arraycopy(fullCipher, 0, iv, 0, iv.length);
		 		System.arraycopy(fullCipher, iv.length, cipher, 0, fullCipher.length - iv.length);

		 		setKey(strToDecrypt);
	         
	            Cipher cipherInstance = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            cipherInstance.init(Cipher.DECRYPT_MODE, secretKey);

	            String result = new String(cipherInstance.doFinal(fullCipher));
	            return result;
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }

	        return null;
	    }
}
