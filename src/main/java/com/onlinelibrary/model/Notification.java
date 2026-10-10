package com.onlinelibrary.model;
import java.time.LocalDateTime;
public class Notification { private long id,memberId; private String title,message,type; private boolean read; private LocalDateTime createdAt;
 public long getId(){return id;} public void setId(long v){id=v;} public long getMemberId(){return memberId;} public void setMemberId(long v){memberId=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public String getType(){return type;} public void setType(String v){type=v;} public boolean isRead(){return read;} public void setRead(boolean v){read=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
