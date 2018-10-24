package lambda.enumeration;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 7/9/2018
 */
public enum RDSEnum {
    CN_NORTH_1("rds.cn-north-1.amazonaws.com.cn", "cn-north-1"),
    CN_NORTHWEST_1("rds.cn-northwest-1.amazonaws.com.cn", "cn-northwest-1"),
    ;

    private String endpoint;
    private String region;

    private RDSEnum(String endpoint, String region) {
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
