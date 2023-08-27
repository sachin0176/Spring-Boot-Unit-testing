package com.anibane.springBootMvc.models;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GradeBook {

    List<GradeBookCollegeStudent> students;

    public GradeBook(){}

    public GradeBook(List<GradeBookCollegeStudent> students) {
        this.students = students;
    }

    public List<GradeBookCollegeStudent> getStudents() {
        return students;
    }

    public void setStudents(List<GradeBookCollegeStudent> students) {
        this.students = students;
    }
}
