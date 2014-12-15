package unicaferater.domain.common;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

/**
 * Created by jani on 14.12.2014.
 */
@Entity
public class PriceValue extends AbstractPersistable<Long> {
    private String contract;
    private String graduate;
    private String graduate_hyy;
    private String normal;
    private String student;
    private String student_hyy;

    public String getContract() {
        return contract;
    }

    public String getGraduate() {
        return graduate;
    }

    public String getGraduate_hyy() {
        return graduate_hyy;
    }

    public String getNormal() {
        return normal;
    }

    public String getStudent() {
        return student;
    }

    public String getStudent_hyy() {
        return student_hyy;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public void setGraduate(String graduate) {
        this.graduate = graduate;
    }

    public void setGraduate_hyy(String graduate_hyy) {
        this.graduate_hyy = graduate_hyy;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public void setStudent_hyy(String student_hyy) {
        this.student_hyy = student_hyy;
    }
}
