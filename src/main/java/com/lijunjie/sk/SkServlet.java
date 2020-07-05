package com.lijunjie.sk;

import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lijunjie
 * @create 2020-06-30 20:55
 */
public class SkServlet extends HttpServlet {
    static String secKillScript = "local userid=KEYS[1];\r\n"
            + "local prodid=KEYS[2];\r\n"
            + "local qtkey='sk:'..prodid..\":qt\";\r\n"
            + "local usersKey='sk:'..prodid..\":usr\";\r\n"
            + "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n"
            + "if tonumber(userExists)==1 then \r\n"
            + "   return 2;\r\n"
            + "end\r\n"
            + "local num= redis.call(\"get\" ,qtkey);\r\n"
            + "if tonumber(num)<=0 then \r\n"
            + "   return 0;\r\n"
            + "else \r\n"
            + "   redis.call(\"decr\",qtkey);\r\n"
            + "   redis.call(\"sadd\",usersKey,userid);\r\n"
            + "end\r\n"
            + "return 1";

    static String secKillScript2 = "local userExists=redis.call(\"sismember\",\"{sk}:0101:usr\",userid);\r\n"
            + " return 1";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、获取请求参数：  商品id ， 用户id(随机生成)
        String pid = req.getParameter("pid");
        String uid = (int)(10000*Math.random())+"";
        //2、加载LUA脚本 并传入获取的参数，交给redis执行   LUA脚本交给redis后会当做一个整体来执行，不会被打断(redis单线程)
        //2.1 使用jedis加载lua脚本字符串
        Jedis jedis = new Jedis("192.168.110.120", 6379);
        String sha1 = jedis.scriptLoad(secKillScript);
        //2.2 使用jedis执行lua脚本
        //参数1：加密过的LUA脚本字符串 ，参数2：脚本执行时需要的参数的数量   参数3：脚本执行时需要参数的实参列表
        Object result = jedis.evalsha(sha1, 2, uid, pid);//返回值脚本执行时return的内容
        long code = (long)result;
        if(code == 1){
            System.out.println(uid+  ": 秒杀成功..");
            resp.getWriter().write("200");
        }else if(code == 0){
            System.err.println("userid为："+uid +" 库存不足...." + jedis.get("sk:"+ pid + ":qt"));
            resp.getWriter().write("10003");
        }else if(code == 2 ){
            System.err.println("userid为："+uid +" 重复秒杀了....");
            resp.getWriter().write("10001");
        }
        jedis.close();

    }
}