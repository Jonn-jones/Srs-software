package com.spas.crss.config;

import com.spas.crss.dao.LecturerSpecializationDAO;
import com.spas.crss.model.TeachingSpecialization;

public class LecturerSpecializationTest {

    public static void main(String[] args) {

        LecturerSpecializationDAO dao =
                new LecturerSpecializationDAO();

        String lecturerId = "TEST001";
        int specializationId = 3;

        System.out.println("==================================");
        System.out.println("SPECIALIZATION REMOVAL TEST");
        System.out.println("==================================");

        boolean removed = dao.removeSpecialization(
                lecturerId,
                specializationId
        );

        if (removed) {
            System.out.println(
                    "Specialization removed successfully.");
        } else {
            System.out.println(
                    "Specialization removal failed.");
        }

        System.out.println("\nREMAINING SPECIALIZATIONS:");

        for (TeachingSpecialization specialization :
                dao.getLecturerSpecializations(lecturerId)) {

            System.out.println(specialization);
        }
    }
}