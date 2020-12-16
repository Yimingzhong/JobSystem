package pojo;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 560寝室
 * @since 2020-12-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeliverRecordInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int ACCEPTED = 2;//接受OFFER
    public static final int ADOPTED = 1;//企业发放offer
    public static final int DELIVERED = 0; //简历已经投递
    public static final int UNADOPTED = -1; //未被企业录用
    public static final int UNACCEPTED = -2;//拒绝企业OFFER

    @TableId(type = IdType.ASSIGN_UUID)
    private String deliverId;

    private String jobId;
    @TableField(exist = false)
    private JobInfo jobInfo;

    private String resumeId;
    @TableField(exist = false)
    private ResumeInfo resumeInfo;

    @TableField(fill = FieldFill.INSERT)
//    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    private Integer status;


}
