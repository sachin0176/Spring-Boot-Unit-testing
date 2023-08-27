package com.anibane.springBootMvc.models;

public class GradeBookCollegeStudent extends CollegeStudent{

    private  int id;

    private StudentGrades studentGrades;

    public GradeBookCollegeStudent(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    public GradeBookCollegeStudent(String firstName, String lastName, String email, int id, StudentGrades studentGrades) {
        super(firstName, lastName, email);
        this.id = id;
        this.studentGrades = studentGrades;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public StudentGrades getStudentGrades() {
        return studentGrades;
    }

    public void setStudentGrades(StudentGrades studentGrades) {
        this.studentGrades = studentGrades;
    }

    @Override
    public String toString() {
        return "GradeBookCollegeStudent{" +
                "id=" + id +
                ", studentGrades=" + studentGrades +
                '}';
    }
}
