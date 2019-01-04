package TPSI.resources;

import TPSI.DBConnection;
import TPSI.model.Course;
import TPSI.model.Grade;
import TPSI.model.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("gradeManager")
public class GradeResource {

    private Datastore datastore;

    public GradeResource(){

        this.datastore = DBConnection.getInstance().getDatastore();
    }

    @GET
    @Path("students/{studentIndex}/grades")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGrades(@PathParam("studentIndex") int studentIndex){

        List<Student> student = datastore.find(Student.class).field("indexNumber").equal(studentIndex).asList();
        if(student.get(0) == null){
            throw new WebApplicationException(Response.status(404).build());
        }
        List<Grade> studentGrades = student.get(0).getGrades();
        return Response.ok().entity(studentGrades).build();
    }


    @POST
    @Path("students/{studentIndex}/grades/{courseName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addGrade(Grade grade, @PathParam("studentIndex") int studentIndex, @PathParam("courseName") String courseName){

        Student student = datastore.createQuery(Student.class).filter("indexNumber =", studentIndex).get();
        Course course = datastore.createQuery(Course.class).filter("courseName =", courseName).get();

        grade.setCourse(course);
        student.getGrades().add(grade);
        datastore.save(student);

        return Response.status(201).build();

    }

    @PUT
    @Path("students/{studentIndex}/grades/{position}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateStudentGrade(Grade upGrade, @PathParam("studentIndex") int studentIndex, @PathParam("position") int position){

        Student student = datastore.createQuery(Student.class).filter("indexNumber =", studentIndex).get();
        student.getGrades().get(position).setGradeValue(upGrade.getGradeValue());

        datastore.save(student);
    }

    @DELETE
    @Path("students/{studentIndex}/grades/{position}")
    public void deleteGradeByStudentId(@PathParam("studentIndex") int studentIndex, @PathParam("position") int position){

        Student student = datastore.createQuery(Student.class).field("indexNumber").equal(studentIndex).get();
        student.getGrades().remove(position);

        datastore.save(student);

    }

















}
