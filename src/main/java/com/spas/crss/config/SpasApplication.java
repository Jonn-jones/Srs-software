package com.spas.crss.config;

import com.spas.crss.controller.AcademicSemesterController;
import com.spas.crss.controller.AuthController;
import com.spas.crss.controller.CodManagementController;
import com.spas.crss.controller.CourseAssignmentController;
import com.spas.crss.controller.CourseManagementController;
import com.spas.crss.controller.CoursePrerequisiteController;
import com.spas.crss.controller.HealthController;
import com.spas.crss.controller.LecturerManagementController;
import com.spas.crss.controller.LecturerSpecializationController;
import com.spas.crss.controller.StudentManagementController;
import com.spas.crss.controller.SystemAdminController;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class SpasApplication {

    public static void main(String[] args) throws Exception {

        // Initialize Tomcat server
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8081);

        // Configure web application context
        String docBase = new File("src/main/webapp").getAbsolutePath();
        Context context = tomcat.addContext("", docBase);

        // HEALTH CONTROLLER
        Tomcat.addServlet(context, "HealthServlet", new HealthController());
        context.addServletMappingDecoded("/api/health", "HealthServlet");

        // AUTHENTICATION CONTROLLER
        Tomcat.addServlet(context, "AuthServlet", new AuthController());
        context.addServletMappingDecoded("/api/auth/*", "AuthServlet");

        // SYSTEM ADMIN CONTROLLER
        Tomcat.addServlet(
                context,
                "SystemAdminServlet",
                new SystemAdminController()
        );
        context.addServletMappingDecoded("/api/admin/*", "SystemAdminServlet");

        // STUDENT MANAGEMENT CONTROLLER
        Tomcat.addServlet(
                context,
                "StudentManagementServlet",
                new StudentManagementController()
        );
        context.addServletMappingDecoded(
                "/api/students/*",
                "StudentManagementServlet"
        );

        // LECTURER MANAGEMENT CONTROLLER
        Tomcat.addServlet(
                context,
                "LecturerManagementServlet",
                new LecturerManagementController()
        );
        context.addServletMappingDecoded(
                "/api/lecturers/*",
                "LecturerManagementServlet"
        );

        // COD MANAGEMENT CONTROLLER
        Tomcat.addServlet(
                context,
                "CodManagementServlet",
                new CodManagementController()
        );
        context.addServletMappingDecoded("/api/cods/*", "CodManagementServlet");

        // COURSE MANAGEMENT CONTROLLER
        Tomcat.addServlet(
                context,
                "CourseManagementServlet",
                new CourseManagementController()
        );
        context.addServletMappingDecoded(
                "/api/courses/*",
                "CourseManagementServlet"
        );

        // ACADEMIC SEMESTER CONTROLLER
        Tomcat.addServlet(
                context,
                "AcademicSemesterServlet",
                new AcademicSemesterController()
        );
        context.addServletMappingDecoded(
                "/api/semesters/*",
                "AcademicSemesterServlet"
        );

        // LECTURER SPECIALIZATION CONTROLLER
        Tomcat.addServlet(
                context,
                "LecturerSpecializationServlet",
                new LecturerSpecializationController()
        );
        context.addServletMappingDecoded(
                "/api/specializations/*",
                "LecturerSpecializationServlet"
        );

        // COURSE ASSIGNMENT CONTROLLER
        Tomcat.addServlet(
                context,
                "CourseAssignmentServlet",
                new CourseAssignmentController()
        );
        context.addServletMappingDecoded(
                "/api/course-assignments/*",
                "CourseAssignmentServlet"
        );

        // COURSE PREREQUISITE CONTROLLER
        Tomcat.addServlet(
                context,
                "CoursePrerequisiteServlet",
                new CoursePrerequisiteController()
        );
        context.addServletMappingDecoded(
                "/api/course-prerequisites/*",
                "CoursePrerequisiteServlet"
        );

        // Initialize connector before starting Tomcat
        tomcat.getConnector();

        // Start server
        tomcat.start();

        System.out.println("----------------------------------------");
        System.out.println("SPAS APPLICATION STARTED");
        System.out.println("----------------------------------------");
        System.out.println("Server: http://localhost:8081");
        System.out.println("Health: /api/health");
        System.out.println("Authentication: /api/auth/*");
        System.out.println("System Admin: /api/admin/*");
        System.out.println("Students: /api/students/*");
        System.out.println("Lecturers: /api/lecturers/*");
        System.out.println("CoD Management: /api/cods/*");
        System.out.println("Courses: /api/courses/*");
        System.out.println("Academic Semesters: /api/semesters/*");
        System.out.println("Specializations: /api/specializations/*");
        System.out.println("Course Assignments: /api/course-assignments/*");
        System.out.println("Course Prerequisites: /api/course-prerequisites/*");
        System.out.println("----------------------------------------");

        // Keep server running
        tomcat.getServer().await();
    }
}