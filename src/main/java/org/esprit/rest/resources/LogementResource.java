package org.esprit.rest.resources;

import org.esprit.entities.Logement;
import org.esprit.metiers.LogementBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("logements")
public class LogementResource {
    private static final Logger LOGGER = Logger.getLogger(LogementResource.class.getName());
    private static LogementBusiness lgb = new LogementBusiness();

    public LogementResource() {}

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
        try {
            // Ensure logement is not null
            if (logement == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Logement cannot be null").build();
            }

            if (lgb.addLogement(logement)) {
                LOGGER.info("Logement created successfully");
                return Response.status(Response.Status.CREATED).entity(logement).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Failed to add logement").build();
            }
        } catch (Exception e) {
            LOGGER.severe("Error adding logement: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
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

    @GET
    @Path("by-reference")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogementByReference(@QueryParam("reference") int reference) {
        Logement logement = lgb.getLogementsByReference(reference);
        if (logement == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Logement non trouvé").build();
        }
        return Response.ok(logement).build();
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLogement(@PathParam("ref") int ref, Logement updatedLogement) {
        LOGGER.info("Updating Logement with reference: " + ref);

        // Ensure updatedLogement is not null
        if (updatedLogement == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Updated logement cannot be null").build();
        }

        try {
            Logement existingLogement = lgb.getLogementsByReference(ref);
            if (existingLogement == null) {
                LOGGER.warning("Logement not found with reference: " + ref);
                return Response.status(Response.Status.NOT_FOUND).entity("Logement non trouvé").build();
            }

            // Update fields directly using updatedLogement
            existingLogement.setAdresse(updatedLogement.getAdresse());
            existingLogement.setGouvernorat(updatedLogement.getGouvernorat());
            existingLogement.setType(updatedLogement.getType());
            existingLogement.setDescription(updatedLogement.getDescription());
            existingLogement.setPrix(updatedLogement.getPrix());
            existingLogement.setDelegation(updatedLogement.getDelegation());

            if (lgb.updateLogement(ref, existingLogement)) {
                LOGGER.info("Logement updated successfully with reference: " + ref);
                return Response.ok(existingLogement).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update logement").build();
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating logement: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }
}
