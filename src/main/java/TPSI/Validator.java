package TPSI;

import TPSI.model.Course;
import TPSI.model.Student;
import org.mongodb.morphia.Datastore;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public boolean validateCourseName(String newCourseName, String oldCourseName, ArrayList<Course> courses){
        boolean result = true;
        if(!newCourseName.equals(oldCourseName)){
            for(Course course: courses){
                if(course.getCourseName().equals(newCourseName)){
                    result = false;
                }
            }
        }
        return result;
    }

    public boolean validateCourseName(String courseName, Datastore datastore){
        Course course = datastore.createQuery(Course.class).field("courseName").equal(courseName).get();
        if(course == null)
            return true;
        else
            return false;

    }

    public boolean validateIndex(int newIndex, int oldIndex, ArrayList<Student> students){
        boolean result = true;
        if(newIndex != oldIndex){

            for(Student student: students){
                if(student.getIndexNumber() == newIndex){
                    result = false;
                }
            }
        }

        return result;
    }

    public boolean validateIndex(int index, Datastore datastore){
        Student student = datastore.createQuery(Student.class).field("indexNumber").equal(index).get();
        if(student==null)
            return true;
        else
            return false;

    }

    public boolean validateIndex(int newIndex, int oldIndex, Datastore datastore){

        ArrayList<Student> students = (ArrayList<Student>)datastore.find(Student.class).asList();
        return validateIndex(newIndex, oldIndex, students);
    }

    public boolean validateCourseName(String newCourseName, String oldCourseName, Datastore datastore){
        ArrayList<Course> courses = (ArrayList<Course>)datastore.find(Course.class).asList();
        return validateCourseName(newCourseName, oldCourseName, courses);
    }



}
