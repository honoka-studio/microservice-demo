### 生成JWKSource所使用的RSA密钥

在Linux系统上执行：
```shell
# 生成 RSA 私钥 (2048 位)
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out auth-private-key.pem
# 从私钥导出公钥
openssl pkey -in auth-private-key.pem -pubout -out auth-public-key.pem
# 合成
cat auth-private-key.pem > auth-key.pem
echo -e "\n" >> auth-key.pem
cat auth-public-key.pem >> auth-key.pem
```

这会在shell所在的当前目录中生成3个`pem`文件，分别为公钥、私钥和二者均包含的密钥。
