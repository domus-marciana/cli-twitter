/*
	COMMAND LINE INTERFACE TWITTER CLIENT
	COPYRIGHT (C) ZEE ZUO 2009

	This file is part of CliTweet.

	CliTweet is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	CliTweet is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License 
	along with CliTweet.  If not, see <http://www.gnu.org/licenses/>.
*/

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
			"r: Reply to the mentions received." + "\n" +
			"s: Logout and delete the current user's data." + "\n" +
			"h: Display this help message." + "\n" +
			"n: Display the license message." + "\n" +
			"q: Exit." + "\n"
			);
	}

	public static void printLicense()
	{
		System.out.print(
			"CliTweet (cli-twitter) is licensed under the GNU General Public License." + "\n" +
			"For a copy of the GNU GPL, see <http://www.gnu.org/copyleft/gpl.html>." + "\n" +
			"Twitter4j (by Yusuke Yamamoto) is the library used in this program." + "\n" +
			"For a copy of twitter4j, see <http://yusuke.homeip.net/>." + "\n" +
			"Twitter4j is licensed under the BSD License. It is NOT included in the source code of this program. However, the binary files are released with this JAR file. This version of the library is re-licensed under the GNU General Public License, as permitted by the BSD License." + "\n"
			);
	}
	
	public static void processCommand(String command)
	{
		// System.out.println("DEBUG: command.length() == " + command.length());

		if(command.length() == 0) return;
	
		String yn = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			switch(command.charAt(0))
			{
			case 'q':
				quitMe();
				break;
			case 'n':
				printLicense();
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
			case 'r':
				tc.listMentions();
				int id; String msg;
				System.out.print("Which mention do you want to reply to (q to break)? ");
				try
				{
					String line=in.readLine();
					if(line.charAt(0)=='q' || line.charAt(0)=='Q') return;
					id = Integer.parseInt(line);
					System.out.print("Type your message: ");
					msg = in.readLine();
				}
				catch(NumberFormatException nfe)
				{
					System.out.println("Illegal input!");
					return;
				}
				catch(IOException ioe)
				{
					System.out.println("An I/O Exception occurred.");
					return;
				}
				
				System.out.print("Successfully updated your status message to: " + tc.sendReplyMsg(id, msg));
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
			System.out.print(tc.getUsername() + "> ");
			
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
