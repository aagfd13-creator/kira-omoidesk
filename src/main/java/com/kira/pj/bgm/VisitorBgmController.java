package com.kira.pj.bgm;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "VisitorBgmController", value = "/api/visitor/bgm")
public class VisitorBgmController extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String ownerPk = req.getParameter("ownerPk");

        // DAO에서 가져온 리스트 (이미 VO 안에 userNickname이 채워져 있음)
        List<BgmTrackVO> list = BgmDAO.MDAO.getTracksByUser(ownerPk);

        // 그대로 JSON 전송
        resp.getWriter().print(new Gson().toJson(list));
    }
}