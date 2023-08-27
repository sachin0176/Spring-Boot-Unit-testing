package com.anibane.springBootMvc.service;

import com.anibane.springBootMvc.models.*;
import com.anibane.springBootMvc.repository.HistoryGradesDao;
import com.anibane.springBootMvc.repository.MathGradesDao;
import com.anibane.springBootMvc.repository.ScienceGradesDao;
import com.anibane.springBootMvc.repository.StudentDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {



    private  StudentDao studentDao;


    private MathGrade mathGrades;


    private ScienceGrade scienceGrades;



    private HistoryGrade historyGrades;


   private MathGradesDao mathGradesDao;


    private ScienceGradesDao scienceGradesDao;


    private HistoryGradesDao historyGradesDao;


    private StudentGrades studentGrades;

    @Autowired
    public StudentAndGradeService(StudentDao studentDao,MathGradesDao mathGradesDao,ScienceGradesDao scienceGradesDao,
                                  HistoryGradesDao historyGradesDao,@Qualifier("mathGrades") MathGrade mathGrade,
                                  @Qualifier("scienceGrades") ScienceGrade scienceGrade,
                                  @Qualifier("historyGrades") HistoryGrade historyGrade, StudentGrades studentGrades )
    {
        this.studentDao=studentDao;
        this.mathGradesDao=mathGradesDao;
        this.scienceGradesDao=scienceGradesDao;
        this.historyGradesDao=historyGradesDao;
        this.mathGrades=mathGrade;
        this.scienceGrades=scienceGrade;
        this.historyGrades=historyGrade;
        this.studentGrades=studentGrades;

    }



    public void createStudent(String firstName, String lastName, String emailAddress)
    {
        CollegeStudent student=new CollegeStudent(firstName,lastName,emailAddress);
        student.setId(0);

        studentDao.save(student);

    }

    public boolean checkIfStudentIsNUll(int id) {
        Optional<CollegeStudent> student=studentDao.findById(id);

        if(student.isPresent())
            return true;
        return false;
    }

    public void deleteStudent(int id) {
        if(checkIfStudentIsNUll(id))
        {
            studentDao.deleteById(id);
            mathGradesDao.deleteByStudentId(id);
            scienceGradesDao.deleteByStudentId(id);
            historyGradesDao.deleteByStudentId(id);

        }
    }

    public Iterable<CollegeStudent> getGradeBook() {
        Iterable<CollegeStudent> iterable=studentDao.findAll();
        return iterable;
    }

    public boolean createGrade(double grade, int id, String gradeType) {
        System.out.println(gradeType+", "+id+","+grade);
        if(!checkIfStudentIsNUll(id))
            return false;
        if(grade>=0 && grade<=100)
        {
            if(gradeType.equals("math"))
            {
                mathGrades.setId(0);
                mathGrades.setGrade(grade);
                mathGrades.setStudentId(id);
                mathGradesDao.save(mathGrades);
                return true;
            }

           if(gradeType.equals("science"))
            {
                scienceGrades.setId(0);
                scienceGrades.setGrade(grade);
                scienceGrades.setStudentId(id);
                scienceGradesDao.save(scienceGrades);
                return true;
            }

           if(gradeType.equals("history"))
            {
                historyGrades.setId(0);
                historyGrades.setGrade(grade);
                historyGrades.setStudentId(id);
                historyGradesDao.save(historyGrades);
                return true;
            }

        }

        return false;
    }

    public int deleteGrade(int id, String gradeType) {

        int studentId=0;
        if(gradeType.equals("math"))
        {
            Optional<MathGrade> grades=mathGradesDao.findById(id);
            if(grades.isEmpty())
                return studentId;
            studentId=grades.get().getStudentId();
            mathGradesDao.deleteById(id);

        }

        if(gradeType.equals("science"))
        {
            Optional<ScienceGrade> grades=scienceGradesDao.findById(id);
            if(grades.isEmpty())
                return studentId;
            studentId=grades.get().getStudentId();
            scienceGradesDao.deleteById(id);

        }
        if(gradeType.equals("history"))
        {
            Optional<HistoryGrade> grades=historyGradesDao.findById(id);
            if(grades.isEmpty())
                return studentId;
            studentId=grades.get().getStudentId();
            historyGradesDao.deleteById(id);

        }
        return studentId;
    }

    public GradeBookCollegeStudent getStudentInformation(int id) {

        if(!checkIfStudentIsNUll(id))
            return null;
        Optional<CollegeStudent> student=studentDao.findById(id);
        Iterable<MathGrade> mathGrades=mathGradesDao.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades=scienceGradesDao.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades=historyGradesDao.findGradeByStudentId(id);

        List<Grade> mathGradeList=new ArrayList<>();
        mathGrades.forEach(mathGradeList::add);

        List<Grade> scienceGradeList=new ArrayList<>();
        scienceGrades.forEach(scienceGradeList::add);

        List<Grade> historyGradeList=new ArrayList<>();
        historyGrades.forEach(historyGradeList::add);

       studentGrades.setMathGradeResults(mathGradeList);
       studentGrades.setScienceGradeResults(scienceGradeList);
       studentGrades.setHistoryGradeResults(historyGradeList);
        GradeBookCollegeStudent studentInfo=new GradeBookCollegeStudent(student.get().getFirstname(),student.get().getLastname(),student.get().getEmailAddress(),
                student.get().getId(),studentGrades);

       return  studentInfo;
    }


    public  void configureStudentInformationModel(int id , Model model)
    {
        GradeBookCollegeStudent studentEntity=getStudentInformation(id);

        model.addAttribute("student",studentEntity);

        if(studentEntity.getStudentGrades().getMathGradeResults().size()>0)
        {
            model.addAttribute("mathAverage",
                    studentEntity.getStudentGrades().findGradePointAverage(studentEntity.getStudentGrades().getMathGradeResults()));
        }
        else
            model.addAttribute("mathAverage","N/A");

        //
        // science
        //


        if(studentEntity.getStudentGrades().getScienceGradeResults().size()>0)
        {
            model.addAttribute("scienceAverage",
                    studentEntity.getStudentGrades().findGradePointAverage(studentEntity.getStudentGrades().getScienceGradeResults()));
        }
        else
            model.addAttribute("scienceAverage","N/A");


        //
        // history
        //

        if(studentEntity.getStudentGrades().getHistoryGradeResults().size()>0)
        {
            model.addAttribute("historyAverage",
                    studentEntity.getStudentGrades().findGradePointAverage(studentEntity.getStudentGrades().getHistoryGradeResults()));
        }
        else
            model.addAttribute("historyAverage","N/A");

    }
}
