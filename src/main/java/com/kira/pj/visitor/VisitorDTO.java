package com.kira.pj.visitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class VisitorDTO {
    private int v_id;
    private String v_writer_pk;
    private String v_owner_pk;
    private int v_emoji;
    private String v_date;
    private String v_writer_nickname;
}