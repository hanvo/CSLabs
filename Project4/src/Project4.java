
public class Project4 {

	public static String VigEncrypt(String plain, String key){
		String kprime = "";
		String cipher = "";
		int x;
		
		plain = plain.toUpperCase();
		key = key.toUpperCase();
		
		int plainLen = plain.length();
		int keyLen = key.length();
		
		int division = plainLen / keyLen;
		
		for(x = 0; x < division; x++){
			kprime += key;
		}
		
		if(plainLen != kprime.length()){
			int difference = plainLen - (x * keyLen);
			for(x = 0; x < difference; x++){
				kprime += key.charAt(x);
			}
		}

		for(x = 0; x < plainLen; x++){
			int plainCode = (char)plain.charAt(x);
			int kprimeCode = (char)kprime.charAt(x);
			
			int plainAlphPos = plainCode - 65;
			int kprimeAlphaPos = kprimeCode - 65;

			int newCodeAlphaPos = plainAlphPos + kprimeAlphaPos;
			newCodeAlphaPos = Math.abs(newCodeAlphaPos % 26);
			
			cipher += Character.toString((char)(newCodeAlphaPos + 65));
		}
		return cipher;
	}
	
	public String VigDecrypt(String Cipher, String Key){
		return "";
	}
	
	public static void main(String[] args) {
		System.out.println(VigEncrypt("databases","abcde"));
	}

}
