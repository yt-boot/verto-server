# JEECG 模块创建命令记录

- 适用场景：在现有工程中快速创建一个新的 jeecg-boot 模块
- 执行位置：D:\github_workspace\verto-server\jeecg-boot-module 目录下执行

推荐（PowerShell，一行命令，参数全部加引号）：

```
mvn -U archetype:generate "-DgroupId=org.jeecgframework.boot3" "-DartifactId=jeecg-module-verto" "-Dversion=3.8.3" "-Dpackage=org.jeecgframework.boot3" "-DarchetypeGroupId=org.jeecgframework.archetype" "-DarchetypeArtifactId=jeecg-boot-archetype" "-DarchetypeVersion=3.0" "-DinteractiveMode=false" "-DarchetypeRepository=https://repo1.maven.org/maven2"
```

PowerShell 多行续行写法（每行末尾使用反引号 `）：

```
mvn -U archetype:generate `
  "-DgroupId=org.jeecgframework.boot3" `
  "-DartifactId=jeecg-module-verto" `
  "-Dversion=3.8.3" `
  "-Dpackage=org.jeecgframework.boot3" `
  "-DarchetypeGroupId=org.jeecgframework.archetype" `
  "-DarchetypeArtifactId=jeecg-boot-archetype" `
  "-DarchetypeVersion=3.0" `
  "-DinteractiveMode=false" `
  "-DarchetypeRepository=https://repo1.maven.org/maven2"
```

注意事项：
- 在 PowerShell 下务必为所有 `-D` 参数加引号，避免参数被误解析或换行截断。
- 指定 `-DarchetypeRepository=https://repo1.maven.org/maven2` 可以绕过部分镜像的 catalog 问题。
- 修改模块名时，只需替换 `-DartifactId=jeecg-module-xxx` 即可；如需统一版本，可调整 `-Dversion=`。

参数说明（保持与当前工程一致）：
- groupId：org.jeecgframework.boot3
- artifactId：jeecg-module-verto（根据需要替换为你的模块名）
- version：3.8.3（与主工程版本保持一致）
- package：org.jeecgframework.boot3
- archetypeGroupId：org.jeecgframework.archetype
- archetypeArtifactId：jeecg-boot-archetype
- archetypeVersion：3.0

创建完成后的目录示例：
- D:\github_workspace\verto-server\jeecg-boot-module\jeecg-module-verto

后续操作：
1) 在父模块 `jeecg-boot-module/pom.xml` 的 `<modules>` 中加入新模块：
   ```xml
   <modules>
     <module>jeecg-module-demo</module>
     <module>jeecg-boot-module-airag</module>
     <module>jeecg-module-verto</module>
   </modules>
   ```
2) 在 `jeecg-boot-module` 目录执行一次构建（可跳过测试）：
   ```
   mvn clean install -DskipTests
   ```

常见问题与排查：
- 报错 `The desired archetype does not exist (org:jeecg-boot-archetype:3)`：
  - 多因参数被换行截断或未加引号导致坐标解析错误；按上面“一行命令”重试。
  - 或镜像 catalog 缓存问题；为命令追加 `-U` 并指定 `-DarchetypeRepository=https://repo1.maven.org/maven2`。
- 日志出现 `MetadataNotFoundException: /archetype-catalog.xml was not found`：
  - 这是镜像仓库的 catalog 不可用提示，通常指定中央仓库即可。

验证成功标志：
- 日志包含 `Project created from Archetype in dir: ...\jeecg-module-<your-module>` 且最终 `BUILD SUCCESS`。