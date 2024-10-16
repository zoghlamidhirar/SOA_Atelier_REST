package org.esprit.rest.resources;

import org.esprit.entities.Logement;
import org.esprit.metiers.LogementBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("logements")
public class LogementResource {

    private static LogementBusiness lgb = new LogementBusiness();

    public LogementResource() {
        // Initialization if needed
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLogements() {
        List<Logement> logements = lgb.getLogements();
        if (logements.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("La liste est vide").build();
        }
        return Response.ok(logements).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addLogement(Logement logement) {
        if (lgb.getLogements().add(logement)) {
            return Response.status(Response.Status.CREATED).entity(logement).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Échec de l'ajout du logement").build();
    }

    @GET
    @Path("by-delegation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogementByDelegation(@QueryParam("delegation") String delegation) {
        if (delegation != null && !delegation.isEmpty()) {
            List<Logement> logements = lgb.getLogementsByDeleguation(delegation);
            if (logements.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Aucun logement pour cette délégation").build();
            }
            return Response.ok(logements).build();
        } else {
            return getAllLogements();
        }
    }

    @DELETE
    @Path("{ref}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteLogement(@PathParam("ref") int ref) {
        if (lgb.deleteLogement(ref)) {
            return Response.ok("Logement supprimé avec succès").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Logement non trouvé").build();
    }


    @PUT
    @Path("{ref}")
    public Response updateLogement(@PathParam("ref") int ref ,Logement l ) {

    }







}
