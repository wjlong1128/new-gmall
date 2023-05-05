# 环境搭建
1. host
```text
127.0.0.1 file.service.com www.gmall.com item.gmall.com 
127.0.0.1 activity.gmall.com passport.gmall.com cart.gmall.com list.gmall.com 
127.0.0.1 order.gmall.com payment.gmall.com api.gmall.com comment.gmall.com
```
2. maven -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
3. docker run -d -p 9411:9411 openzipkin/zipkin
4. nacos db.url.0=jdbc:mysql://127.0.0.1:3306/gmall-nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
5. minio ```sh docker run -d --name minio -p 9000:9000 -e "MINIO_ROOT_USER=admin" -e "MINIO_ROOT_PASSWORD=admin123456" -v /home/data:d:/gmall/minio/data -v  /home/config:d:/gmall/minio/config minio/minio server /data --console-address ":9001"```

# spu 表关系
![spu](https://raw.githubusercontent.com/wjlong1128/images/main/xczx/202304221708870.png)

![spu](https://raw.githubusercontent.com/wjlong1128/images/main/xczx/202304221712785.png)

# sku
![sku](https://raw.githubusercontent.com/wjlong1128/images/main/xczx/202304231056390.png)

# 流水号 防止重复提交
![](https://raw.githubusercontent.com/wjlong1128/images/main/xczx/202304281415791.png)

// 打包 mvn install -DskipTests
            
