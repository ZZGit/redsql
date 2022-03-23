--;;
INSERT INTO t_user
(id, username, password, realname, email, remark, delete_flag, create_user_id, update_user_id)
VALUES
('1', 'lizhaoyu','lizhaoyu', '李照宇', 'lizhaoyu@hc.com', '', false, '0', '0'),
('2', 'mahaiqiang', 'mahaiqiang', '马海强', 'mahaiqiang@hc.com', '', false, '0', '0'),
('3', 'yuzhuangzhuang', 'yuzhuangzhuang', '于壮壮', 'yuzhuangzhuang@hc.com', '', false, '0', '0'),
('4', 'sunxiaoxuan', 'sunxiaoxuan', '孙晓晅', 'sunxiaoxuan@hc.com', '', false, '0', '0'),
('100', 'sundonghe', 'sundonghe', '孙东和', 'sundonghe@hc.com', '', true, '0', '0');

--;;
INSERT INTO
t_category
(id, category_name, category_code, remark, delete_flag, create_user_id, update_user_id)
VALUES
('1', '图书','BOOKS', '图书分类',false, '1', '1'),
('2', '电子','ELECTRON', '电子分类',false, '1', '1'),
('3', '食物','FOODS', '食物分类',true, '1', '1');

--;;
INSERT INTO t_item
(id, category_id, item_name, item_code, item_price, item_sort, remark, delete_flag, create_user_id, update_user_id)
VALUES
('1', '1', '黑客与画家','', 49.00, 0, '', false, '1', '1'),
('2', '1', '三体','', 26.00, 0, '', false, '1', '1'),
('3', '1', '索拉里斯星','', 49.00, 0, '', false, '1', '1'),
('4', '1', '编程珠玑','', 39.00, 0, '', false, '2', '2'),
('5', '1', '人月神话(影印版)','', 25.00, 1, '', false, '2', '2'),
('6', '1', '修改代码的艺术','', 59.00, 1, '', false, '2', '2'),
('7', '1', 'Clojure编程乐趣','', 89.00, 1, '', false, '3', '3'),
('8', '1', 'Clojure高级编程','', 49.80, 1, '', false, '3', '3'),
('9', '1', '高性能Mysql','', 128.00, 2, '', false, '3', '3'),
('10', '1', 'HTML5与WebGL编程','', 79.00, 2, '', false, '3', '3'),
('1000', '1', '卓有成效的程序员','', 20.00, 2, '', true, '3', '3'),
('1001', '1', 'Python编程','', 198.00, 2,'', true, '1', '1');

--;;
INSERT INTO user (id, name, age, email) VALUES
(1, 'Kevin', 18, 'test1@hc.com'),
(2, 'Jack', 20, 'test2@hc.com'),
(3, 'Tom', 28, 'test3@hc.com'),
(4, 'Marvin', 19, 'test4@hc.com'),
(5, 'Serina', 24, 'test5@hc.com');
