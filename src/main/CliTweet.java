package main;
import java.io.*;

import twitter4j.*;

public class CliTweet
{
	/**
	 * @param args
	 */
	
	static TwitterController tc;
	
	public static void initialize()
	{
		TokenManager myTokenMan = new TokenManager();
		Twitter twitter = myTokenMan.getTwitter();
		String username = myTokenMan.getUsername();
		tc = new TwitterController(twitter, username);
	}
	
	public static void quitMe()
	{
		System.exit(0);
	}
	
	public static void printHelp()
	{
		System.out.print(
			"CliTweet " + Global.MY_VERSION + "\n" +
			"User's Guide" + "\n\n" +
			"Command List:" + "\n" +
			"a[n|u]: Follow (n) or stop following (u) another user." + "\n" +
			"b[n|u]: Block (n) or unblock (u) another user." + "\n" +
			"u [Status message]: Update the authenticated user's status message." + "\n" +
			"l[f|b]: List followed users (f) or followers (b) of the authenticated user." + "\n" +
			"ld: List all direct messages received by the authenticated user." + "\n" +
			"d: Send a direct message." + "\n" +
			"t[m|o] [Another user's name if needed]: Show the timeline of the authenticated user (m), another user (o), or the user's friends (nothing)." + "\n" +
			"f [Username]: Find another user." + "\n" +
			"s: Logout and delete the current user's data." + "\n" +
			"h: Display this help message." + "\n" +
			"q: Exit." + "\n"
			);
	}
	
	public static void processCommand(String command)
	{
		String yn = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			switch(command.charAt(0))
			{
			case 'q':
				quitMe();
				break;
			case 'u':
				if(command.length() < 3) { System.out.print("Update your status message to: "); try {
					command = "u " + in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} }
				tc.updateStatus(command.substring(2));
				System.out.println("Successfully updated your status message to: " + command.substring(2));
				break;
			case 'l':
				if(command.length() > 1 && command.charAt(1) == 'b') tc.listFollowers();
				else if(command.length() > 1 && command.charAt(1) == 'f') tc.listFollowing();
				else if(command.length() > 1 && command.charAt(1) == 'd') tc.listDirectMessages();
				else System.out.println("Unrecognized command. Type h for help.");
				break;
			case 't':
				if(command.length() > 1 && command.charAt(1) == 'm') tc.getMyTimeline();
				else if(command.length() > 1 && command.charAt(1) == 'o') tc.getOthersTimeline(command.substring(3));
				else tc.getTimeline();
				break;
			case 'd':
				tc.listFollowers();
				int uid = -1;
				while(uid == -1)
				{
					System.out.print("Who do you want to send the message to (0 = cancel)? ");
					try {
						uid = Integer.parseInt(in.readLine());
						if(uid == 0) { System.out.println("Operation canceled."); return; }
						if(uid < 1 || uid > tc.getFollowersNumber()) throw new NumberFormatException();
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						System.out.println("Illegal input.");
						uid = -1;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("IO Exception.");
					}
				}
				
				System.out.print("Type your message: ");
				String message = "";
					try {
						message = in.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("IO Exception.");
					}
				tc.sendDirectMessage(uid, message);
				
				break;
			case 's':
				while(yn.length() == 0 ||( yn.charAt(0) != 'y' && yn.charAt(0) != 'Y' && yn.charAt(0) != 'N' && yn.charAt(0) != 'n'))
				{
					System.out.print("Are you sure you want to log out? (Y/N) ");
					try {
						yn = in.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("IO Exception.");
					}
				}
				if(yn.length() == 0) { yn = ""; break; }
				if(yn.charAt(0) != 'Y' && yn.charAt(0) != 'y') { yn = ""; break; }
				try {
			        BufferedWriter out = new BufferedWriter(new FileWriter("token.txt"));
			        out.write("");
			        out.close();
			    } catch (IOException e) {
			    	System.out.println("IO Exception.");
			    }
			    yn = "";
			    initialize();
			    break;
			case 'f':
				if(command.length() < 3) { System.out.println("Need parameter. Type h for help."); return; }
				tc.findUser(command.substring(2));
				break;
			case 'a':
				if(command.length() < 3) { System.out.println("Need parameter. Type h for help."); return; }
				if(command.charAt(1) == 'n') tc.addFriend(command.substring(3));
				else if(command.charAt(1) == 'u') tc.removeFriend(command.substring(3));
				else System.out.println("Unrecognized command. Type h for help.");
				break;
			case 'b':
				if(command.length() < 4) { System.out.println("Need parameter. Type h for help."); return; }
				if(command.charAt(1) == 'n') tc.blockUser(command.substring(3));
				else if(command.charAt(1) == 'u') tc.unblockUser(command.substring(3));
				else System.out.println("Unrecognized command. Type h for help.");
				break;
			case 'h':
				printHelp();
				break;
			default:
				System.out.println("Unrecognized command. Type h for help.");
				break;
			}
		}
		catch(TwitterException e)
		{
			System.out.println("Operation failed! Cannot connect to the Twitter server, or the specified user does not exist.");
			return;
		}
	}
	
	public static void main(String[] args) {
		System.out.println("CliTweet v" + Global.MY_VERSION);
		System.out.println("By Zee Zuo\n");
		
		initialize();
		
		String command = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			System.out.print(tc.getUsername() + ">");
			
			try {
				command = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			processCommand(command);
		}
	}

}
