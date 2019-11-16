package com.myblog.controller;

import com.myblog.entity.Notification;
import com.myblog.entity.User;
import com.myblog.enums.NotificationTypeEnum;
import com.myblog.service.NotificationService;
import com.myblog.vo.MsgConditionVo;
import com.myblog.vo.PageBean;
import com.myblog.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

//UnknownHostException

/**
 *     方法为get和delete请求时的时候，是不会有request body的，只有post和put才有。
 */

@Controller
@PreAuthorize("isAuthenticated()")
public class MessageController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("messages")
    public String toMessage(Model model, HttpSession session){
        User user =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //1-查找侧边栏所有未读消息数
        int unreadCommentCount = notificationService.findUSerTypeMsgCountByStatus(user.getNickname(),NotificationTypeEnum.COMMENT_BLOG.getType(),0);
        int unreadReplyCount = notificationService.findUSerTypeMsgCountByStatus(user.getNickname(),NotificationTypeEnum.REPLY_COMMENT.getType(),0);
        int unreadPraiseCount = notificationService.findUSerTypeMsgCountByStatus(user.getNickname(),NotificationTypeEnum.PRAISE.getType(),0);


        //
        model.addAttribute("unreadComCount",unreadCommentCount);
        model.addAttribute("unreadRepCount",unreadReplyCount);
        model.addAttribute("unreadPraCount",unreadPraiseCount);

        //更新总未读数
        session.setAttribute("unreadCount",unreadCommentCount+unreadReplyCount+unreadPraiseCount);

        return "message/message";
    }


    //查找所有消息
    @GetMapping("/msg/{type}")
    public String toCommentMSG(@PathVariable("type") String type, @RequestParam(defaultValue = "1") int currentPage, Model model){

        MsgConditionVo msgConditionVo = new MsgConditionVo();
        msgConditionVo.setCurrentPage(currentPage);

        //1-判断消息类型
        if ("comment".equals(type)){
            msgConditionVo.setType(NotificationTypeEnum.COMMENT_BLOG.getType());
        }else if ("reply".equals(type)){
            msgConditionVo.setType(NotificationTypeEnum.REPLY_COMMENT.getType());
        }else if ("praise".equals(type)){
            msgConditionVo.setType(NotificationTypeEnum.PRAISE.getType());
        }else
            return "error";

        //2-消息所属用户
        User user =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        msgConditionVo.setNickanme(user.getNickname());

        //3-
        PageBean<Notification> pageBean =  notificationService.findUserAllMsgByCondition(msgConditionVo);

        //4-统计未读消息
        /*int unreadCount = (int)pageBean.getDataList().stream()
                .filter(e -> e.getStatus() == NotificationStatusEnum.UNREAD.getStatus())
                .count();*/

        //5-
        model.addAttribute("pageBean",pageBean);
        model.addAttribute("type",type);

        return "message/mesCenter.html";
    }

    //删除通知消息
    @DeleteMapping("/msg/delete/{type}")
    @ResponseBody
    public ResultVo<String> deleteMsg(@PathVariable("type") String type,String nickname,Long id){
        //1-判断消息所属的用户是否是当前登陆用户
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getNickname().equals(nickname))
            return ResultVo.errorOf(400,"你无权删除");

        //2-判断删除方式
        int res;
        if ("comment".equals(type)){
            res = NotificationTypeEnum.COMMENT_BLOG.getType();
        }else if ("reply".equals(type)){
            res = NotificationTypeEnum.REPLY_COMMENT.getType();
        }else if ("praise".equals(type)){
            res = NotificationTypeEnum.PRAISE.getType();
        }else
            return ResultVo.errorOf(400,"删除失败");

        try {
            if (id == null){
                notificationService.deleteAllUserMsgByType(user.getNickname(),res);
            }else{
                notificationService.deleteUserMsgByTypeAndId(user.getNickname(),res,id);
            }

            return ResultVo.okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.errorOf(400,"删除失败");

        }
    }

    //标记为已读
    @PostMapping("/msg/read/{id}")
    @ResponseBody
    public ResultVo<String> readMessage(@PathVariable("id")Long id,String receiver){

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getNickname().equals(receiver))
            return ResultVo.errorOf(400,"你无权阅读消息");


        notificationService.readMessage(id);

        return ResultVo.okOf();
    }

    //全部标记已读
    @PostMapping("/msg/read")
    @ResponseBody
    public ResultVo<String> readAllMessage(String type){

        //2-判断删除方式
        int res;
        if ("comment".equals(type)){
            res = NotificationTypeEnum.COMMENT_BLOG.getType();
        }else if ("reply".equals(type)){
            res = NotificationTypeEnum.REPLY_COMMENT.getType();
        }else if ("praise".equals(type)){
            res = NotificationTypeEnum.PRAISE.getType();
        }else
            return ResultVo.errorOf(400,"删除失败");

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();



        notificationService.readAllMessageByUserAndType(user.getNickname(),res);

        return ResultVo.okOf();
    }


}
