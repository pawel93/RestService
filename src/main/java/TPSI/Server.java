package TPSI;

import TPSI.resources.CourseResource;
import TPSI.resources.GradeResource;
import TPSI.resources.SearchResource;
import TPSI.resources.StudentResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Server {


    public static void main(String[] args){

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig(GradeResource.class, StudentResource.class, CourseResource.class, SearchResource.class);

        config.packages("org.glassfish.jersey.examples.linking").register(DeclarativeLinkingFeature.class);
        config.register(new DateParamConverterProvider("yyyy-MM-dd"));
        config.register(CustomHeaders.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);


    }



}
