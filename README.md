# android-http-lib-based-on-volley
android http lib  based on volley
使用说明：

**一、优化了volley的组织结构，重新定义的包架构更加清晰明了，方便阅读，扩展**

包组织结构图如下所示：

![](https://raw.githubusercontent.com/sddyljsx/android-http-lib-based-on-volley/master/001.png)

base包中包括了volley架构的基础类，抽象类。

impl包中包括了base包中类的具体实现。

process包中包括了网络请求处理队列，缓存处理队列等处理方法类。

ui包中包括了网络图片相关的view类。

utils包中是一些处理工具类。

Http类包含了一系列的静态方法，不同的方法适用于不同的使用场景。

**二、优化了缓存机制。之前的机制为获取到网络请求结果后，先存入缓存，然后返回请求结果；现在的机制为获取到网络请求结果后，存入缓存与返回请求结果放在两个线程中，加快了返回请求结果的速度。**

如下图中红框代码所示，额外添加了CacheWriter类，在这里新开一个线程处理缓存写入IO。
![](https://github.com/sddyljsx/android-http-lib-based-on-volley/blob/master/002.png?raw=true)

**三、扩展的更丰富的功能。轻松实现get，post异步同步访问；与gson结合，轻松处理json文件；网络图片异步加载；上传，下载文件。**

使用方法示例：

初始化： Http.init(getApplicationContext());




