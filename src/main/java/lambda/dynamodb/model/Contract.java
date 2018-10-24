package lambda.dynamodb.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 7/8/2018
 */
@Data
public class Contract {
    private String contractCode;
    private Date uploadDate;
    private String bucketName;
    private String objectKey;
}
