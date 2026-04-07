package com.kira.pj.minihome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.kira.pj.main.DBManager;

public class MinihomeDAO {

    // 1. 회원가입 직후, 해당 유저의 PK를 바탕으로 빈 미니홈피 자동 생성
    public int createDefaultMinihome(String userPk) {
        Connection con = null;
        PreparedStatement pstmt = null;

        // INSERT 시에는 PK를 명확히 넣어주어야 한다.
        String sql = "INSERT INTO minihome (h_id, u_pk) VALUES (minihome_seq.NEXTVAL, ?)";

        try {
            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userPk);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("미니홈피 자동 생성 중 오류 발생", e);
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    // 2. [핵심] 사용자의 '아이디(u_id)'로 미니홈피 정보 불러오기 (JOIN 사용)
    public MinihomeDTO getMinihomeByUserId(String userId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MinihomeDTO home = null;

        // URL에서 파라미터로 넘어온 것은 u_id이므로, u_pk를 기준으로 두 테이블을 조인하여 검색한다.
        String sql = "SELECT m.* " +
                "FROM minihome m " +
                "JOIN userReg u ON m.u_pk = u.u_pk " +
                "WHERE u.u_id = ?";

        try {
            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                home = new MinihomeDTO();
                home.setH_id(rs.getInt("h_id"));
                home.setU_pk(rs.getString("u_pk")); // u_pk 저장
                home.setH_title(rs.getString("h_title"));
                home.setH_today(rs.getInt("h_today"));
                home.setH_total(rs.getInt("h_total"));
                home.setH_bgm(rs.getString("h_bgm"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }
        return home;
    }
}