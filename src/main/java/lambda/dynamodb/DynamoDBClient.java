package lambda.dynamodb;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import lambda.constants.Constants;

/**
 * A singleton to create the DynamoDB client
 *
 * @author: jiasfeng
 * @Date: 7/6/2018
 */
public class DynamoDBClient {
    private DynamoDBClient(){}

    private static class DynamnoDBClientHolder {
        private final static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(Constants.DYNAMODB_ENDPOINT,
                                Constants.DYNAMODB_REGION))
                .build();

        private final static DynamoDB dynamoDB = new DynamoDB(client);
    }

    public static DynamoDB getDynamoDBClient() {
        return DynamnoDBClientHolder.dynamoDB;
    }
}
