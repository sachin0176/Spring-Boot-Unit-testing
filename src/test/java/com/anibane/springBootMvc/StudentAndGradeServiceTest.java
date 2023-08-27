package com.anibane.springBootMvc;

import com.anibane.springBootMvc.models.*;
import com.anibane.springBootMvc.repository.HistoryGradesDao;
import com.anibane.springBootMvc.repository.MathGradesDao;
import com.anibane.springBootMvc.repository.ScienceGradesDao;
import com.anibane.springBootMvc.repository.StudentDao;
import com.anibane.springBootMvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
public class StudentAndGradeServiceTest {

    private JdbcTemplate jdbcTemplate;


    private StudentAndGradeService studentAndGradeService;


    private StudentDao studentDao;
    private MathGradesDao mathGradesDao;
    private ScienceGradesDao scienceGradesDao;
    private HistoryGradesDao historyGradesDao;


    @Autowired
    public StudentAndGradeServiceTest(StudentDao studentDao, JdbcTemplate jdbcTemplate,StudentAndGradeService studentService,
                                   MathGradesDao mathGradesDao, ScienceGradesDao scienceGradesDao, HistoryGradesDao historyGradesDao)
    {
        this.jdbcTemplate=jdbcTemplate;
        this.studentDao=studentDao;
        this.mathGradesDao=mathGradesDao;
        this.scienceGradesDao=scienceGradesDao;
        this.historyGradesDao=historyGradesDao;
        this.studentAndGradeService =studentService;
    }



    @BeforeEach
    public void setupDatabaseBeforeEach(){
        CollegeStudent student = new CollegeStudent("Eric", "Roby", "eric.roby@luv2code_school.com");
        student.setId(1);
        studentDao.save(student);


        MathGrade mathGrade=new MathGrade(100.0);
        mathGrade.setStudentId(1);
        mathGrade.setId(1);
        mathGradesDao.save(mathGrade);



        ScienceGrade scienceGrade=new ScienceGrade(100.0);
        scienceGrade.setStudentId(1);
        scienceGrade.setId(1);
        scienceGradesDao.save(scienceGrade);

        HistoryGrade historyGrade=new HistoryGrade(100.0);
        historyGrade.setStudentId(1);
        historyGrade.setId(1);
        historyGradesDao.save(historyGrade);


    }
    @AfterEach
    void setupAfterEachTransaction()
    {
//
        jdbcTemplate.execute("DELETE FROM student");
        jdbcTemplate.execute("ALTER TABLE student ALTER COLUMN ID RESTART WITH 1");


        jdbcTemplate.execute("DELETE FROM math_grade");
        jdbcTemplate.execute("ALTER TABLE math_grade ALTER COLUMN ID RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM science_grade");
        jdbcTemplate.execute("ALTER TABLE science_grade ALTER COLUMN ID RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM history_grade");
        jdbcTemplate.execute("ALTER TABLE history_grade ALTER COLUMN ID RESTART WITH 1");


    }

    @Test
    void createStudentService() {

        studentAndGradeService.createStudent("Sachin","Ray","sachin@anibane.com");

        CollegeStudent student=studentDao.findByEmailAddress("sachin@anibane.com");

        assertEquals("sachin@anibane.com", student.getEmailAddress());
    }

    @Test
    void isStudentNullCheck(){
        assertTrue(studentAndGradeService.checkIfStudentIsNUll(1));
        assertFalse(studentAndGradeService.checkIfStudentIsNUll(0));
    }

    @Test
    void deleteStudentService(){
        Optional<CollegeStudent> deleteCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deleteMathGrade = mathGradesDao.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradesDao.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradesDao.findById(1);


        assertTrue(deleteCollegeStudent.isPresent(),"return true");
        assertTrue(deleteMathGrade.isPresent(),"return true");
        assertTrue(deleteScienceGrade.isPresent(),"return true");
        assertTrue(deleteHistoryGrade.isPresent(),"return true");


        studentAndGradeService.deleteStudent(1);

        deleteCollegeStudent=studentDao.findById(1);
        deleteMathGrade=mathGradesDao.findById(1);
        deleteScienceGrade=scienceGradesDao.findById(1);
        deleteHistoryGrade=historyGradesDao.findById(1);


        assertFalse(deleteCollegeStudent.isPresent(),"return false");
        assertFalse(deleteMathGrade.isPresent(),"return false");
        assertFalse(deleteScienceGrade.isPresent(),"return false");
        assertFalse(deleteHistoryGrade.isPresent(),"return false");



    }

    @Sql("/insertData.sql")
    @Test
    void  getGradeBookService(){
        Iterable<CollegeStudent> iterable= studentAndGradeService.getGradeBook();

        List<CollegeStudent> collegeStudentList=new ArrayList<>();
        for(CollegeStudent collegeStudent:iterable){
            collegeStudentList.add(collegeStudent);
        }
        assertEquals(5,collegeStudentList.size());
    }

    @Test
    void createGradeService()
    {

        // create grade
        assertTrue(studentAndGradeService.createGrade(85.0,1,"math"));
        assertTrue(studentAndGradeService.createGrade(85.0,1,"science"));
        assertTrue(studentAndGradeService.createGrade(85.0,1,"history"));


        // get all grade student
        Iterable<MathGrade> mathGrades=mathGradesDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades=scienceGradesDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades=historyGradesDao.findGradeByStudentId(1);


        //verify grade
        assertTrue(((Collection<MathGrade>) mathGrades).size()==2);
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size()==2);
        assertTrue(((Collection<HistoryGrade>) historyGrades).size()==2);


    }

    @Test
    void createGradeServiceReturnFalse()
    {
        // create grade
        assertFalse(studentAndGradeService.createGrade(105.0,1,"math"));
        assertFalse(studentAndGradeService.createGrade(-5.0,1,"math"));
        assertFalse(studentAndGradeService.createGrade(85.0,2,"math"));
        assertFalse(studentAndGradeService.createGrade(85.0,1,"literature"));


    }

    @Test
    void deleteGradeService()
    {
        assertEquals(1,studentAndGradeService.deleteGrade(1,"math"), "Return student id");
        assertEquals(1,studentAndGradeService.deleteGrade(1,"science"), "Return student id");
        assertEquals(1,studentAndGradeService.deleteGrade(1,"history"), "Return student id");

    }


    @Test
    void deleteGradeServiceReturnStudentIdZero()
    {
        assertEquals(0,studentAndGradeService.deleteGrade(2,"math"), "Return student id");
        assertEquals(0,studentAndGradeService.deleteGrade(1,"literature"), "Return student id");
//
//        assertEquals(0,studentAndGradeService.deleteGrade(1,"science"), "Return student id");
//        assertEquals(0,studentAndGradeService.deleteGrade(1,"history"), "Return student id");

    }

    @Test
    public void StudentInformationService()
    {
        GradeBookCollegeStudent studentInfo=studentAndGradeService.getStudentInformation(1);
        assertNotNull(studentInfo,"Student info not null");
        assertEquals(1,studentInfo.getId());
        assertEquals("Eric",studentInfo.getFirstname());
        assertEquals("Roby", studentInfo.getLastname());
        assertEquals("eric.roby@luv2code_school.com",studentInfo.getEmailAddress());
        assertTrue(studentInfo.getStudentGrades().getHistoryGradeResults().size()==1);
        assertTrue(studentInfo.getStudentGrades().getMathGradeResults().size()==1);
        assertTrue(studentInfo.getStudentGrades().getScienceGradeResults().size()==1);
    }

    @Test
    void StudentInformationServiceReturnNull()
    {
        GradeBookCollegeStudent studentInfo=studentAndGradeService.getStudentInformation(0);
        assertNull(studentInfo,"should return null ");

    }

}
