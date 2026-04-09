package com.kira.pj.search;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO {
    private String u_id;
    private String u_name;
    private String u_nickname;
    private String u_email;
    private String u_pk;

    public  String toJSON(){
        Gson gson = new Gson();
       return gson.toJson(this);
    }

}
