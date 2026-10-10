package com.onlinelibrary.model;
public class User {
 private long id; private String membershipId,name,email,passwordHash,role,status;
 public User() {} public User(long id,String membershipId,String name,String email,String passwordHash,String role,String status){this.id=id;this.membershipId=membershipId;this.name=name;this.email=email;this.passwordHash=passwordHash;this.role=role;this.status=status;}
 public long getId(){return id;} public void setId(long v){id=v;} public String getMembershipId(){return membershipId;} public void setMembershipId(String v){membershipId=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;} public String getRole(){return role;} public void setRole(String v){role=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
