package TPSI.model;

import TPSI.ObjectIdJaxbAdapter;

import TPSI.resources.StudentResource;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "course")
@Entity("Courses")
public class Course {

    private String courseName;
    private String lecturer;
    private String surname;


    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;


    @InjectLink(resource = StudentResource.class, method = "getStudents", rel = "parent")
    @XmlElement(name = "link")
    URI uri;

    public Course(){

    }

    public Course(String courseName, String lecturer, String surname){
        this.courseName = courseName;
        this.lecturer = lecturer;
        this.surname = surname;
    }


    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
