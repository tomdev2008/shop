 package com.rt.shop.manage.admin.action;
 
 import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.plugins.Page;
import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.rt.shop.common.annotation.SecurityMapping;
import com.rt.shop.common.tools.CommUtil;
import com.rt.shop.entity.Address;
import com.rt.shop.entity.GroupArea;
import com.rt.shop.entity.query.GroupAreaQueryObject;
import com.rt.shop.manage.util.WebForm;
import com.rt.shop.mv.JModelAndView;
import com.rt.shop.service.IGroupAreaService;
import com.rt.shop.service.ISysConfigService;
import com.rt.shop.service.IUserConfigService;
import com.rt.shop.util.CommWebUtil;
 
 @Controller
 public class GroupAreaManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGroupAreaService groupareaService;
 
   @SecurityMapping(display = false, rsequence = 0, title="团购区域列表", value="/admin/group_area_list.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_area_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/group_area_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     GroupAreaQueryObject qo = new GroupAreaQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.parent.id is null", null);
     Page pList = this.groupareaService.selectPage(new Page<GroupArea>(Integer.valueOf(CommUtil.null2Int(currentPage)), 12), null);
     CommWebUtil.saveIPageList2ModelAndView(url + "/admin/group_area_list.htm", 
       "", params, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购区域增加", value="/admin/group_area_add.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_area_add.htm"})
   public ModelAndView add(HttpServletRequest request, HttpServletResponse response, String currentPage, String pid)
   {
     ModelAndView mv = new JModelAndView("admin/blue/group_area_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     GroupArea parent = this.groupareaService.selectById(
       CommUtil.null2Long(pid));
     GroupArea obj = new GroupArea();
     obj.setParent(parent);
     GroupArea sGroupArea=new GroupArea();
     sGroupArea.setParent_id(null);
     List gas = this.groupareaService.selectList(sGroupArea);
     mv.addObject("gas", gas);
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购区域编辑", value="/admin/group_area_edit.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_area_edit.htm"})
   public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/group_area_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       GroupArea grouparea = this.groupareaService.selectById(
         Long.valueOf(Long.parseLong(id)));
       GroupArea sGroupArea=new GroupArea();
       sGroupArea.setParent_id(null);
       List gas = this.groupareaService.selectList(sGroupArea);
       mv.addObject("gas", gas);
       mv.addObject("obj", grouparea);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购区域保存", value="/admin/group_area_save.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_area_save.htm"})
   public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String pid)
   {
     WebForm wf = new WebForm();
     GroupArea grouparea = null;
     if (id.equals("")) {
       grouparea = (GroupArea)wf.toPo(request, GroupArea.class);
       grouparea.setAddTime(new Date());
     } else {
       GroupArea obj = this.groupareaService
         .selectById(Long.valueOf(Long.parseLong(id)));
       grouparea = (GroupArea)wf.toPo(request, obj);
     }
     GroupArea parent = this.groupareaService.selectById(
       CommUtil.null2Long(pid));
     if (parent != null) {
       grouparea.setParent(parent);
       grouparea.setGa_level(parent.getGa_level() + 1);
     }
     if (id.equals(""))
       this.groupareaService.insertSelective(grouparea);
     else
       this.groupareaService.updateSelectiveById(grouparea);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/group_area_list.htm");
     mv.addObject("op_title", "保存团购区域成功");
     mv.addObject("add_url", CommUtil.getURL(request) + 
       "/admin/group_area_add.htm" + "?currentPage=" + currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="团购区域删除", value="/admin/group_area_del.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_area_del.htm"})
   public String delete(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GroupArea grouparea = this.groupareaService.selectById(
           Long.valueOf(Long.parseLong(id)));
         this.groupareaService.deleteById(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:group_area_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购区域Ajax更新", value="/admin/group_area_ajax.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_area_ajax.htm"})
   public void ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     GroupArea obj = this.groupareaService.selectById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = GroupArea.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields)
     {
       if (field.getName().equals(fieldName)) {
         Class clz = Class.forName("java.lang.String");
         if (field.getType().getName().equals("int")) {
           clz = Class.forName("java.lang.Integer");
         }
         if (field.getType().getName().equals("boolean")) {
           clz = Class.forName("java.lang.Boolean");
         }
         if (!value.equals(""))
           val = BeanUtils.convertType(value, clz);
         else {
           val = Boolean.valueOf(
             !CommUtil.null2Boolean(wrapper
             .getPropertyValue(fieldName)));
         }
         wrapper.setPropertyValue(fieldName, val);
       }
     }
     this.groupareaService.updateSelectiveById(obj);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(val.toString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购区域下级加载", value="/admin/group_area_data.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/group_area_data.htm"})
   public ModelAndView group_area_data(HttpServletRequest request, HttpServletResponse response, String pid, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/group_area_data.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
    
     GroupArea sGroupArea=new GroupArea();
     sGroupArea.setParent_id(CommUtil.null2Long(pid));
     List gas = this.groupareaService.selectList(sGroupArea);
     mv.addObject("gas", gas);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @RequestMapping({"/admin/group_area_verify.htm"})
   public void group_area_verify(HttpServletRequest request, HttpServletResponse response, String ga_name, String id, String pid) {
     boolean ret = true;
     
     String sql="where ga_name='"+ga_name+"' and parent_id="+CommUtil.null2Long(pid)+" and id!="+CommUtil.null2Long(id);
     List gcs = this.groupareaService.selectList(sql,null);

     if ((gcs != null) && (gcs.size() > 0)) {
       ret = false;
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(ret);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 