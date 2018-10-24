package lambda;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lambda.constants.Constants;
import lambda.dynamodb.ContractsDynamoDBQuery;
import lambda.dynamodb.DynamoDBClient;
import lambda.dynamodb.model.Contract;

import java.util.List;

import static lambda.constants.Constants.DYNAMODB_TABLE_CONTRACTS;
import static lambda.utils.Utils.UTCToCST;


/**
 * A Lambda handler to execute the logic of the contract file ETL and route the
 * file name to store in DynamoDB
 *
 * @author: jiasfeng
 * @Date: 7/6/2018
 */
public class ContractDynamodbHandler implements RequestHandler<S3Event, String> {
    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received event: " + event);
        logger.log(event.toJson());
        logger.log("DynamoDB Endpoint: " + Constants.DYNAMODB_ENDPOINT);

        DynamoDB dynamoDB = DynamoDBClient.getDynamoDBClient();
        // Define the DynamoDB Table
        Table table = dynamoDB.getTable(DYNAMODB_TABLE_CONTRACTS);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            // Example: V10001.pdf ==> V10001
            String contractCode = key.substring(key.lastIndexOf("/") + 1, key.lastIndexOf("."));
            logger.log("Contract Code: " + contractCode);

            // Insert items into DynamoDB
            S3Object response = s3.getObject(new GetObjectRequest(bucket, key));
            String uploadDate = UTCToCST(response.getObjectMetadata().getLastModified());
            logger.log("Upload Date: " + uploadDate);
            logger.log("Adding a new item...");

            if (!checkItemExists(contractCode)) {
                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("contract_code", contractCode,
                                "upload_date", uploadDate)
                                .withString("bucket_name", bucket)
                                .withString("object_key", key));
                logger.log("PutItem succeeded:\n" + outcome.getPutItemResult());
            } else {
                logger.log("The item is already existed in dynamodb...");
            }

            return Constants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(String.format(
                    "Error getting object %s from bucket %s. Make sure they exist and"
                            + " your bucket is in the same region as this function.", key, bucket));
            throw e;
        }
    }

    private boolean checkItemExists(String contractCode) {
        List<Contract> contracts = ContractsDynamoDBQuery.query(DYNAMODB_TABLE_CONTRACTS, contractCode);
        return contracts.size() > 0;
    }


}
