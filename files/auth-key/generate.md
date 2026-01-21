### 生成JWKSource所使用的密钥（EC P-256）

在Linux系统上执行：
```shell
# 生成 P-256 私钥
openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 -out auth-private-key.pem
# 从私钥导出公钥
openssl pkey -in auth-private-key.pem -pubout -out auth-public-key.pem
# 将公钥内容加入私钥文件中
echo -e "\n$(cat auth-public-key.pem)" >> auth-private-key.pem
```

这会在shell所在的当前目录中生成两个`pem`文件，分别为公钥和私钥。
