package com.zemiak.nasphotos.files.boundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.zemiak.nasphotos.files.control.MediaStreamer;
import com.zemiak.nasphotos.files.control.VideoControl;

@Path("/backend/streaming")
@RequestScoped
public class StreamingResource {
    @Inject
    VideoControl videos;

	final static Logger logger = Logger.getLogger( StreamingResource.class.getName() );

    private static final int CHUNK_SIZE = 1024 * 1024 * 2; // 2 MB chunks

    // for clients to check whether the server supports range / partial content requests
    @HEAD
    @Path("{path}")
    @Produces("video/mp4")
    public Response header(@PathParam("path") String path) {
        logger.info("@HEAD request received: " + path);

        String relPath = videos.getRealPathForStreaming(path);
        if (null == relPath) {
            return Response.status(Status.FORBIDDEN).entity("Path " + path + " is unsafe").build();
        }
        File file = new File( videos.getRealPathForStreaming(relPath) );

        return Response.ok()
            .status( Response.Status.PARTIAL_CONTENT )
            .header( HttpHeaders.CONTENT_LENGTH, file.length() )
            .header( "Accept-Ranges", "bytes" )
            .build();
    }

    @GET
    @Path("{path}")
    @Produces("video/mp4")
    public Response stream( @HeaderParam("Range") String range, @PathParam("path") String path ) throws Exception {
        logger.info("@GET request received: " + path + ", range: " + range);

        String relPath = videos.getRealPathForStreaming(path);
        if (null == relPath) {
            return Response.status(Status.FORBIDDEN).entity("Path " + path + " is unsafe").build();
        }
        File file = new File(relPath);

        return buildStream( file, range );
    }

    /**
     * @param asset Media file
     * @param range range header
     * @return Streaming output
     * @throws Exception IOException if an error occurs in streaming.
     */
    private Response buildStream( final File asset, final String range ) throws Exception {
        // range not requested: firefox does not send range headers
        if ( range == null ) {
        	logger.info("Request does not contain a range parameter!");

            StreamingOutput streamer = output -> {
                try ( FileChannel inputChannel = new FileInputStream( asset ).getChannel();
                	  WritableByteChannel outputChannel = Channels.newChannel( output ) ) {

                    inputChannel.transferTo( 0, inputChannel.size(), outputChannel );
                }
                catch( IOException io ) {
                    logger.severe("buildStream/no-range exception");
                	logger.info( io.getMessage() );
                }
            };

            return Response.ok( streamer )
                .status( Response.Status.OK )
                .header( HttpHeaders.CONTENT_LENGTH, asset.length() )
                .build();
        }

        logger.info( "Requested Range: " + range );

        String[] ranges = range.split( "=" )[1].split( "-" );

        int from = Integer.parseInt( ranges[0] );

        // Chunk media if the range upper bound is unspecified
        int to = CHUNK_SIZE + from;

        if ( to >= asset.length() ) {
            to = (int) ( asset.length() - 1 );
        }

        // uncomment to let the client decide the upper bound
        // we want to send 2 MB chunks all the time
        if ( ranges.length == 2 ) {
           to = Integer.parseInt( ranges[1] );
        }

        final String responseRange = String.format( "bytes %d-%d/%d", from, to, asset.length() );

        logger.info( "Response Content-Range: " + responseRange + "\n");

        final RandomAccessFile raf = new RandomAccessFile( asset, "r" );
        raf.seek( from );

        final int len = to - from + 1;
        final MediaStreamer mediaStreamer = new MediaStreamer( len, raf );

        return Response.ok( mediaStreamer )
                .status( Response.Status.PARTIAL_CONTENT )
                .header( "Accept-Ranges", "bytes" )
                .header( "Content-Range", responseRange )
                .header( HttpHeaders.CONTENT_LENGTH, mediaStreamer. getLenth() )
                .header( HttpHeaders.LAST_MODIFIED, new Date( asset.lastModified() ) )
                .build();
    }
}
