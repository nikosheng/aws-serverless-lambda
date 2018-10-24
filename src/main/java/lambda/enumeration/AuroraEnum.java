package lambda.enumeration;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 7/9/2018
 */
public enum AuroraEnum {
    CN_NORTHWEST_1("rds.cn-northwest-1.amazonaws.com.cn", "cn-northwest-1"),
    ;

    private String endpoint;
    private String region;

    private AuroraEnum(String endpoint, String region) {
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
