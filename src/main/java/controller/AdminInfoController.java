package controller;


import mapper.CompanyInfoMapper;
import mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import pojo.AdminInfo;
import pojo.CompanyInfo;
import pojo.StudentInfo;
import service.AdminInfoService;
import service.CompanyInfoService;
import service.StudentInfoService;
import util.json.RestResult;
import util.json.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pojo.CompanyInfo;
import pojo.StudentInfo;
import service.AdminInfoService;
import service.CompanyInfoService;
import service.StudentInfoService;
import util.json.RestResult;
import util.json.ResultCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 560寝室
 * @since 2020-12-06
 */

@Controller

public class AdminInfoController {
    @Autowired
    private StudentInfoService studentInfoService;
    @Autowired
    private CompanyInfoService companyInfoService;
    @Autowired
    private AdminInfoService adminInfoService;


    @RequestMapping("/showUnsolved")
    @ResponseBody
    public String getUnsolvedCompany(int current,int size) {
        return adminInfoService.selectUnsolvedCompany(CompanyInfo.CHECKING,current,size);
    }

    @RequestMapping("/showAll")
    @ResponseBody
    public String getAllStudent() {
        List<StudentInfo> list = adminInfoService.selectAllStudent();
        return new RestResult().setCode(ResultCode.SUCCESS).setMessage("成功").setData(list).toString();
    }

    @RequestMapping("/addStu")
    @ResponseBody
    public String addStudent(HttpServletRequest request){
        StudentInfo student=new StudentInfo();
        String studentId=request.getParameter("studentId");
        student.setStudentId(studentId);
        String password=request.getParameter("password");
        student.setPassword(password);
        String studentName=request.getParameter("studentName");
        student.setStudentName(studentName);
        int sex=Integer.parseInt(request.getParameter("sex"));
        student.setSex(sex);
        String politicalStatus=request.getParameter("politicalStatus");
        student.setPoliticalStatus(politicalStatus);
        String hometown=request.getParameter("hometown");
        student.setHometown(hometown);
        if(adminInfoService.insertStudent(student)){
            return new RestResult().setCode(ResultCode.SUCCESS).setMessage("成功").setData(student).toString();
        }
        return new RestResult().setCode(ResultCode.FAIL).setMessage("失败").toString();
    }

    @RequestMapping("/company/{id}/{flag}")
    @ResponseBody
    public String checkCompany(@PathVariable("id") String companyId, @PathVariable("flag") boolean flag){
        if(flag){
            CompanyInfo companyInfo=adminInfoService.updateCompany(companyId);
            return new RestResult().setCode(ResultCode.SUCCESS).setMessage("已通过").setData(companyInfo).toString();
        }
        CompanyInfo companyInfo=adminInfoService.rejectCompany(companyId);
        return new RestResult().setCode(ResultCode.SUCCESS).setMessage("未通过").setData(companyInfo).toString();
    }

    @RequestMapping("/download")
    @ResponseBody
    public String downloads(HttpServletResponse response , HttpServletRequest request) throws Exception{
        //要下载的地址
        String  path = request.getRealPath("/downloads");
        String  fileName = "模板.xlsx";
        System.out.println(path);
        //1、设置response 响应头
        response.reset(); //设置页面不缓存,清空buffer
        response.setCharacterEncoding("UTF-8"); //字符编码
        response.setContentType("multipart/form-data"); //二进制传输数据
        //设置响应头
        response.setHeader("Content-Disposition", "attachment;fileName="+ URLEncoder.encode(fileName, "UTF-8"));

        File file = new File(path,fileName);
        //2、 读取文件--输入流
        InputStream input=new FileInputStream(file);
        //3、 写出文件--输出流
        OutputStream out = response.getOutputStream();

        byte[] buff =new byte[1024];
        int index=0;
        //4、执行 写出操作
        while((index= input.read(buff))!= -1){
            out.write(buff, 0, index);
            out.flush();
        }
        out.close();
        input.close();
        return null;
    }

    @RequestMapping("/uploadStudent")
    @ResponseBody
    public String uploadStudent(@RequestBody List<Map> parmaMap){
        new Thread(
                ()->{
                    for(Map<String,String> map : parmaMap){
                        StudentInfo stu = new StudentInfo();
                        stu.setStudentId(map.get("id"));
                        stu.setPassword(map.get("password"));
                        stu.setStudentName(map.get("name"));
                        stu.setSex(map.get("sex").equals("男")?1:0);
                        stu.setHometown(map.get("hometown"));
                        stu.setPoliticalStatus(map.get("political"));
                        studentInfoService.save(stu);

                    }
                }
        ).start();
        return new RestResult().setCode(ResultCode.SUCCESS).setMessage("添加成功").toString();
    }

    @ResponseBody
    @RequestMapping("/company/{companyId}")
    public String searchCompanyById(@PathVariable("companyId") String companyId){
        CompanyInfo companyInfo=adminInfoService.searchCompany(companyId);
        if(companyInfo!=null){
            return new RestResult().setCode(ResultCode.SUCCESS).setData(companyInfo).toString();
        }
        return new RestResult().setCode(ResultCode.FAIL).toString();
    }
}

