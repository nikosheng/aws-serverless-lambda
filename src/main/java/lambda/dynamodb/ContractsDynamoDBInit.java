package lambda.dynamodb;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import lambda.constants.Constants;

import java.util.Arrays;

/**
 * A template class to create the DynamoDB table
 *
 * @author: jiasfeng
 * @Date: 7/6/2018
 */
public class ContractsDynamoDBInit {
    public static void main(String[] args) {
        DynamoDB dynamoDB = DynamoDBClient.getDynamoDBClient();

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(Constants.DYNAMODB_TABLE_CONTRACTS,
                    Arrays.asList(new KeySchemaElement("contract_code", KeyType.HASH), // Partition
                            // key
                            new KeySchemaElement("upload_date", KeyType.RANGE)), // Sort key
                    Arrays.asList(new AttributeDefinition("contract_code", ScalarAttributeType.S),
                            new AttributeDefinition("upload_date", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }
}
