package com.tinkerpop.rexster;

import com.tinkerpop.blueprints.pgm.Graph;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

@Path("/{graphname}")
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource extends AbstractSubResource {

    private static Logger logger = Logger.getLogger(GraphResource.class);

    public GraphResource(@PathParam("graphname") String graphName, @Context UriInfo ui, @Context HttpServletRequest req) {
        super(graphName, ui, req, null);
    }
    
    public GraphResource(String graphName, UriInfo ui, HttpServletRequest req, RexsterApplicationProvider rap) {
        super(graphName, ui, req, rap);
    }
    
    /**
     * GET http://host/graph
     * graph.toString();
     */
    @GET
    public Response getGraph() {
        try {

            // graph should be ready to go at this point.  checks in the
            // constructor ensure that the rag is not null.
            Graph graph = this.rag.getGraph();

            this.resultObject.put("name", this.rag.getGraphName());
            this.resultObject.put("graph", graph.toString());
            this.resultObject.put(Tokens.QUERY_TIME, this.sh.stopWatch());
            this.resultObject.put("up_time", this.getTimeAlive());
            this.resultObject.put("version", RexsterApplication.getVersion());

        } catch (JSONException ex) {
            logger.error(ex);
            JSONObject error = generateErrorObjectJsonFail(ex);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build());
        }

        return this.addHeaders(Response.ok(this.resultObject)).build();
    }

    /**
     * DELETE http://host/graph
     * graph.clear()
     *
     * @return Query time
     */
    @DELETE
    public Response deleteGraph() {
        Graph graph = this.rag.getGraph();
        graph.clear();

        try {
            this.resultObject.put(Tokens.QUERY_TIME, sh.stopWatch());
        } catch (JSONException ex) {
            logger.error(ex);
            JSONObject error = generateErrorObjectJsonFail(ex);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build());
        }

        return Response.ok(this.resultObject).build();

    }


}
