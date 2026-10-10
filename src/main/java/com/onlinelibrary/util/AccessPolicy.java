package com.onlinelibrary.util;
import com.onlinelibrary.exception.UnauthorizedAccessException;
public final class AccessPolicy {private AccessPolicy(){} public static void requireRole(String actual,String required)throws UnauthorizedAccessException{if(actual==null||!actual.equals(required))throw new UnauthorizedAccessException("This action requires role "+required);}}
