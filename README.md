# WeixinDemo
微信公众号开发demo项目

## 一、成为微信公众平台开发者（握手）
官网文档：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319

1. 在微信公众平台上填写服务器配置，如下图：
![a895baf9ea6a43cbad3b499646c5993b-image.png](http://ozeauwce0.bkt.clouddn.com//file/2018/3/a895baf9ea6a43cbad3b499646c5993b-image.png) 
**URL**：请填写为"项目根目录/f/wx/core"

**Token**：随意填，但填的值要记下来，之后填写在服务端的配置文件中。

同时要记录下微信公众号的**appId**，**secret**，之后同样需要填写在服务端的配置文件**global.properties**中。

2. 验证消息是否来自微信服务器
WeiXinFrontController接收对应url的get请求，获取参数**signature**、**timestamp**、**nonce**和**echostr**。根据**signature**、**timestamp**、**nonce**三个参数调用SignUtil进行验证请求是否来自微信。校验流程如下：
(1) 将**token**、**timestamp**、**nonce**三个参数进行字典序排序 
(2) 将三个参数字符串拼接成一个字符串进行sha1加密 
(3) 开发者获得加密后的字符串可与**signature**对比，标识该请求来源于微信

3. 返回对应的随机字符串**echostr**，表示验证成功。

## 二、消息与事件通知，返回信息给用户
握手成功，成为开发者之后，用户每次向微信公众号发送消息、或者产生自定义菜单、或产生微信支付订单等情况时，开发者填写的服务器配置URL将得到微信服务器推送过来的消息和事件，开发者可以依据自身业务逻辑进行响应，如回复消息。

1. 接收微信服务器推送的消息和事件。WeiXinFrontController接收对应url的post请求。
2. ProcessRequest解析消息，并将消息和事件分类，交给对应的输入信息Handler进行处理。
3. 对应类别信息处理Handler中执行业务逻辑。
4. 若要返回信息给用户，则调用输出信息Handler进行返回信息构造，并返回信息给微信服务器即可。

## 三、全局定时获取access_token和jsapi_ticket
官网文档：

获取access_token：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183

获取jsapi_ticket：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141115

access_token和jsapi_ticket的有效期目前都为2个小时，故需要定时刷新。

定时任务类ObtainAccessTokenScheduler中定时使用appId和secret调用微信接口获取access_token的值，使用access_token调用微信接口获取jsapi_ticket的值。

其他微信api调用待继续更新完善。
