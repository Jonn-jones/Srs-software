package com.spas.crss.model;

public class CoursePrerequisite {

    private int courseId;
    private int prerequisiteCourseId;

    // Default constructor
    public CoursePrerequisite() {
    }

    // Constructor
    public CoursePrerequisite(
            int courseId,
            int prerequisiteCourseId) {

        this.courseId = courseId;
        this.prerequisiteCourseId = prerequisiteCourseId;
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getPrerequisiteCourseId() {
        return prerequisiteCourseId;
    }

    public void setPrerequisiteCourseId(int prerequisiteCourseId) {
        this.prerequisiteCourseId = prerequisiteCourseId;
    }

    @Override
    public String toString() {
        return "CoursePrerequisite{" +
                "courseId=" + courseId +
                ", prerequisiteCourseId=" + prerequisiteCourseId +
                '}';
    }
}
