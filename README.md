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

**三、扩展的更丰富的功能。轻松实现get，post异步同步访问；与gson结合，轻松处理json文件；网络图片异步加载；上传，下载小文件。**

使用方法示例：

1、初始化： 

Http.init(getApplicationContext());

2、Http post请求的一个例子：
![](https://github.com/sddyljsx/android-http-lib-based-on-volley/blob/master/003.png?raw=true)

将网络返回的json文件流通过gson直接变为对应的对象，非常方便。

3、get，下载，上传小文件等方法详见Http类。

**四、网络图片请求**

示例代码如下所示：

一个setImageUrl函数搞定一切！
![](https://github.com/sddyljsx/android-http-lib-based-on-volley/blob/master/004.png?raw=true)

效果图：
![](https://github.com/sddyljsx/android-http-lib-based-on-volley/blob/master/005.png?raw=true)

除了RecyclingNetImageView，还有CircleNetImageView，直接将图片转化为圆形样式。当然你也可以自己定制自己喜欢的样式。

效果图：

![](https://github.com/sddyljsx/android-http-lib-based-on-volley/blob/master/006.png?raw=true)

另外，所有的网络图片都继承自RecyclingImageView，有效的实现了Bitmap的自动回收。

**五、不足之处，大家批评指正！**







