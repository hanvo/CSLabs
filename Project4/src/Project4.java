
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
	
	public static String VigDecrypt(String cipher, String key){
		String kprime = "";
		String plain = "";
		int x;
		int check;

		
		cipher = cipher.toUpperCase();
		key = key.toUpperCase();
		
		int cipherLen = cipher.length();
		int keyLen = key.length();
		
		int division = cipherLen / keyLen;
		
		for(x = 0; x < division; x++){
			kprime += key;
		}
		
		if(cipherLen != kprime.length()){
			int difference = cipherLen - (x * keyLen);
			for(x = 0; x < difference; x++){
				kprime += key.charAt(x);
			}
		}

		for(x = 0; x < cipherLen; x++){
			int cipherCode = (char)cipher.charAt(x);
			int kprimeCode = (char)kprime.charAt(x);
			
			int cipherAlphPos = cipherCode - 65;
			int kprimeAlphaPos = kprimeCode - 65;

			int plainAlphaPos = cipherAlphPos - kprimeAlphaPos;
			if((check = cipherAlphPos - kprimeAlphaPos) < 0){
				check = Math.abs(check);
				plainAlphaPos = 26 - check;
			} else {
				plainAlphaPos = Math.abs(plainAlphaPos % 26);
			}
			
			plain += Character.toString((char)(plainAlphaPos + 65));
		}
		return plain;
	}
	
	public static void main(String[] args) {
		System.out.println(VigEncrypt("BEWARETHEJABBERWOCKMYSONTHEJAWSTHATBITETHECLAWSTHATCATCH","VIGENERECIPHER"));
		System.out.println(VigDecrypt("WMCEEIKLGRPIFVMEUGXQPWQVIOIAVEYXUEKFKBTALVXTGAFXYEVKPAGY", "VIGENERECIPHER"));
	}

}
