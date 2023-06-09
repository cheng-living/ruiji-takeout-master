# 瑞吉外卖

## 一、项目介绍
瑞吉外卖是专门为餐饮企业定制的一款软件产品，包括系统管理后台和移动端应用两部分。
系统管理后台主要提供给餐饮企业内部员工使用，可以对餐厅的分类、菜品、套餐、订单、员工等进行管理维护。
移动端应用主要提供给消费者使用，可以在线浏览菜品、添加购物车、下单等。
    
 **本地访问：** 
- 后台管理：localhost:8080/backend/page/login/login.html
- 前端页面：localhost:8080/front/page/login.html


 **后台管理页面的主要功能有：** 
| 功能    | 作用                 |
|-------|--------------------|
| 登录/退出 | 员工进入后台时必须先登录       |
| 员工管理  | 管理员对员工的信息进行增删改查等操作 |
| 分类管理  | 对菜品的分类以及套餐的分类进行增删改查等操作  |
| 菜品管理  | 对每个分类下的菜品进行增删改查等操作          |
| 套餐管理  | 对每个套餐的信息进行增删改查等操作            |
| 订单明细  | 对用户在移动端下的订单信息进行查询、取消、派送、完成，以及订单报表下载等操作|

 **用户页面的主要功能有：** 
| 功能 | 作用 |
|----|----|
| 登录/退出  | 用户点餐时必须先登录          |
| 菜单       | 在点餐界面展示出菜品/套餐分类，并根据当前选择的分类加载其中的菜品信息|
| 购物车     | 用户选中的菜品会加入用户的购物车,用户可以对购物车的订单进行增删改查等操作  |
| 订单支付   | 用户选完菜品/套餐后,可以对购物车菜品进行结算支付  |
| 个人信息   | 在个人中心页面中展示当前用户的基本信息,用户可以管理收货地址,查询历史订单数据等操作   |
## 二、技术选型
| 阶段    | 所需技术                              |
|------- |--------------------|
| 用户层  | H5、VUE.js、ElementUI、微信小程序
| 网关层  | Nginx                                                       |
| 应用层  | SpringBoot、SpringMVC、SpringSession、SpringSwagger、Lombok |
| 数据层  | Mysql、Mybatis、MybatisPlus、Redis              |
| 其他工具| Git/Github、Maven、Junit                                      |
## 三、其他说明
正在编写。。。。。

