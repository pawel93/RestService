package TPSI.resources;

import TPSI.DBConnection;
import TPSI.Validator;

import TPSI.model.Course;
import TPSI.model.Student;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import javax.xml.ws.soap.AddressingFeature;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("gradeManager")
public class StudentResource {

    private Datastore datastore;

    @Context
    UriInfo uriInfo;

    public StudentResource(){

        this.datastore = DBConnection.getInstance().getDatastore();
    }


    @GET
    @Path("students")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudents() {

        List<Student> students = datastore.find(Student.class).asList();
        return Response.ok().entity(students).build();

    }


    @GET
    @Path("students/{index}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Student getStudentByIndex(@PathParam("index") int studentIndex){

        Query<Student> indexQuery = datastore.find(Student.class).field("indexNumber").equal(studentIndex);
        if(indexQuery.get() == null){
            throw new WebApplicationException(Response.status(404).build());
        }
        return indexQuery.get();
    }


    @POST
    @Path("students")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStudent(Student student){

        Validator validator = new Validator();
        if(validator.validateIndex(student.getIndexNumber(), datastore)){
            datastore.save(student);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            URI studentUri = builder.path(String.valueOf(student.getIndexNumber())).build();

            return Response.created(studentUri).build();
        }else{
            return Response.status(409).entity("repeating index").build();
        }

    }


    @PUT
    @Path("students/{studentIndex}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateStudentData(Student upStudent, @PathParam("studentIndex") int studentIndex){

        Validator validator = new Validator();

        Query<Student> indexQuery = datastore.find(Student.class).field("indexNumber").equal(studentIndex);
        UpdateOperations<Student> studentDataUpdate = datastore.createUpdateOperations(Student.class)
                .set("firstName", upStudent.getFirstName())
                .set("lastName", upStudent.getLastName())
                .set("birthday", upStudent.getBirthday());
        datastore.update(indexQuery, studentDataUpdate);

        if(validator.validateIndex(upStudent.getIndexNumber(), studentIndex, datastore)){

            UpdateOperations<Student> studentIndexUpdate = datastore.createUpdateOperations(Student.class)
                    .set("indexNumber", upStudent.getIndexNumber());
            datastore.update(indexQuery, studentIndexUpdate);
        }

    }

    @DELETE
    @Path("students/{studentIndex}")
    public void deleteStudent(@PathParam("studentIndex") int studentIndex){
        Query<Student> indexQuery = datastore.createQuery(Student.class).field("indexNumber").equal(studentIndex);
        datastore.delete(indexQuery);

    }






}
