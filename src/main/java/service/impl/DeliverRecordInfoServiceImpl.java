package service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import pojo.DeliverRecordInfo;
import mapper.DeliverRecordInfoMapper;
import service.DeliverRecordInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import util.json.RestResult;
import util.json.ResultCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 560寝室
 * @since 2020-12-06
 */
@Service
public class DeliverRecordInfoServiceImpl extends ServiceImpl<DeliverRecordInfoMapper, DeliverRecordInfo> implements DeliverRecordInfoService {
    @Autowired
    private DeliverRecordInfoMapper deliverRecordInfoMapper;
    public List<DeliverRecordInfo> listDeliverRecordInfoWithJobNameAndCompanyName(Wrapper wrapper){
        return deliverRecordInfoMapper.listDeliverRecordInfoWithJobNameAndCompanyName(wrapper);
    }

    @Override
    public String getDeliverRecordBySnameCid(int current,int size,String studentName, String companyId) {
        Page<DeliverRecordInfo> page = new Page<>(current,size);
        IPage<DeliverRecordInfo> deliverRecordBySnameCid = deliverRecordInfoMapper.getDeliverRecordBySnameCid(page,studentName, companyId);
        List<DeliverRecordInfo> records = deliverRecordBySnameCid.getRecords();

        List<HashMap<String , Object>> restList = new ArrayList<>();
        for(DeliverRecordInfo record : records){
            HashMap<String , Object> hs = new HashMap<>();
            hs.put("deliverId",record.getDeliverId());
            hs.put("total",deliverRecordBySnameCid.getTotal());
            hs.put("jobName",record.getJobInfo().getJobName());
            hs.put("jobType",record.getJobInfo().getJobType());
            hs.put("resumeName",record.getResumeInfo().getResumeName());
            hs.put("studentName",record.getResumeInfo().getStudentInfo().getStudentName());
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            hs.put("time",sd.format(record.getCreateTime()));
            hs.put("resumeId",record.getResumeId());
            hs.put("deliverStatus",record.getStatus());
            hs.put("url",record.getResumeInfo().getUrl());
            System.out.println(record.getResumeInfo().getUrl());
            restList.add(hs);
        }
        return new RestResult().setData(restList).setCode(ResultCode.SUCCESS).toString();
    }

    @Override
    public String employ(String deliverId, boolean flag) {
        DeliverRecordInfo recordInfo = deliverRecordInfoMapper.selectById(deliverId);
        RestResult restResult = new RestResult().setCode(ResultCode.SUCCESS);
        if(flag){
            recordInfo.setStatus(DeliverRecordInfo.ADOPTED);
            restResult.setMessage("已发送录用");
        }

        else{
            recordInfo.setStatus(DeliverRecordInfo.UNADOPTED);
            restResult.setMessage("已发送拒用");
        }
        deliverRecordInfoMapper.updateById(recordInfo);
        return restResult.toString();
    }

    @Override
    public String commitOffer(String deliverId, boolean flag) {
        RestResult restResult = new RestResult();
        DeliverRecordInfo recordInfo = deliverRecordInfoMapper.selectById(deliverId);
        if(recordInfo.getStatus()!=DeliverRecordInfo.ADOPTED){
            return restResult.setCode(ResultCode.FAIL).setMessage("企业未发放offer").toString();
        }
        if(flag==true){
            recordInfo.setStatus(DeliverRecordInfo.ACCEPTED);
            restResult.setCode(ResultCode.SUCCESS).setMessage("已接受offer");
        }else{
            recordInfo.setStatus(DeliverRecordInfo.UNACCEPTED);
            restResult.setCode(ResultCode.SUCCESS).setMessage("已拒绝offer");
        }
        deliverRecordInfoMapper.updateById(recordInfo);
        return restResult.toString();
    }
}
