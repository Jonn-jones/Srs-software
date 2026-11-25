package com.spas.crss.config;

import com.spas.crss.controller.AuthController;
import com.spas.crss.controller.HealthController;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class SpasApplication {

    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();

        tomcat.setPort(8081);
        tomcat.getConnector();

        String docBase = new File("src/main/webapp")
                .getAbsolutePath();

        Context context = tomcat.addContext("", docBase);

        // Health API
        Tomcat.addServlet(
                context,
                "healthController",
                new HealthController()
        );

        context.addServletMappingDecoded(
                "/api/health",
                "healthController"
        );

        // Authentication API
        Tomcat.addServlet(
                context,
                "authController",
                new AuthController()
        );

        context.addServletMappingDecoded(
                "/api/auth/*",
                "authController"
        );

        // Start server
        tomcat.start();

        System.out.println("======================================");
        System.out.println("       SPAS BACKEND SERVER");
        System.out.println("======================================");
        System.out.println("Server: http://localhost:8081");
        System.out.println("Health: http://localhost:8081/api/health");
        System.out.println("Login:  http://localhost:8081/api/auth/login");
        System.out.println("Logout: http://localhost:8081/api/auth/logout");
        System.out.println("======================================");

        tomcat.getServer().await();
    }
}