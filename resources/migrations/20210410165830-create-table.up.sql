--;; 用户表
create table t_user (
  id varchar(40) primary key,
  username varchar(32),
  password varchar(200),
  realname varchar(20),
  email varchar(200),
  remark varchar(500),
  delete_flag BOOLEAN,
  create_user_id varchar(200),
  update_user_id varchar(200)
);

--;; 分类表
create table t_category (
  id varchar(40) primary key,
  category_name varchar(50),
  category_code varchar(50),
  remark varchar(500),
  delete_flag BOOLEAN,
  create_user_id varchar(200),
  update_user_id varchar(200)
);

--;; 物品表
create table t_item (
  id varchar(40) primary key,
  category_id varchar(40),
  item_name varchar(200),
  item_code varchar(50),
  item_price float,
  item_sort int,
  remark varchar(500),
  delete_flag BOOLEAN,
  create_user_id varchar(200),
  update_user_id varchar(200)
);

--;;
CREATE TABLE user
(
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (id)
);
