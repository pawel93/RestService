package TPSI.model;


import TPSI.resources.CourseResource;
import TPSI.resources.GradeResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "student")
@Entity("Students")
public class Student {

    private String firstName;
    private String lastName;
    private int indexNumber;
    private ArrayList<Grade> grades;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date birthday;


    @XmlTransient
    @Id
    ObjectId id;

    @InjectLinks({
            @InjectLink(resource = CourseResource.class, method = "getCourses"),
            @InjectLink(resource = GradeResource.class, method = "getGrades", bindings = {
                    @Binding(name = "studentIndex", value = "${instance.indexNumber}")
            })
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;


    public Student(){
        this.grades = new ArrayList<>();
    }

    public Student(String firstName, String lastName, Date birthday, int indexNumber, ArrayList<Grade> grades){
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.indexNumber = indexNumber;
        this.grades = grades;

    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        this.grades = grades;
    }
}
