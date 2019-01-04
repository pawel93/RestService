package TPSI.resources;

import TPSI.DBConnection;
import TPSI.model.Course;
import TPSI.model.Student;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("gradeManager")
public class SearchResource {

    private Datastore datastore;

    public SearchResource(){

        this.datastore = DBConnection.getInstance().getDatastore();
    }

    @GET
    @Path("students/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> findStudentsByName(@DefaultValue("")@QueryParam("firstName") String firstName, @DefaultValue("")@QueryParam("lastName") String lastName){

        Query<Student> queryStudents = datastore.createQuery(Student.class);
        List<Student> foundStudents;

        if(!firstName.isEmpty() && !lastName.isEmpty()){
            foundStudents = queryStudents.filter("firstName =", firstName)
                    .filter("lastName =", lastName)
                    .asList();
        }
        else if(!firstName.isEmpty()){
            foundStudents = queryStudents.filter("firstName =", firstName).asList();
        }
        else{
            foundStudents = queryStudents.filter("lastName =", lastName).asList();
        }

        return foundStudents;

    }

    @GET
    @Path("students/search/birthday")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> findStudentsByBirthday(@QueryParam("birthday") Date birthday, @DefaultValue("")@QueryParam("when") String when){

        switch(when){
            case "before":
                return datastore.createQuery(Student.class).field("birthday").lessThan(birthday).asList();

            case "after":
                return datastore.createQuery(Student.class).field("birthday").greaterThan(birthday).asList();

            default:
                return datastore.createQuery(Student.class).field("birthday").equal(birthday).asList();

        }

    }

    @GET
    @Path("courses/search")
    public List<Course> findCoursesByLecturer(@DefaultValue("")@QueryParam("lecturer") String lecturer){

        return datastore.createQuery(Course.class).field("lecturer").equal(lecturer).asList();
    }

    @GET
    @Path("students/searching")
    public List<Student> findStudentBySubstring(@DefaultValue("null") @QueryParam("searchName") String searchName,
                                                @DefaultValue("null") @QueryParam("searchSurname") String searchSurname,
                                                @DefaultValue("1970-01-01") @QueryParam("searchBirthday") String searchBirthday,
                                                @DefaultValue("0") @QueryParam("searchIndex") String searchIndex){

        Query<Student> query = datastore.createQuery(Student.class)
                .field("firstName").containsIgnoreCase(searchName)
                .field("lastName").containsIgnoreCase(searchSurname);

        List<Student> students = query.asList();
        return students;
    }

    @GET
    @Path("courses/searching")
    public List<Course> findCoursesBySubstring(@DefaultValue("null") @QueryParam("searchCourse") String searchCourse,
                                               @DefaultValue("null") @QueryParam("searchLecturer") String searchLecturer,
                                               @DefaultValue("null") @QueryParam("searchSurname") String searchSurname){

        Query<Course> query = datastore.createQuery(Course.class)
                .field("courseName").containsIgnoreCase(searchCourse)
                .field("lecturer").containsIgnoreCase(searchLecturer)
                .field("surname").containsIgnoreCase(searchSurname);

        return query.asList();

    }


}
