package com.zemiak.nasphotos.folders;

import com.zemiak.nasphotos.FilenameEncoder;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("folders")
@Produces(MediaType.APPLICATION_JSON)
public class FoldersResource {
    @Inject
    FolderControl folders;

    @GET
    @Path("{id}")
    public Response download(@PathParam("id") @DefaultValue("Lw==") String encodedPath) {
        // Lw== is "/" base64-encoded
        return Response.ok(folders.getList(FilenameEncoder.decode(encodedPath))).build();
    }
}
