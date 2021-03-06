package xin.pxyu.servlet;

import net.sf.json.JSONObject;
import xin.pxyu.dao.SuggestionDao;
import xin.pxyu.dao.SystemMessageDao;
import xin.pxyu.json.ListObject;
import xin.pxyu.json.StatusObject;
import xin.pxyu.model.News;
import xin.pxyu.model.Suggestion;
import xin.pxyu.model.SystemMessage;
import xin.pxyu.service.NewsService;
import xin.pxyu.service.SystemMessageService;
import xin.pxyu.service.UserService;
import xin.pxyu.util.JackJsonUtils;
import xin.pxyu.util.ResponseUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class newSuggestion extends HttpServlet{
    public newSuggestion(){}
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"newSuggestion接口收到"+request.getRemoteAddr()+"的请求");
        try{
            response.setContentType("text/json; charset=utf-8");
            request.setCharacterEncoding("UTF-8");
            BufferedReader br=new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
            String s=null;
            StringBuilder sb=new StringBuilder();
            while((s=br.readLine())!=null){
                sb.append(s);
            }
            JSONObject json=JSONObject.fromObject(sb.toString());
            String topic=json.getString("topic");
            String suggest_id=json.getString("suggest_id");
            Suggestion suggestion=new Suggestion();
           suggestion.setTopic(topic);
           suggestion.setSuggest_id(suggest_id);
            boolean result= SuggestionDao.addSuggestion(suggestion);
            ListObject listObject=new ListObject();
            if(result){
                UserService.userAddCredit(suggest_id);
                UserService.updateRank(suggest_id);
                listObject.setStatusObject(new StatusObject("200","生成建议成功"));
                String responseText= JackJsonUtils.toJson(listObject);
                ResponseUtils.renderJson(response,responseText);
            }else{
                listObject.setStatusObject(new StatusObject("400","生成建议失败"));
                String responseText= JackJsonUtils.toJson(listObject);
                ResponseUtils.renderJson(response,responseText);
            }
        }catch(UnsupportedEncodingException u){
            System.out.println("UnsupportedEncodingException");
            u.printStackTrace();
        }catch(IOException e){
            System.out.println("IOException");
            e.printStackTrace();
        }

    }
}
