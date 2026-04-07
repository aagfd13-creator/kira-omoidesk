package com.kira.pj.minihome;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class MinihomeDTO {
    private int h_id;
    private String u_pk; // u_id 대신 u_pk로 변경
    private String h_title;
    private int h_today;
    private int h_total;
    private String h_bgm;

}