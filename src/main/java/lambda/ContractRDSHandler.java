package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lambda.constants.Constants;
import lambda.rds.RDSClient;
import lambda.rds.model.Contract;

import java.net.URLDecoder;

/**
 * A Lambda handler to execute the logic of the contract file ETL and route the
 * file name to store in RDS
 *
 * @author: jiasfeng
 * @Date: 7/6/2018
 */
public class ContractRDSHandler implements RequestHandler<S3Event, String> {
    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received event: " + event);
        logger.log(event.toJson());
        logger.log(String.format("RDS Endpoint: %s\n", Constants.RDS_NORTHWEST_ENDPOINT));
        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            key = URLDecoder.decode(key, "UTF-8");
            logger.log(String.format("S3 Bucket: %s\n", bucket));
            logger.log(String.format("S3 Object Key: %s\n", key));
            // Example: V10001.pdf ==> V10001
            String contractCode = key.substring(key.lastIndexOf("/") + 1, key.lastIndexOf("."));
            logger.log(String.format("Contract Num:  %s\n", contractCode));

            // Insert items into DynamoDB
            S3Object response = s3.getObject(new GetObjectRequest(bucket, key));
            logger.log("Adding a new item...");

            Contract contract = new Contract();
            contract.setContractNum(contractCode);
            contract.setS3Bucket(bucket);
            contract.setS3Key(key);
            int num = RDSClient.insert(Constants.RDS_TABLE_CONTRACTS, contract);
            logger.log(String.format("PutItem %d item(s) succeeded:\n", num));

            return Constants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(String.format(
                    "Error getting object %s from bucket %s. Make sure they exist and"
                            + " your bucket is in the same region as this function.", key, bucket));
        }
        return Constants.EXCEPTION;
    }
}
