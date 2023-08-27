package com.anibane.springBootMvc;

import com.anibane.springBootMvc.models.*;
import com.anibane.springBootMvc.repository.HistoryGradesDao;
import com.anibane.springBootMvc.repository.MathGradesDao;
import com.anibane.springBootMvc.repository.ScienceGradesDao;
import com.anibane.springBootMvc.repository.StudentDao;
import com.anibane.springBootMvc.service.StudentAndGradeService;
import org.antlr.v4.runtime.misc.LogManager;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@SpringBootTest
public class GradeBookControllerTest {

    private static MockHttpServletRequest request;


    private JdbcTemplate jdbcTemplate;


    private MockMvc mockMvc;



    @Mock
    private StudentAndGradeService studentCreateServiceMock;


    private StudentDao studentDao;
    private MathGradesDao mathGradesDao;
    private ScienceGradesDao scienceGradesDao;
    private HistoryGradesDao historyGradesDao;
    private StudentAndGradeService studentService;

    @Autowired
    public GradeBookControllerTest(StudentDao studentDao, MockMvc  mockMvc, JdbcTemplate jdbcTemplate,StudentAndGradeService studentService,
                                   MathGradesDao mathGradesDao, ScienceGradesDao scienceGradesDao, HistoryGradesDao historyGradesDao)
    {
        this.jdbcTemplate=jdbcTemplate;
        this.mockMvc=mockMvc;
        this.studentDao=studentDao;
        this.mathGradesDao=mathGradesDao;
        this.scienceGradesDao=scienceGradesDao;
        this.historyGradesDao=historyGradesDao;
        this.studentService=studentService;
    }


    @BeforeAll
    public  static void setup()
    {
        request=new MockHttpServletRequest();
        request.addParameter("firstname", "Chad");
        request.addParameter("lastname", "Darby");
        request.addParameter("emailAddress", "chad@anibane.com");
    }
    @BeforeEach
    public void setupDatabase(){
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
    public  void getStudentsHttpRequest() throws Exception {
        CollegeStudent student1 = new CollegeStudent("Chad", "Darby", "chad@anibane.com");
        CollegeStudent student2 = new CollegeStudent("Eric", "Roby", "eric@anibane.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(student1, student2));

        when(studentCreateServiceMock.getGradeBook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradeBook());

        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"index");

    }

    @Test
    public  void createStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne=new CollegeStudent("Rahul","Doe","rahul@anibane.com");
        List<CollegeStudent> collegeStudentList=new ArrayList<>(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradeBook()).thenReturn(collegeStudentList);
        assertIterableEquals(collegeStudentList,studentCreateServiceMock.getGradeBook());

        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname",request.getParameterValues("lastname"))
                .param("emailAddress",request.getParameterValues("emailAddress"))
        ).andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"index");

        CollegeStudent verifyStudent=studentDao.findByEmailAddress("chad@anibane.com");

        assertNotNull(verifyStudent,"Student must be found");

    }

    @Test
    public  void deleteStudentsHttpRequest() throws Exception {

        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"index");

        assertFalse(studentDao.findById(1).isPresent());

    }

    @Test
    public  void deleteStudentsHttpRequestErrorPage() throws Exception {


        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"error");

        assertFalse(studentDao.findById(0).isPresent());

    }

    @Test
    public  void getStudentInformationHttpRequest() throws Exception {

        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"studentInformation");



    }
    @Test
    public  void getStudentInformationHttpRequestReturnErrorPage() throws Exception {

        assertFalse(studentDao.findById(0).isPresent());

        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"error");



    }

    @Test
    public void createAValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());

        GradeBookCollegeStudent student=studentService.getStudentInformation(1);

        assertEquals(1,student.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult=mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("studentId", "1")
                .param("grade","84.8")
                .param("gradeType","math"))
                .andExpect(status().isOk()
                ).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();
         ModelAndViewAssert.assertViewName(mav,"studentInformation");

         student=studentService.getStudentInformation(1);

         assertEquals(2,student.getStudentGrades().getMathGradeResults().size());
    }


    @Test
    public  void createAValidHttpRequestStudentDoesNotExitResponse() throws Exception {

        MvcResult mvcResult=mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studentId", "0")
                        .param("grade","84.8")
                        .param("gradeType","math"))
                .andExpect(status().isOk()
                ).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");

    }

    @Test
    public  void createANonValidGradeHttpRequestGradeTypeDoesNotExitResponse() throws Exception {

        MvcResult mvcResult=mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studentId", "1")
                        .param("grade","84.8")
                        .param("gradeType","literature"))
                .andExpect(status().isOk()
                ).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");

    }

    @Test
    public  void deleteAValidGradeHttpRequest() throws  Exception
    {
        Optional<MathGrade> mathGrade=mathGradesDao.findById(1);

        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult=mockMvc.perform(get("/delete/{id}/{gradeType}",1,"math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"studentInformation");
        mathGrade=mathGradesDao.findById(1);
        assertFalse(mathGrade.isPresent());
    }


    @Test
    public  void deleteAValidGradeHttpRequestGradeIdDoesNotExist() throws  Exception
    {
        Optional<MathGrade> mathGrade=mathGradesDao.findById(2);

        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult=mockMvc.perform(get("/delete/{id}/{gradeType}",2,"math"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    public  void deleteANonValidGradeHttpRequest() throws  Exception
    {


        MvcResult mvcResult=mockMvc.perform(get("/delete/{id}/{gradeType}",1,"literatur"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");
    }












}
