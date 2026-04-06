CREATE TABLE bgm_track (
                           track_id    NUMBER PRIMARY KEY,
                           title       VARCHAR2(50 char) NOT NULL,
                           youtube_id  VARCHAR2(20 char)  NOT NULL,
                           duration    NUMBER        DEFAULT 0,
                           track_order NUMBER        DEFAULT 0,
                           u_pk     VARCHAR2(15 char) NOT NULL
);

CREATE SEQUENCE bgm_track_seq START WITH 1 INCREMENT BY 1;

INSERT INTO bgm_track VALUES (bgm_track_seq.NEXTVAL, 'Needygirl Overdose',       'BnkhBwzBqlQ', 214, 1, 'DongMin');
INSERT INTO bgm_track VALUES (bgm_track_seq.NEXTVAL, '차가운 상어 아가씨',          'wZlv3qDPfjk', 155, 2, 'DongMin');
INSERT INTO bgm_track VALUES (bgm_track_seq.NEXTVAL, '처형박수 (Execution Clap)', 'YcxhmHEykPg', 194, 3, 'DongMin');

COMMIT;

select * from bgm_track;

drop table bgm_track;