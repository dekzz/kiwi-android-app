package kiwi.cryptography;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cryptographic hashing (one way function)
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class SHA1 {
	
	/**
	 * Hash function
	 * @param toHash Plain text string
	 * @return Irreversible string
	 */
	public static String hash(String toHash)
	{
		String hash = null;
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(toHash.getBytes(), 0, toHash.length());
			hash = new BigInteger(1, digest.digest()).toString();
		}
		catch (NoSuchAlgorithmException e){
		}
		return hash;
	}

}
