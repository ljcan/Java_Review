首先Error与Exception都继承自Throwable类。

Error是指正常情况不大可能会出现的错误，一旦发生这类错误，可能会导致程序处于非正常的，不可恢复的错误，因此Error不能捕获，比如OutOfMerroyError之类，
都是Error的子类。

Exception分为可检查的异常与不可检查的异常，可检查的异常必须在代码中显示捕获，比如IOException，FileNotFoundException等异常，都是Exeption的子类。
不可检查的异常即运行时异常，比如NullPointerException,ArrayIndexOutOfBoundsException异常，通常可以根据避免编码逻辑错误来捕获，并不在编译阶段要求强制
捕获。

![](https://github.com/ljcan/Review/blob/master/Java/pictures/1.png)
