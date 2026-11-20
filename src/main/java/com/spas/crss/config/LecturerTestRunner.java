package com.spas.crss.config;

import com.spas.crss.dao.LecturerDAO;
import com.spas.crss.model.Lecturer;

import java.util.List;

public class LecturerTestRunner {

    public static void main(String[] args) {

        LecturerDAO lecturerDAO = new LecturerDAO();

        System.out.println("====================================");
        System.out.println("       SPAS CRSS LECTURER RECORDS");
        System.out.println("====================================");

        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();

        if (lecturers.isEmpty()) {
            System.out.println("No lecturer records found.");
        } else {

            for (Lecturer lecturer : lecturers) {

                System.out.println("Lecturer ID: " + lecturer.getLecturerId());
                System.out.println("Name: " + lecturer.getName());
                System.out.println("Qualification: " + lecturer.getQualification());
                System.out.println("Email: " + lecturer.getEmail());
                System.out.println("Department: " + lecturer.getDepartment());

                System.out.println("------------------------------------");
            }
        }

        System.out.println("Total Lecturers: " + lecturers.size());
        System.out.println("====================================");
    }
}