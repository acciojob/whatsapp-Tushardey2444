package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String,User> userDb=new HashMap<>();
    private HashMap<Integer,Message> messageDb=new HashMap<>();
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public boolean checkMobileNumberExist(String mobile){
        return userMobile.contains(mobile);
    }
    public void saveUser(String name,String mobile){
        userDb.put(mobile,new User(name,mobile));
        userMobile.add(mobile);
    }
    public void updateCount(){
        customGroupCount++;
    }
    public int getCount(){
        return customGroupCount;
    }
    public void addAdmin(Group group,User user){
        adminMap.put(group,user);
    }
    public void addGroup(Group group,List<User> users){
        groupUserMap.put(group,users);
    }
    public void updateId(){
        messageId++;
    }
    public int getMessageId(){
        return messageId;
    }

    public void addMessage(Message message) {
        messageDb.put(message.getId(),message);
    }
    public boolean groupExistInAdminDb(Group group){
        return adminMap.containsKey(group);
    }
    public List<User> getUserList(Group group){
        return groupUserMap.get(group);
    }
    public void addMessageUser(Message message,User sender){
        senderMap.put(message,sender);
    }

    public void addGroupMessage(Group group, Message message) {
        if(!groupMessageMap.containsKey(group)){
            groupMessageMap.put(group,new ArrayList<>());
        }
        groupMessageMap.get(group).add(message);
    }

    public int getGroupMessageCount(Group group) {
        return groupMessageMap.get(group).size();
    }
    public User getAdmin(Group group){
        return adminMap.get(group);
    }

    public Group getGroupByUser(User user) {
        for(Group group:groupUserMap.keySet()){
            List<User> users=groupUserMap.get(group);
            for(User u:users){
                if(u.getName().equals(user.getName()) && u.getMobile().equals(user.getMobile())){
                    return group;
                }
            }
        }
        return null;
    }

    public int removeUser(Group group, User user) {
        List<User> users=groupUserMap.get(group);
        int numberOfUsers=0;
        int numberOfMessages=0;
        int overallMessages=0;

        if(users.size()==2){
            groupUserMap.remove(group);
            List<Message> messages=groupMessageMap.get(group);
            for(Message message:messages){
                senderMap.remove(message);
            }
            groupMessageMap.remove(group);
        }else{
            users.remove(user);
            groupUserMap.put(group,users);
            numberOfUsers=users.size();

            List<Message> messages=groupMessageMap.get(group);
            for(Message message:messages){
                User user1=senderMap.get(message);
                if(user1.getName().equals(user.getName()) && user1.getMobile().equals(user.getMobile())) {
                    senderMap.remove(message);
                    messages.remove(message);
                }
            }
            groupMessageMap.put(group,messages);
            numberOfMessages=messages.size();


        }
        for(Group group1:groupMessageMap.keySet()){
            overallMessages+=groupMessageMap.get(group1).size();
        }
        return numberOfUsers+numberOfMessages+overallMessages;
    }

    public List<Message> findMessage(Date start, Date end, int k)  {
        List<Message> messagesInBetween=new ArrayList<>();
        for(Group group:groupMessageMap.keySet()){
            List<Message> messages=groupMessageMap.get(group);
            for(Message message:messages){
                if(message.getTimestamp().compareTo(start)>0 && message.getTimestamp().compareTo(end)<0){
                    if(message.getId()>=k){
                        messagesInBetween.add(message);
                    }
                }
            }
        }
        return messagesInBetween;
    }
}
