package com.driver;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {
    WhatsappRepository whatsappRepository=new WhatsappRepository();
    public String createUser(String name, String mobile) throws Exception {
        boolean check=whatsappRepository.checkMobileNumberExist(mobile);
        if(!check){
            whatsappRepository.saveUser(name,mobile);
        }else {
            throw new Exception("User already exists");
        }
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        Group group;
        User user1=users.get(0);
        User user2=users.get(1);
        if(users.size() == 2){
            group=new Group(user2.getName(),users.size());
        }else{
            whatsappRepository.updateCount();
            int count= whatsappRepository.getCount();
            group=new Group("Group "+count,users.size());
        }
        whatsappRepository.addAdmin(group,user1);
        whatsappRepository.addGroup(group,users);
        return group;
    }

    public int createMessage(String content) {
        whatsappRepository.updateId();
        int id= whatsappRepository.getMessageId();
        Message message=new Message(id,content);
        whatsappRepository.addMessage(message);
        return id;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!whatsappRepository.groupExistInAdminDb(group)){
            throw new Exception("Group does not exist");
        }
        List<User> users=whatsappRepository.getUserList(group);
        boolean flag=false;
        for(User user:users){
            if(user.getName().equals(sender.getName()) && user.getMobile().equals(sender.getMobile())){
                flag=true;
                break;
            }
        }
        if(!flag){
            throw new Exception("You are not allowed to send message");
        }
        whatsappRepository.addMessageUser(message,sender);
        whatsappRepository.addGroupMessage(group,message);

        return whatsappRepository.getGroupMessageCount(group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!whatsappRepository.groupExistInAdminDb(group)){
            throw new Exception("Group does not exist");
        }
        User user1=whatsappRepository.getAdmin(group);
        if(!user1.getName().equals(approver.getName()) || user1.getMobile().equals(approver.getMobile())){
            throw new Exception("Approver does not have rights");
        }

        List<User> users=whatsappRepository.getUserList(group);
        boolean flag=false;
        for(User u:users){
            if(u.getName().equals(user.getName()) && u.getMobile().equals(user.getMobile())){
                flag=true;
                break;
            }
        }
        if(!flag){
            throw new Exception("User is not a participant");
        }
        whatsappRepository.addAdmin(group,user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        Group group=whatsappRepository.getGroupByUser(user);
        if (group==null){
            throw new Exception("User not found");
        }
        User admin=whatsappRepository.getAdmin(group);
        if(admin.getMobile().equals(user.getMobile()) && admin.getName().equals(user.getName())){
            throw new Exception("Cannot remove admin");
        }
        return whatsappRepository.removeUser(group,user);
    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        List<Message> messages= whatsappRepository.findMessage(start,end,k);
        if(messages.isEmpty()){
            throw new Exception("K is greater than the number of messages");
        }
        int maxMessage=Integer.MIN_VALUE;
        Message message=null;
        for(Message message1:messages){
            if(message1.getId()>maxMessage){
                maxMessage=message1.getId();
                message=message1;
            }
        }
        return message==null?"null":message.getContent();
    }
}
