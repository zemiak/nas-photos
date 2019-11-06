package com.zemiak.nasphotos.frontend;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("backend/config")
public class ConfigResource {

    @Inject @ConfigProperty(name = "quarkus.http.port") String port;

    @GET
    public FrontendConfig getConfig() {
        FrontendConfig config = new FrontendConfig();
        config.setPort(this.port);

        return config;
    }
}
