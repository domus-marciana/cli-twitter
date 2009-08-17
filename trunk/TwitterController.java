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

import java.util.Iterator;
import java.io.*;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class TwitterController
{
	Twitter twitter;
	String username;
	
	public TwitterController(Twitter tw, String uname)
	{
		twitter = tw;
		username = uname;
	}
	
	public void updateStatus(String statMsg) throws TwitterException
	{
		twitter.updateStatus(statMsg);
	}
	
	public void listFollowers() throws TwitterException
	{
		int counter = 0;
		List<User> followersList = twitter.getFollowers(username);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Iterator<User> iterator = followersList.iterator();
		while (iterator.hasNext())
		{
			User u = iterator.next();
			if(u.getStatusText() == null) System.out.println(++counter + " " + printUser(u));
			else System.out.println(++counter + " " + printUser(u) + ": " + u.getStatusText());
			if(counter % 10 == 0)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public void listFollowing() throws TwitterException
	{
		int counter = 0;
		List<User> followingList = twitter.getFriends(username);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Iterator<User> iterator = followingList.iterator();
		while (iterator.hasNext())
		{
			User u = iterator.next();
			if(u.getStatusText() == null) System.out.println(++counter + " " + printUser(u));
			else System.out.println(++counter + " " + printUser(u) + ": " + u.getStatusText());
			if(counter % 10 == 0)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public void listMentions() throws TwitterException
	{
		int counter = 0;
		List<Status> mentions = twitter.getMentions();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Iterator<Status> iterator = mentions.iterator();
		while (iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + printUser(s.getUser()) + ": " + s.getText());
			if(counter % 10 == 0)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public String sendReplyMsg(int id, String msg) throws TwitterException
	{
		List<Status> mentions = twitter.getMentions();
		String mtStr = "@" + mentions.get(id-1).getUser().getScreenName();
		if(msg.indexOf(mtStr) == -1) msg = mtStr + " " + msg;
		this.updateStatus(msg);
		return msg;
	}
	
	public void findUser(String uname) throws TwitterException
	{
		User u = twitter.getUserDetail(uname);
		if(twitter.existsFriendship(username, uname) && twitter.existsFriendship(uname, username)) System.out.print("Mutual friend: ");
		else if(twitter.existsFriendship(username, uname)) System.out.print("Following: ");
		else if(twitter.existsFriendship(uname, username)) System.out.print("Followed by: ");
		else System.out.print("Stranger: ");
		if(u.getStatusText() == null) System.out.println(printUser(u));
		else System.out.println(printUser(u) + ": " + u.getStatusText());
	}
	
	public void getMyTimeline() throws TwitterException
	{
		int counter = 0;
		List<Status> statusList = twitter.getUserTimeline();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + s.getText());
			if(counter == 10)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public void getOthersTimeline(String uname) throws TwitterException
	{
		User user = twitter.getUserDetail(uname);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(printUser(user));
		
		List<Status> statusList = twitter.getUserTimeline(uname);
		int counter = 0;
		
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + s.getText());
			if(counter % 10 == 0)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public void getTimeline() throws TwitterException
	{
		List<Status> statusList = null;
		int counter = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		statusList = twitter.getFriendsTimeline();
		
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + printUser(s.getUser()) + ": " + s.getText());
			if(counter % 10 == 0)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public int getFollowersNumber() throws TwitterException
	{
		int followersNumber = 0;

		followersNumber = twitter.getFollowers().size();
		
		return followersNumber;
	}
	
	public void sendDirectMessage(int uid, String text) throws TwitterException
	{
		List<User> followersList = twitter.getFollowers();
		
		String uname = followersList.get(uid-1).getScreenName();
		twitter.sendDirectMessage(uname, text);
		
		System.out.println("Message sent.");
	}
	
	public void listDirectMessages() throws TwitterException
	{
		List<DirectMessage> statusList = null;
		int counter = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		statusList = twitter.getDirectMessages();
		
		Iterator<DirectMessage> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			DirectMessage s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + printUser(s.getSender()) + " to "
					+ printUser(s.getRecipient()) + ": " + s.getText());
			if(counter % 10 == 0)
			{
				System.out.print("Hit [Enter] to continue, or type q to break: ");
				String str = null;
				try {
					str = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(str.length() > 0 && (str.charAt(0) == 'Q' || str.charAt(0) == 'q')) return;
			}
		}
	}
	
	public void addFriend(String uname) throws TwitterException
	{
		if(twitter.existsFriendship(username, uname))
		{
			System.out.println("You are already following " + printUser(twitter.getUserDetail(uname)) + ".");
			return;
		}
		
		twitter.createFriendship(uname);
			
		System.out.println("Successful! You are now following " + printUser(twitter.getUserDetail(uname)) + ".");
	}
	
	public void removeFriend(String uname) throws TwitterException
	{
		if(!twitter.existsFriendship(username, uname))
		{
			System.out.println("You are not following " + printUser(twitter.getUserDetail(uname)) + ".");
			return;
		}
		
		twitter.destroyFriendship(uname);
		
		System.out.println("Successful! You are no longer following " + printUser(twitter.getUserDetail(uname)) + ".");
	}
	
	public void blockUser(String uname) throws TwitterException
	{
		if(twitter.existsBlock(uname))
		{
			System.out.println("You have already blocked " + printUser(twitter.getUserDetail(uname)) + ".");
			return;
		}
		
		twitter.createBlock(uname);

		System.out.println("Successful! " + printUser(twitter.getUserDetail(uname)) + " is now blocked.");
	}
	
	public void unblockUser(String uname) throws TwitterException
	{
		if(!twitter.existsBlock(uname))
		{
			System.out.println("You have already unblocked " + printUser(twitter.getUserDetail(uname)) + ".");
			return;
		}
		
		twitter.destroyBlock(uname);
		
		System.out.println("Successful! " + printUser(twitter.getUserDetail(uname)) + " is now unblocked.");
	}
	
	public String printUser(User u)
	{
		return u.getName() + " (" + u.getScreenName() + ")";
	}
	
	public String getUsername()
	{
		return username;
	}
}
