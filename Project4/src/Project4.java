import java.io.*;
import java.util.*;
import java.sql.*;

public class Project4 {
	static Scanner scan;
	static File file;
	boolean isAdmin = false;
	Connection conn;
	
	static int roleID = 2;
	static int userID = 2; 
	
	
	
	

	public Project4(){
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
	
	public void InputFile(String fp){
		String filename = fp;
		boolean loginSuccessful;
		filename = "/Users/QuakeZ/Desktop/CSLabs/Project4/src/input.txt";
        file = new File( filename );
        try { 
        	scan = new Scanner( file );
        	while(scan.hasNext()){
        		String line = scan.nextLine();
        		String[] commands = line.split(" ");
        			
        		switch (commands[0]) {
        			case "LOGIN":
        				do{
        					//System.out.println("Login Procedure");
        					String userName = commands[1];
        					String pass = commands[2];
        					loginSuccessful= loginCheck(userName, pass);
        					if(loginSuccessful)
        						System.out.println("Login successful");
        					else
        						System.out.println("Invalid login");
        				} while(!loginSuccessful && scan.hasNext());
        				break;
        			case "CREATE":
        				//System.out.println("Create Procedure");
        				switch (commands[1]){
        					case "USER":
        						System.out.println("USER");
        						String username = commands[2];
        						String password = commands[3];
        						boolean userCreationSucc = createUser(username,password);
        						if(userCreationSucc)
        							System.out.println("User created successfully");        						
        						break;
        					case "ROLE":
        						System.out.println("ROLE");
        						String roleName = commands[2];
        						String encKey = commands[3];
        						boolean roleCreationSucc = createRole(roleName,encKey);
        						if(roleCreationSucc)
        							System.out.println("Role created successfully");
        						break;        						
        				}
        				break;
        			case "GRANT":
        				System.out.println("Grant Procedure");
        				switch (commands[1]){
    						case "ROLE":
    							System.out.println("Granting Role");
    							String grantUser = commands[2];
    							String grantRole = commands[3];
    							boolean grantRoleSucc = grantRole(grantUser,grantRole);
    							if(grantRoleSucc)
    								System.out.println("Role assigned successfully");
    							break;
    						case "PRIVILEGE":
    							System.out.println("Granting Privilege");
    							break;
        				}
        				break;
        				
        		}	
        	}
        } 
        catch ( FileNotFoundException e ){
            System.out.println(e);
        }
        scan.close();
	}
	
	public boolean loginCheck(String user, String pass)
	{
		boolean ret = false;
		String query = "SELECT * FROM users WHERE (USERNAME = \'" + user + "\' AND PASSWORD = \'" + pass + "\')";
		//System.out.println(query);
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( query );
			if(rs.next()) {
				String account = rs.getString("USERNAME");
				if(account.equals("admin"))
					isAdmin = true;
				ret = true;
			}
			rs.close();
			stmt.close();
		}
		catch ( SQLException e ) {
			e.printStackTrace();
		}		
		return ret;
	}

	public boolean createRole(String roleName, String encKey)
	{
		boolean ret = false;
		if(isAdmin)
		{
			System.out.println("Admin Status");
			
			String query = "INSERT INTO Roles VALUES(" + roleID + "," + "\'" + roleName + "\', \'" + encKey + "\')"; 
			//System.out.println(query);
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate( query );
				stmt.close();
			}
			catch ( SQLException e ) {
				System.out.println(e);
			}
			roleID++;
			ret = true;
		}
		else
			System.out.println("Authorization failure");
		
		return ret;
	}

	public boolean createUser(String username, String password)
	{
		boolean ret = false;
		if(isAdmin) {
			System.out.println("Granted");
			String query = "INSERT INTO Users VALUES(" + userID + "," + "\'" + username + "\', \'" + password + "\')"; 
			System.out.println(query);
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate( query );
				stmt.close();
			}
			catch ( SQLException e ) {
				System.out.println(e);
			}
			userID++;			
			ret = true;	
		}
		else {
			System.out.println("Authorization failure");
		}
		return ret;
	}
	
	public boolean grantRole(String grantUser, String grantRoleName){
		boolean ret = false;
		if(isAdmin){
			System.out.println("Admin Status");
			int user = getUserID(grantUser);
			int role = getRoleID(grantRoleName);
			
			String update = "INSERT INTO userroles VALUES(" + user + "," + role + ")";
			System.out.println(update);
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate( update );
				stmt.close();
			}
			catch ( SQLException e ) {
				System.out.println(e);
			}
			ret = true;
		}
		else {
			System.out.println("Authorization failure");
		}
		return ret;
	}
	
	public int getUserID(String user)
	{
		String userid = "";
		int id = -1;
		String query = "SELECT userid FROM users WHERE UserName=\'" + user + "\'";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( query );
			while(rs.next()){
				userid = rs.getString("USERID");
			}
			rs.close();
			stmt.close();
		}
		catch ( SQLException e ) {
			e.printStackTrace();
		}
		id = Integer.parseInt(userid);
		return id;
		
	}
	
	public int getRoleID(String role){
		String roleid = "";
		int id = -1;
		String query = "SELECT roleid FROM roles WHERE RoleName=\'" + role + "\'";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( query );
			while(rs.next()){
				roleid = rs.getString("ROLEID");
			}
			rs.close();
			stmt.close();
		}
		catch ( SQLException e ) {
			e.printStackTrace();
		}
		id = Integer.parseInt(roleid);
		return id;
	}
	
	
	
	public static void main(String[] args) {		
		//need to change this once i get project finish. change to unix 
		Project4 p4 = new Project4();
		
		if ( args.length < -1 ) {
            System.out.println("Need an input file");
            return;
        }
		else{
			p4.InputFile("arg");
		}
	}
}
