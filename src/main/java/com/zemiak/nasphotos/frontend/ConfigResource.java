package com.zemiak.nasphotos.frontend;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("backend/config")
public class ConfigResource {

    @Inject @ConfigProperty(name = "quarkus.http.port") String port;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public FrontendConfig getConfig() {
        FrontendConfig config = new FrontendConfig();
        config.setPort(this.port);

        return config;
    }
}
