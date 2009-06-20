package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class TokenManager
{
	Twitter twitter;
	AccessToken accessToken;
	String username;
	String myToken;
	String myTokenSecret;
	
	public TokenManager()
	{
		initialize();
	}
	
	public void initialize() { // Log the user in
		twitter = new Twitter();
	    twitter.setOAuthConsumer(Global.MY_TOKEN, Global.MY_SECRET);
		
		String YN = "#";
		String PIN = null;
		
		if(readMyToken() == false)
		{
		    RequestToken requestToken = null;
			try {
				requestToken = twitter.getOAuthRequestToken();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in twitter.getOAuthRequestToken()");
			}
		    accessToken = null;
		    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    while (null == accessToken) {
		      System.out.println("Open the following URL and grant access to your account:");
		      System.out.println(requestToken.getAuthorizationURL());
		      System.out.print("Once you are done, type in the PIN code:");
		      try {
				PIN = br.readLine();
			} catch (IOException e) {
			}
		      try{
		        accessToken = twitter.getOAuthAccessToken(requestToken, PIN);
		      } catch (TwitterException te) {
		        if(401 == te.getStatusCode()){
		          System.out.println("Unable to get the access token.");
		        }else{
		          te.printStackTrace();
		        }
		      }
		    }
		    
		    username = accessToken.getScreenName();
		    
		    System.out.print("Access granted. Do you want to save the token for future use? (Y/N) ");
		    while(YN.charAt(0) != 'Y' && YN.charAt(0) != 'y' && YN.charAt(0) != 'N' && YN.charAt(0) != 'n')
		    {
		    	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    	try {
					YN = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    switch(YN.charAt(0))
		    {
		    case 'Y': case 'y':
		    	try {
						storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
					} catch (TwitterException e) {
						System.out.println("twitter.verifyCredentials().getId()");
					}
		    	break;
		    }
		}
		else
		{
			accessToken = new AccessToken(myToken, myTokenSecret);
			twitter.setOAuthAccessToken(accessToken);
		}
	}
	
	private void storeAccessToken(int credentialID, AccessToken at)
	{
		assert(username != "");
		
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("token.txt"));
	        out.write(at.getToken() + '\n');
	        out.write(at.getTokenSecret() + '\n');
	        out.write(username + '\n');
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	private boolean readMyToken()
	{
		try {
			BufferedReader in = new BufferedReader(new FileReader("token.txt"));
			myToken = in.readLine();
			myTokenSecret = in.readLine();
			username = in.readLine();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		if(myToken == null || myTokenSecret == null || username == null) return false;
		
		return true;
	}
	
	public Twitter getTwitter()
	{
		return twitter;
	}
	
	public String getUsername()
	{
		return username;
	}
}
