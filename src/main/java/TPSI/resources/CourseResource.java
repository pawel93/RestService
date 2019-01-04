package TPSI.resources;

import TPSI.DBConnection;
import TPSI.Validator;
import TPSI.lab1.Model;
import TPSI.lab1.ResUtil;
import TPSI.model.Course;
import TPSI.model.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("gradeManager")
public class CourseResource {


    private Datastore datastore;

    @Context
    UriInfo uriInfo;

    public CourseResource(){

        this.datastore = DBConnection.getInstance().getDatastore();

    }

    @GET
    @Path("courses")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourses() {

        List<Course> courses = datastore.find(Course.class).asList();
        return Response.ok().entity(courses).build();
    }


    @GET
    @Path("courses/{courseName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Course getCourseById(@PathParam("courseName") String courseName){

        Course course = datastore.createQuery(Course.class).field("courseName").equal(courseName).get();
        if(course == null){
            throw new WebApplicationException(Response.status(404).build());
        }
        return course;

    }

    @POST
    @Path("courses")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCourse(Course course){
        Validator validator = new Validator();
        if(validator.validateCourseName(course.getCourseName(), datastore)){
            datastore.save(course);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            URI courseUri = builder.path(course.getCourseName()).build();

            return Response.created(courseUri).build();
        }else {
            return Response.status(409).entity("repeating course name").build();
        }

    }

    @PUT
    @Path("courses/{courseName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateCourseData(Course upCourse, @PathParam("courseName") String courseName){

        Query<Course> courseQuery = datastore.find(Course.class).field("courseName").equal(courseName);
        UpdateOperations<Course> courseDataUpdate = datastore.createUpdateOperations(Course.class)
                .set("lecturer", upCourse.getLecturer())
                .set("surname", upCourse.getSurname());
        datastore.update(courseQuery, courseDataUpdate);

    }

    @DELETE
    @Path("courses/{courseName}")
    public void deleteCourseById(@PathParam("courseName") String courseName){

        List<Student> students = datastore.createQuery(Student.class).asList();
        for(Student student: students){
            student.getGrades().removeIf(grade -> grade.getCourse().getCourseName().equals(courseName));
            datastore.save(student);
        }

        Query<Course> courseQuery = datastore.find(Course.class).field("courseName").equal(courseName);
        datastore.delete(courseQuery);

    }




}
