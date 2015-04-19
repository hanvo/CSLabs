import java.io.*;
import java.util.*;
import java.sql.*;

public class Project4 {
	static Scanner scan;
	static File file;
	static Connection conn;
	

	
	
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
	
	public static void InputFile(String fp){
		String filename = fp;
        file = new File( filename );
        try { 
        	scan = new Scanner( file );
        } 
        catch ( FileNotFoundException e ){
            System.out.println(e);
        }
	}

	public static void Database(){
		try {
			Class.forName( "oracle.jdbc.driver.OracleDriver" );
		}
		catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection( "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep","phand", "aWKd9gtk" );
		}
		catch ( SQLException e ){
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {		
		if ( args.length < 1 ) {
            System.out.println("Need an input file");
            return;
        }
		else{
			InputFile(args[0]);
			Database();
			int x;
			
			while(scan.hasNext()){
				String line = scan.nextLine();
				String inputSplit[] = line.split("\\s+");
				 switch (inputSplit[0]) {
		            case "LOGIN":  
		            	System.out.println("Login");
		            default:
		            	System.out.println("End");
				 }

			}

			
			
			
            System.out.println("EOF"); 
			scan.close();

		}
		
	}

}
