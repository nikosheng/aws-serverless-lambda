package lambda.rds.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
@Data
public class Contract {
    private String contractId;
    private String contractNum;
    private String clientMobile;
    private String clientNum;
    private String capital;
    private Integer contractStatus;
    private String contractName;
    private String directory;
    private Date signDate;
    private String identityCardNum;
    private String s3Bucket;
    private String s3Key;
    private Date createTime;
    private Date updateTime;
    private String operator;
    private Integer delete;
}
