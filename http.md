## HTTP

### cookie 与 session

### 一个HTTP请求的经理

1. 输入 URL 后，会先进行域名解析。优先查找本地 host 文件有无对应的 IP地址，没有的话去本地 DNS 服务器查找，还不行的话，本地服务器会去找根 DNS 要一个域服务器的地址进行查询， 域服务器将要查询的域名的解析服务器地址返回给本地 DNS，本地 DNS 去这里查询就可以了
2. 浏览器拿到服务器的 IP 地址后，会向它发送 HTTP 请求。HTTP 请求经由一层层的处理、封装、发出之后，最终经由网络到达服务器，建立 TCP/IP 连接，服务器接收到请求并开始处理。
3. 服务器构建响应，再经一层层的处理、封装、发出后，到达客户端，浏览器处理请求。
4. 浏览器开始渲染页面，解析 HTML，构建 render 树，根据 render 树的节点 和  CSS 的对应关系，进行布局，绘制页面。