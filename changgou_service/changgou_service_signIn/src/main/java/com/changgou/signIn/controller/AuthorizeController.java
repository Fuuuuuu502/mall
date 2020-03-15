package com.changgou.signIn.controller;

import com.changgou.signIn.util.GithubPro;
import com.chuanggou.signIn.pojo.AccessToken;
import com.chuanggou.signIn.pojo.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/signIn")
public class AuthorizeController {

    @Autowired
    private GithubPro githubPro;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request){

        AccessToken accessToken = new AccessToken();
        accessToken.setRedirect_uri(redirectUri);
        accessToken.setClient_id(clientId);
        accessToken.setCode(code);
        accessToken.setState(state);
        accessToken.setClient_secret(clientSecret);

        String token = githubPro.getAccessToken(accessToken);
        GithubUser user = githubPro.getUser(token);

        System.out.println("user.getId()：" + user.getId());


        if(user != null){
            //登录成功 写入cookie和session
            request.getSession().setAttribute("user",user);
            return "redirect:/";
        }else{
            //登录失败  重新登录
            return "redirect:/";
        }
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }
}
