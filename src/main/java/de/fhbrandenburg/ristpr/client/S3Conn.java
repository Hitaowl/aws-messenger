/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhbrandenburg.ristpr.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import de.fhbrandenburg.ristpr.client.*;

/**
 *
 * @author taake
 */
public class S3Conn {
    
   public static void picUpload(String filepath) throws IOException{
       
         Random rand = new Random();
         String existingBucketName = "awsimagefhb2";
         String keyName = "image" + rand.nextInt(100000000) + ".jpg";
         String S3Url = "https://s3.amazonaws.com/awsimagefhb2/";
         
         
         String amazonFileUploadLocationOriginal=existingBucketName+"/";
         
         AmazonS3 s3Client = new AmazonS3Client(new PropertiesCredentials(S3Conn.class.getResourceAsStream("AwsCreds.properties")));
         FileInputStream stream = new FileInputStream(filepath);
         ObjectMetadata objectMetadata = new ObjectMetadata();
         PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, stream, objectMetadata);
         PutObjectResult result = s3Client.putObject(putObjectRequest);
         
         de.fhbrandenburg.ristpr.client.Client client = new de.fhbrandenburg.ristpr.client.Client();
         client.sendCommend("LINK:"+S3Url + keyName);
         
         
        
       
   }
    
}
