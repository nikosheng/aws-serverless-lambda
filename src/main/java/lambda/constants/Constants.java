package lambda.constants;

import amazon.aws.lambda.enumeration.DynamodbEnum;
import amazon.aws.lambda.enumeration.RDSEnum;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

/**
 * Constants
 *
 * @author: jiasfeng
 * @Date: 7/6/2018
 */
public class Constants {
    /**
     * Common
     */
    public final static String SUCCESS = "SUCCESS";
    public final static String EXCEPTION = "EXCEPTION";

    /**
     * Dynamodb
     */
    public final static String DYNAMODB_TABLE_CONTRACTS = "Contracts";
    public final static String DYNAMODB_ENDPOINT = DynamodbEnum.CN_NORTH_1.getEndpoint();
    public final static String DYNAMODB_REGION = DynamodbEnum.CN_NORTH_1.getRegion();


    /**
     * RDS
     */
    public final static String RDS_TABLE_CONTRACTS = "contract";
    public final static String RDS_NORTHWEST_ENDPOINT = RDSEnum.CN_NORTHWEST_1.getEndpoint();
    public final static String RDS_NORTHWEST_REGION = RDSEnum.CN_NORTHWEST_1.getRegion();
    public final static String RDS_NORTH_ENDPOINT = RDSEnum.CN_NORTH_1.getEndpoint();
    public final static String RDS_NORTH_REGION = RDSEnum.CN_NORTH_1.getRegion();
    public final static String RDS_LAB_INSTANCE_HOSTNAME = "lab.cxbwswfsjviz.rds.cn-northwest-1.amazonaws.com.cn";
    public final static int RDS_LAB_INSTANCE_PORT = 3306;
    public final static String RDS_DB_USER = "admin";
    public final static String RDS_DB_PASSWD = "abcd1234";
    public final static String RDS_DB_LAB = "labdb";
    public final static String RDS_NORTHWEST_CONTRACT_JDBC = "jdbc:mysql://" + RDS_LAB_INSTANCE_HOSTNAME + ":" + RDS_LAB_INSTANCE_PORT + "/" + RDS_DB_LAB;

    //AWS Credentials of the IAM user with policy enabling IAM Database Authenticated access to the db by the db user.
    public final static DefaultAWSCredentialsProviderChain creds = new DefaultAWSCredentialsProviderChain();
    public final static String AWS_ACCESS_KEY = creds.getCredentials().getAWSAccessKeyId();
    public final static String AWS_SECRET_KEY = creds.getCredentials().getAWSSecretKey();
}
