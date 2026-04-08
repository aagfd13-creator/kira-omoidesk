create table diary_test(
    d_no int primary key,
    d_id varchar2(50) not null ,
    d_date Date not null ,
    d_title varchar2(100) not null ,
    d_txt varchar2(1000) not null ,
    d_created_at Date not null
);

create sequence diary_test_seq;

SELECT * FROM diary_test;

-- 일기 테이블에 '공개 범위(d_visibility)' 칸을 추가합니다.
-- 0-나만보기 1-친구만 공개 2- 전체공개
-- 기본값(DEFAULT)은 2(전체 공개)로 설정합니다.
ALTER TABLE diary_test ADD (d_visibility NUMBER(1) DEFAULT 2);