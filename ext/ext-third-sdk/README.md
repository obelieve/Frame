# ext-third-sdk

## PayManager
- 微信支付
```java
//微信支付调用
public void weChatPay(final Activity activity, String appId, final String sign);
```
- 支付宝支付
```java
public void aliPay(final Activity activity, final String sign);
```
## 七牛云上传
```java
//单文件上传
public void upload(final File file, final Callback callback, Activity activity);
//多文件上传
public void upload(List<File> fileList, final Callback callback, Activity activity);
```