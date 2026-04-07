package com.kira.pj.visitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kira.pj.main.DBManager;

public class VisitorDAO {


    // 1. 발도장 찍기 및 갱신 (Upsert - 오늘 날짜 기준)
    public int upsertVisitor(VisitorDTO dto) {
        Connection con = null;
        PreparedStatement pstmt = null;

        // DB 레벨의 원자적 처리를 위한 MERGE INTO 구문
        // [조건] 동일한 작성자가, 동일한 페이지(owner)에, '오늘' 남긴 기록이 있는가?
        String sql =
                "MERGE INTO visitor_log " +
                        "USING dual " +
                        "ON (v_writer_id = ? AND v_owner_id = ? AND TRUNC(v_date) = TRUNC(SYSDATE)) " +
                        "WHEN MATCHED THEN " +
                        "    UPDATE SET v_emoji = ?, v_date = SYSDATE " +
                        "WHEN NOT MATCHED THEN " +
                        "    INSERT (v_id, v_writer_id, v_owner_id, v_emoji, v_date) " +
                        "    VALUES (visitor_seq.NEXTVAL, ?, ?, ?, SYSDATE)";

        try {
            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);

            // ON 절 파라미터 (조건 검색용)
            pstmt.setString(1, dto.getV_writer_id());
            pstmt.setString(2, dto.getV_owner_id());

            // MATCHED 절 파라미터 (UPDATE용)
            pstmt.setInt(3, dto.getV_emoji());

            // NOT MATCHED 절 파라미터 (INSERT용)
            pstmt.setString(4, dto.getV_writer_id());
            pstmt.setString(5, dto.getV_owner_id());
            pstmt.setInt(6, dto.getV_emoji());

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            // 에러를 은폐하지 않고 상위로 던져 컨트롤러가 실패를 인지하게 만듭니다.
            throw new RuntimeException("발도장 갱신/등록 중 DB 오류 발생", e);
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    // 2. 전체 방문자 목록 조회
    public List<VisitorDTO> getAllVisitors(String ownerId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<VisitorDTO> list = new ArrayList<>();

        String sql =
                "SELECT v_id, v_writer_id, v_owner_id, v_emoji, " +
                        "TO_CHAR(v_date, 'MM.DD AM HH12:MI') as v_date_fmt " +
                        "FROM visitor_log WHERE v_owner_id = ? ORDER BY v_date DESC";

        try {
            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ownerId);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                VisitorDTO v = new VisitorDTO();

                v.setV_id(rs.getInt("v_id"));
                v.setV_writer_id(rs.getString("v_writer_id"));
                v.setV_emoji(rs.getInt("v_emoji"));
                v.setV_date(rs.getString("v_date_fmt"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }

        return list;
    }

    // 3. 최근 방문자 5명 조회 (메인 위젯용)
    public List<VisitorDTO> getRecentVisitors(String ownerId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<VisitorDTO> list = new ArrayList<>();

        String sql =
                "SELECT * FROM (" +
                        "  SELECT v_id, v_writer_id, v_emoji, TO_CHAR(v_date, 'MM.DD') as v_date_fmt " +
                        "  FROM visitor_log WHERE v_owner_id = ? ORDER BY v_date DESC" +
                        ") WHERE rownum <= 5";
        try {

            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ownerId);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                VisitorDTO v = new VisitorDTO();

                v.setV_writer_id(rs.getString("v_writer_id"));
                v.setV_date(rs.getString("v_date_fmt"));
                v.setV_emoji(rs.getInt("v_emoji"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }

        return list;
    }

    // 4. 방문 기록 삭제
    public int deleteVisitor(int vId) {

        Connection con = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM visitor_log WHERE v_id = ?";

        try {

            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, vId);

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    // 5. 페이징 방문자 조회
    public List<VisitorDTO> getVisitorsByPage(String ownerId, int page) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<VisitorDTO> list = new ArrayList<>();

        int start = (page - 1) * 7 + 1;
        int end = page * 7;

        String sql =
                "SELECT * FROM (" +
                        "  SELECT rownum as rn, t.* FROM (" +
                        "    SELECT v_id, v_writer_id, v_emoji, TO_CHAR(v_date, 'MM.DD AM HH12:MI') as v_date_fmt " +
                        "    FROM visitor_log WHERE v_owner_id = ? ORDER BY v_date DESC" +
                        "  ) t" +
                        ") WHERE rn BETWEEN ? AND ?";


        try {

            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, ownerId);
            pstmt.setInt(2, start);
            pstmt.setInt(3, end);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                VisitorDTO v = new VisitorDTO();

                v.setV_id(rs.getInt("v_id"));
                v.setV_writer_id(rs.getString("v_writer_id"));
                v.setV_date(rs.getString("v_date_fmt"));
                v.setV_emoji(rs.getInt("v_emoji"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }

        return list;
    }
}