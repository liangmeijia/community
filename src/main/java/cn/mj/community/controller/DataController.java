package cn.mj.community.controller;

import cn.mj.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    //record html
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getRecordPage(){
        return "/site/admin/data";
    }

    //
    @RequestMapping(path = "/data/uv",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end,
                        Model model){
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uv",uv);
        model.addAttribute("uvStart",start);
        model.addAttribute("uvEnd",end);
        return "forward:/data";
    }

    @RequestMapping(path = "/data/dau",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end,
                        Model model){
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dau",dau);
        model.addAttribute("dauStart",start);
        model.addAttribute("dauEnd",end);
        return "forward:/data";
    }
}
