package com.anibane.springBootMvc.controller;

import com.anibane.springBootMvc.models.CollegeStudent;

import com.anibane.springBootMvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradeBookController {



    StudentAndGradeService studentAndGradeService;

    @Autowired
    public GradeBookController(StudentAndGradeService studentAndGradeService){
        this.studentAndGradeService=studentAndGradeService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public  String getStudents(Model model)
    {
        Iterable<CollegeStudent> iterable=studentAndGradeService.getGradeBook();

        model.addAttribute("students", iterable);

        return "index";
    }

    @PostMapping(value = "/")
    public String createStudent(@ModelAttribute("student") CollegeStudent collegeStudent , Model model)
    {
        studentAndGradeService.createStudent(collegeStudent.getFirstname(),collegeStudent.getLastname(),collegeStudent.getEmailAddress());
       Iterable<CollegeStudent> iterable=studentAndGradeService.getGradeBook();
       model.addAttribute("students",iterable);

        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id,Model model)
    {
        if(!studentAndGradeService.checkIfStudentIsNUll(id))
            return "error";
        studentAndGradeService.deleteStudent(id);
        Iterable<CollegeStudent> iterable=studentAndGradeService.getGradeBook();
        model.addAttribute("students",iterable);

        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id,Model model)
    {
        if(!studentAndGradeService.checkIfStudentIsNUll(id))
            return "error";

        studentAndGradeService.configureStudentInformationModel(id,model);

        return "studentInformation";
    }


    @PostMapping(value = "/grades")
    public  String createGrade(@RequestParam("grade") double grade, @RequestParam("studentId")  int id,@RequestParam("gradeType") String gradeType,Model model)
    {

        System.out.println(id +","+grade+","+gradeType);
        if(!studentAndGradeService.checkIfStudentIsNUll(id))
            return "error";


        boolean success=studentAndGradeService.createGrade(grade,id,gradeType);
        if(!success)
            return "error";

        studentAndGradeService.configureStudentInformationModel(id,model);
        return "studentInformation";
    }

    @GetMapping("/delete/{id}/{gradeType}")
    public String deleteGrade(@PathVariable int id , @PathVariable String gradeType,Model model)
    {

        int studentId=studentAndGradeService.deleteGrade(id,gradeType);
        if(studentId==0)
            return "error";

        studentAndGradeService.configureStudentInformationModel(studentId,model);
        return "studentInformation";
    }
}
