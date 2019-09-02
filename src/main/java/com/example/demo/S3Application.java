package com.example.demo;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Application {

    private static final AWSCredentials credentials;
    private static String bucketName;  

    static {
        //put your accesskey and secretkey here
        credentials = new BasicAWSCredentials(
          "########", 
          "########"
        );
    }
    
    public static void main(String[] args) throws IOException {
        //set-up the client
        AmazonS3 s3client = AmazonS3ClientBuilder
          .standard()
          .withCredentials(new AWSStaticCredentialsProvider(credentials))
          .withRegion(Regions.US_EAST_1)
          .build();
        
        AWSS3Service awsService = new AWSS3Service(s3client);

        bucketName = "logstash-0212";

//        //creating a bucket
//        if(awsService.doesBucketExist(bucketName)) {
//            System.out.println("Bucket name is not available."
//              + " Try again with a different Bucket name.");
//            return;
//        }
//        awsService.createBucket(bucketName);
//        
        //list all the buckets
        for(Bucket s : awsService.listBuckets() ) {
            System.out.println(s.getName());
        }
        
        //deleting bucket
//        awsService.deleteBucket("baeldung-bucket-test2");
        
        //uploading object
        awsService.putObject(
          bucketName, 
          "pom.xml",
          new File("C:\\Users\\mm\\Desktop\\demo\\pom.xml")
        );

        //listing objects
        ObjectListing objectListing = awsService.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println(os.getKey());
        }

        //downloading an object
        S3Object s3object = awsService.getObject(bucketName, "pom.xml");
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileUtils.copyInputStreamToFile(inputStream, new File("C:\\Users\\mm\\Desktop\\demo\\pom_download.xml"));
        
               
        //deleting an object
        awsService.deleteObject(bucketName, "pom.xml");

        //deleting multiple objects
        String objkeyArr[] = {
          "pom.xml"
        };
        
        DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucketName)
          .withKeys(objkeyArr);
        awsService.deleteObjects(delObjReq);
    }
}