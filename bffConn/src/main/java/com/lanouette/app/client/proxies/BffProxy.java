package com.lanouette.app.client.proxies;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.lanouette.app.shared.ServerPaths;

public interface BffProxy extends RestService {
    @GET
    @Path("/rest/bffConn/get_motd/{userId}/{sessionId}/{rndValue}" +
            "")
    void getMotd(@PathParam("userId") int userId, @PathParam("sessionId") int sessionId,
                 @PathParam("rndValue") int rndValue, MethodCallback<List<String>> callback);


}
