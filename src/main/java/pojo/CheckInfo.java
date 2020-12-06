package pojo;

import java.util.Date;
import java.io.Serializable;
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
public class CheckInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String checkId;

    private String companyId;

    private Integer checkStatus;

    private Date createTime;


}
