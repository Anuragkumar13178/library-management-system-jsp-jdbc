package com.onlinelibrary.thread;
import jakarta.servlet.*; import jakarta.servlet.annotation.WebListener;
@WebListener public class NotificationWorkerListener implements ServletContextListener {private NotificationWorker worker;public void contextInitialized(ServletContextEvent e){worker=new NotificationWorker();worker.start();}public void contextDestroyed(ServletContextEvent e){if(worker!=null)worker.close();}}
