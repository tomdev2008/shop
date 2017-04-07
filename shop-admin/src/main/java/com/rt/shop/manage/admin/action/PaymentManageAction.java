 package com.rt.shop.manage.admin.action;
 
 import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rt.shop.common.annotation.SecurityMapping;
import com.rt.shop.common.tools.CommUtil;
import com.rt.shop.entity.Payment;
import com.rt.shop.entity.SysConfig;
import com.rt.shop.manage.util.WebForm;
import com.rt.shop.mv.JModelAndView;
import com.rt.shop.service.IPaymentService;
import com.rt.shop.service.ISysConfigService;
import com.rt.shop.service.IUserConfigService;
import com.rt.shop.tools.PaymentTools;
 
 @Controller
 public class PaymentManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IPaymentService paymentService;
 
   @Autowired
   private PaymentTools paymentTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="支付方式列表", value="/admin/payment_list.htm*", rtype="admin", rname="支付方式", rcode="payment_set", rgroup="设置")
   @RequestMapping({"/admin/payment_list.htm"})
   public ModelAndView payment_list(HttpServletRequest request, HttpServletResponse response, String type)
   {
     ModelAndView mv = new JModelAndView("admin/blue/payment_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (CommUtil.null2String(type).equals("user"))
       mv.addObject("op", "store");
     else
       mv.addObject("op", "platform");
     SysConfig config = this.configService.getSysConfig();
     String store_payment = CommUtil.null2String(config.getStore_payment());
     Map map = (Map)Json.fromJson(HashMap.class, store_payment);
     if (map != null) {
       for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
         String key = (String)it.next();
         Object val = map.get(key);
         mv.addObject(key, val);
       }
     }
     mv.addObject("paymentTools", this.paymentTools);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="支付方式设置", value="/admin/payment_set.htm*", rtype="admin", rname="支付方式", rcode="payment_set", rgroup="设置")
   @RequestMapping({"/admin/payment_set.htm"})
   public ModelAndView payment_set(HttpServletRequest request, HttpServletResponse response, String mark, String type, String pay, String config_id) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (CommUtil.null2String(type).equals("admin")) {
      
       Payment sPayment=new Payment();
       sPayment.setMark(mark);
       sPayment.setType(type);
       List objs = this.paymentService.selectList(sPayment);
       Payment obj = null;
       if (objs.size() > 0)
         obj = (Payment)objs.get(0);
       else
         obj = new Payment();
       obj.setAddTime(new Date());
       obj.setMark(mark);
       obj.setInstall(!CommUtil.null2Boolean(pay));
       obj.setType(type);
       if (CommUtil.null2String(obj.getName()).equals("")) {
         if (mark.trim().equals("alipay")) {
           obj.setName("支付宝");
         }
         if (mark.trim().equals("balance")) {
           obj.setName("预存款支付");
         }
         if (mark.trim().equals("outline")) {
           obj.setName("线下支付");
         }
         if (mark.trim().equals("tenpay")) {
           obj.setName("财付通");
         }
         if (mark.trim().equals("yikatong")) {
             obj.setName("甜园云卡");
           }
         if (mark.trim().equals("ka3800ka")) {
             obj.setName("未来卡");
           }
         if (mark.trim().equals("bill")) {
           obj.setName("快钱支付");
         }
         if (mark.trim().equals("chinabank")) {
           obj.setName("网银在线");
         }
         if (mark.trim().equals("alipay_wap")) {
           obj.setName("支付宝手机网页支付");
         }
       }
       if (objs.size() > 0)
         this.paymentService.updateSelectiveById(obj);
       else
         this.paymentService.insertSelective(obj);
     }
     if (CommUtil.null2String(type).equals("user")) {
       SysConfig config = this.configService.getSysConfig();
       String store_payment = CommUtil.null2String(config
         .getStore_payment());
       Map map = (Map)Json.fromJson(HashMap.class, store_payment);
       if (map == null)
         map = new HashMap();
       map.put(mark, Boolean.valueOf(!CommUtil.null2Boolean(pay)));
       store_payment = Json.toJson(map, JsonFormat.compact());
       config.setStore_payment(store_payment);
       if (!CommUtil.null2String(config_id).equals(""))
         this.configService.updateSelectiveById(config);
       else
         this.configService.insertSelective(config);
     }
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/payment_list.htm?type=" + type);
     mv.addObject("op_title", "设置支付方式成功");
     mv.addObject("paymentTools", this.paymentTools);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="支付方式编辑", value="/admin/payment_edit.htm*", rtype="admin", rname="支付方式", rcode="payment_set", rgroup="设置")
   @RequestMapping({"/admin/payment_edit.htm"})
   public ModelAndView payment_edit(HttpServletRequest request, HttpServletResponse response, String mark) {
     ModelAndView mv = new JModelAndView("admin/blue/payment_info.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Payment sPayment=new Payment();
     sPayment.setMark(mark);
     sPayment.setType("admin");
     List objs = this.paymentService.selectList(sPayment);
     Payment obj = null;
     if (objs.size() > 0) {
       obj = (Payment)objs.get(0);
     } else {
       obj = new Payment();
       obj.setMark(mark);
     }
     mv.addObject("obj", obj);
     mv.addObject("edit", Boolean.valueOf(true));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="支付方式保存", value="/admin/payment_save.htm*", rtype="admin", rname="支付方式", rcode="payment_set", rgroup="设置")
   @RequestMapping({"/admin/payment_save.htm"})
   public ModelAndView payment_save(HttpServletRequest request, HttpServletResponse response, String mark, String list_url) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Payment sPayment=new Payment();
     sPayment.setMark(mark);
     sPayment.setType("admin");
     List objs = this.paymentService.selectList(sPayment);
     Payment obj = null;
     if (objs.size() > 0) {
       Payment temp = (Payment)objs.get(0);
       WebForm wf = new WebForm();
       obj = (Payment)wf.toPo(request, temp);
     } else {
       WebForm wf = new WebForm();
       obj = (Payment)wf.toPo(request, Payment.class);
       obj.setAddTime(new Date());
       obj.setType("admin");
     }
     if (objs.size() > 0) {
       this.paymentService.updateSelectiveById(obj);
     }
     else {
       this.paymentService.insertSelective(obj);
     }
     mv.addObject("op_title", "保存支付方式成功");
     mv.addObject("list_url", list_url);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="平台支付方式设置", value="/admin/payment_config_set.htm*", rtype="admin", rname="支付方式", rcode="payment_set", rgroup="设置")
   @RequestMapping({"/admin/payment_config_set.htm"})
   public ModelAndView payment_config_set(HttpServletRequest request, HttpServletResponse response, String id, String config_payment_type)
   {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SysConfig obj = this.configService.getSysConfig();
     WebForm wf = new WebForm();
     SysConfig sysConfig = null;
     if (id.equals(""))
       sysConfig = (SysConfig)wf.toPo(request, SysConfig.class);
     else {
       sysConfig = (SysConfig)wf.toPo(request, obj);
     }
     sysConfig
       .setConfig_payment_type(CommUtil.null2Int(config_payment_type));
     if (id.equals(""))
       this.configService.insertSelective(sysConfig);
     else {
       this.configService.updateSelectiveById(sysConfig);
     }
     mv.addObject("op_title", "设置统一支付成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/payment_list.htm");
     return mv;
   }
 }


 
 
 