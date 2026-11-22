package com.spas.crss.config;

import com.spas.crss.dao.AcademicSemesterDAO;
import com.spas.crss.model.AcademicSemester;

public class AcademicSemesterTestRunner {

    public static void main(String[] args) {

        AcademicSemesterDAO semesterDAO = new AcademicSemesterDAO();

        System.out.println("====================================");
        System.out.println("ACADEMIC SEMESTER INTEGRATION TEST");
        System.out.println("====================================");

        AcademicSemester semester = semesterDAO.getActiveSemester();

        if (semester != null) {

            System.out.println("Active semester retrieved successfully!");

            System.out.println("Semester ID: " + semester.getSemesterId());
            System.out.println("Academic Year: " + semester.getAcademicYear());
            System.out.println("Semester Name: " + semester.getSemesterName());
            System.out.println("Start Date: " + semester.getStartDate());
            System.out.println("End Date: " + semester.getEndDate());
            System.out.println("Status: " + semester.getSemesterStatus());

        } else {

            System.out.println("No active semester found.");
        }

        System.out.println("====================================");
    }
}
