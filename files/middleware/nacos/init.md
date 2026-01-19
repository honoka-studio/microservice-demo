1. 访问[http://vm.honoka.de:8849]()，使用nacos进行登录，首次登录会提示生成一个随机密码，建议登录后进行修改。
2. 进入“命名空间”，新建`microservice-demo`命名空间，ID为`d3495abc-4a17-43cb-9c38-b72c4e8b1aba`。
3. 进入“权限控制/用户列表”，新建用户`root`，密码为`root123`。
4. 进入“权限控制/角色管理”，新建角色`ROLE_ROOT`，绑定用户`root`。
5. 进入“权限控制/权限管理”，为角色`ROLE_ROOT`添加`microservice-demo`命名空间的读写权限。
