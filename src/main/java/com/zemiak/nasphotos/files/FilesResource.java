package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.FilenameEncoder;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("files")
@Produces(MediaType.APPLICATION_JSON)
public class FilesResource {
    @Inject FolderControl folders;
    @Inject PictureControl pictures;

    @GET
    @Path("{id}")
    public Response download(@PathParam("id") @DefaultValue("Lw==") String encodedPath) {
        // Lw== is "/" base64-encoded

        String realPath = FilenameEncoder.decode(encodedPath);

        if (folders.isLeafFolder(realPath)) {
            return Response.ok(pictures.getPictures(realPath)).build();
        }

        return Response.ok(folders.getList(realPath)).build();
    }
}
