package controller;



import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import pojo.DeliverRecordInfo;
import pojo.JobInfo;
import pojo.ResumeInfo;
import pojo.StudentInfo;
import pojo.requestBody.GetJobsRequestBody;
import service.*;
import util.json.RestResult;
import util.json.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 560寝室
 * @since 2020-12-06
 */
@Controller
public class StudentInfoController {
    @Autowired
    private StudentInfoService studentInfoService;
    @Autowired
    private ResumeInfoService resumeInfoService;
    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private DeliverRecordInfoService deliverRecordInfoService;

    @RequestMapping("/getInfo") //请求路径（ajax接口）
    @ResponseBody   //直接返回字符串  不经过视图解析
    public String getStudentList(){
        List<StudentInfo> list = studentInfoService.list();

        return new RestResult()
                .setCode(ResultCode.SUCCESS)
                .setMessage("成功")
                .setData(list)
                .toString();
    }
    @RequestMapping("/getMyInfo")//请求路径（ajax接口）
    @ResponseBody
    public String getStudentInfo(HttpSession session){
        String studentId = (String) session.getAttribute("user");
        StudentInfo student = studentInfoService.getById(studentId);
        return new RestResult()
                .setCode(ResultCode.SUCCESS)
                .setMessage("成功")
                .setData(student)
                .toString();
    }
    @RequestMapping("/getMyResumes")
    @ResponseBody
    public String getStudentResumes(HttpSession session){
        String studentId = (String) session.getAttribute("user");
        //存放查询结果的list
        List<ResumeInfo> resumeInfos;
        //设置查询条件的wrapper
        QueryWrapper<ResumeInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id",studentId);
        //执行查询
        resumeInfos = resumeInfoService.list(wrapper);
        //返回结果
        return new RestResult()
                .setCode(ResultCode.SUCCESS)
                .setData(resumeInfos)
                .toString();
    }
    @RequestMapping("/uploadResume")
    @ResponseBody
    public String uploadResume(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        String studentId = (String) request.getSession().getAttribute("user");
        String resumeName = file.getOriginalFilename();
        //传输简历pdf文件
        //设置文件存储路径
        String storagePath = "/Job/uploads/"+studentId;
        String realPath = request.getRealPath("/uploads/"+studentId);
        //如果文件存储路径不存在，则新建
        File filePath = new File(realPath,resumeName);
        if(!filePath.getParentFile().exists()){
            filePath.getParentFile().mkdirs();
        }
        //判断文件是否为空
        if(!file.isEmpty()){
            try{
                file.transferTo(new File(realPath+ File.separator+resumeName));//把文件写入目标文件地址
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        //更新数据库
        ResumeInfo resumeInfo = new ResumeInfo();
        resumeInfo.setResumeName(resumeName);
        resumeInfo.setResumeStatus(0);//unknow
        resumeInfo.setStudentId(studentId);
        resumeInfo.setUrl(storagePath+"/"+resumeName);
        resumeInfoService.save(resumeInfo);
        return new RestResult().setCode(ResultCode.SUCCESS).toString();
    }
    @RequestMapping("/deleteResume")
    @ResponseBody
    public String deleteResume(HttpSession session, @RequestBody(required = true)Map<String,Object> map){
        String studentId = (String) session.getAttribute("user");
        String resumeId = (String) map.get("resumeId");
        ResumeInfo resumeInfo = resumeInfoService.getById(resumeId);
        resumeInfo.setResumeStatus(-1);
        resumeInfoService.updateById(resumeInfo);
        return new RestResult().setCode(ResultCode.SUCCESS).toString();
    }
    @RequestMapping("/getJobs")
    @ResponseBody
    public String getJobs(HttpSession session, @RequestBody GetJobsRequestBody getJobsRequestBody){
        String studentId = (String) session.getAttribute("user");
        String companyName = getJobsRequestBody.getCompanyName();
        String jobName = getJobsRequestBody.getJobName();
        String jobType = getJobsRequestBody.getJobType();
        System.out.println(companyName);
        System.out.println(jobName);
        System.out.println(jobType);
        //设置分页器
        int current = 1;
        int size = 20;
        Page<JobInfo> jobInfoIPage = new Page<>(current,size);
        //设置查询条件
        QueryWrapper<JobInfo> wrapper = new QueryWrapper<>();
        if (companyName != null && !companyName.trim().equals("")) {
            wrapper.like("company_name",companyName);
        }
        if (jobName != null && !jobName.trim().equals("")) {
            wrapper.like("job_name",jobName);
        }
        if (jobType != null && !jobType.trim().equals("")) {
            wrapper.eq("job_type",jobType);
        }
        //执行查询
        List<JobInfo> jobInfos = jobInfoService.listJobsWithCompanyName(jobInfoIPage, wrapper).getRecords();
        //返回结果
        return new RestResult()
                .setCode(ResultCode.SUCCESS)
                .setData(jobInfos)
                .toString();
    }
    @RequestMapping("/getRecord")
    @ResponseBody
    public String getDeliverRecord(HttpSession session){
        String studentId = (String) session.getAttribute("user");
        int current = 1;
        int size = 20;
        IPage<DeliverRecordInfo> deliverRecordInfoIPage = new Page<>(current,size);
        //设置查询条件的wrapper
        QueryWrapper<DeliverRecordInfo> deliverRecordInfoWrapper = new QueryWrapper<>();
        deliverRecordInfoWrapper.eq("student_id",studentId);
        //执行查询
        List<DeliverRecordInfo> deliverRecordInfos = deliverRecordInfoService.listDeliverRecordInfoWithJobNameAndCompanyName(deliverRecordInfoWrapper);
        //返回结果
        return new RestResult()
                .setCode(ResultCode.SUCCESS)
                .setData(deliverRecordInfos)
                .toString();
    }

    @RequestMapping("/commitOffer")
    @ResponseBody
    public String commit(String deliverId,boolean flag){
        return deliverRecordInfoService.commitOffer(deliverId,flag);
    }

    @RequestMapping("/deliverResume")
    @ResponseBody
    public String deliver(String resumeId,String jobId){
        System.out.println(resumeId);
        System.out.println(jobId);
        DeliverRecordInfo info = new DeliverRecordInfo();
        info.setJobId(jobId);
        info.setResumeId(resumeId);
        info.setStatus(DeliverRecordInfo.DELIVERED);
        deliverRecordInfoService.save(info);
        return new RestResult().setCode(ResultCode.SUCCESS).setMessage("投递成功").toString();
    }
}

