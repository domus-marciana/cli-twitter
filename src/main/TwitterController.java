package main;

import java.util.Iterator;
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
		
		Iterator<User> iterator = followersList.iterator();
		while (iterator.hasNext())
		{
			User u = iterator.next();
			if(u.getStatusText() == null) System.out.println(++counter + " " + printUser(u));
			else System.out.println(++counter + " " + printUser(u) + ": " + u.getStatusText());
		}
	}
	
	public void listFollowing() throws TwitterException
	{
		int counter = 0;
		List<User> followingList = twitter.getFriends(username);
		
		Iterator<User> iterator = followingList.iterator();
		while (iterator.hasNext())
		{
			User u = iterator.next();
			if(u.getStatusText() == null) System.out.println(++counter + " " + printUser(u));
			else System.out.println(++counter + " " + printUser(u) + ": " + u.getStatusText());
		}
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
		
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + s.getText());
		}
	}
	
	public void getOthersTimeline(String uname) throws TwitterException
	{
		User user = twitter.getUserDetail(uname);
		
		System.out.println(printUser(user));
		
		List<Status> statusList = twitter.getUserTimeline(uname);
		int counter = 0;
		
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + s.getText());
		}
	}
	
	public void getTimeline() throws TwitterException
	{
		List<Status> statusList = null;
		int counter = 0;
		
		statusList = twitter.getFriendsTimeline();
		
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			Status s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + printUser(s.getUser()) + ": " + s.getText());
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
		List<User> followersList = null;
		followersList = twitter.getFollowers();
		
		String uname = followersList.get(uid-1).getScreenName();
		twitter.sendDirectMessage(uname, text);
		
		System.out.println("Message sent.");
	}
	
	public void listDirectMessages() throws TwitterException
	{
		List<DirectMessage> statusList = null;
		int counter = 0;

		statusList = twitter.getDirectMessages();
		
		Iterator<DirectMessage> iterator = statusList.iterator();
		while(iterator.hasNext())
		{
			DirectMessage s = iterator.next();
			System.out.println(++counter + " [" + s.getCreatedAt().toString() + "] " + printUser(s.getSender()) + " to "
					+ printUser(s.getRecipient()) + ": " + s.getText());
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
