package com.anibane.springBootMvc.repository;

import com.anibane.springBootMvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MathGradesDao extends CrudRepository<MathGrade,Integer> {


    Iterable<MathGrade> findGradeByStudentId(int id);

    public  void deleteByStudentId(int id);
}
