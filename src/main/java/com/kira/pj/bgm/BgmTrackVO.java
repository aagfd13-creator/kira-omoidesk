package com.kira.pj.bgm;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BgmTrackVO {

        private int trackId;
        private String title;
        private String youtubeId;
        private int duration;
        private int trackOrder;
        private String userId;
}
