# minidb

## 项目规范

- 开发环境

  - Java 8

- Git提交

  遵循[Angular规范](https://www.cnblogs.com/louyifei0824/p/10135450.html)，使用中文`commit`信息。（需提前学习Git基本操作，如`add / commit / push / pull / checkout / branch / merge`……）

  小改动可直接使用`git commit -m`，如：

  ```bash
  git add .
  git commit -m "feat: 新增xxx功能"
  ```

  大改动需要附上改动的详细说明，使用`git commit`进行提交。

- 分支管理

  不允许将改动直接`push`到`master`分支中，每个人单独拉一个分支进行开发，开发完成之后经过`技术总监（待定）`review确认无误之后`merge`到`master`中。

- 编码

  - 注释：中文，遵循`Java doc`注释规范。
  - 其它的基本上可遵循业界Java规范，或者编辑器中安装统一的代码检查插件（待定，候选：阿里Java开发规范插件）。


## 参考资料

- [使用C语言从零开始仿造sqlite](https://cstack.github.io/db_tutorial/)
- [使用C#从零开始编写数据库](https://www.codeproject.com/articles/1029838/build-your-own-database)