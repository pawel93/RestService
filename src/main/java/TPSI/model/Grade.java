package TPSI.model;

import TPSI.ObjectIdJaxbAdapter;
import TPSI.resources.CourseResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.Date;

@XmlRootElement(name = "grade")
@Embedded
public class Grade {

    private int gradeValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date gradeDate;

    @Reference
    private Course course;


    @InjectLink(resource = CourseResource.class, method = "getCourses")
    @XmlElement(name = "link")
    URI uri;

    public Grade(){

    }

    public Grade(int gradeValue, Date gradeDate, Course course){
        this.gradeValue = gradeValue;
        this.gradeDate = gradeDate;
        this.course = course;
    }


    public int getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(int gradeValue) {
        this.gradeValue = gradeValue;
    }

    public Date getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(Date gradeDate) {
        this.gradeDate = gradeDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
