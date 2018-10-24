package lambda.enumeration;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 7/9/2018
 */
public enum DynamodbEnum {
    CN_NORTH_1("dynamodb.cn-north-1.amazonaws.com.cn", "cn-north-1"),
    US_EAST_1("dynamodb.us-east-1.amazonaws.com", "us-east-1")
    ;

    private String endpoint;
    private String region;

    private DynamodbEnum(String endpoint, String region) {
        this.endpoint = endpoint;
        this.region = region;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getRegion() {
        return region;
    }
}
