package com.zemiak.nasphotos.thumbnails;

import com.zemiak.nasphotos.batch.ScheduledCacheRegeneration;
import com.zemiak.nasphotos.files.CacheDataReader;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("cache")
public class CacheResource {
    @Inject ScheduledCacheRegeneration cacheRecreator;
    @Inject CacheDataReader cache;

    @DELETE
    @Path("cache")
    public void refreshThumbnails() {
        cacheRecreator.refreshImageCache();
    }

    @GET
    public Response getData() {
        return Response
                .ok(buildData())
                .build();
    }

    private JsonObject buildData() {
        JsonObject version = Json.createObjectBuilder()
                .add("version", cache.getVersion())
                .add("motd", "")
                .build();
        JsonObject dataCache = cache.getCache();
        JsonObject data = Json.createObjectBuilder()
                .add("version", version)
                .add("cache", dataCache)
                .build();

        return data;
    }
}
