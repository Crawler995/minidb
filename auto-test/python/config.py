"""
先在IDEA中运行一次想要运行的main函数，可以看到在下方的输入框中，
第一行是浅灰色背景、以省略号结尾的输出，单击它，
展开的就是运行此工程相应main函数的命令，拷贝过来放在这里。
"""
minidb_launch_cmd = '"D:/install-dir/Liberica JDK 8/bin/java.exe" "-javaagent:D:/install-dir/IntelliJ IDEA Community Edition 2020.2.1/lib/idea_rt.jar=51362:D:/install-dir/IntelliJ IDEA Community Edition 2020.2.1/bin" -Dfile.encoding=UTF-8 -classpath "D:/install-dir/Liberica JDK 8/jre/lib/charsets.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/access-bridge-64.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/cldrdata.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/dnsns.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/jaccess.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/jfxrt.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/localedata.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/nashorn.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/sunec.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/sunjce_provider.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/sunmscapi.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/sunpkcs11.jar;D:/install-dir/Liberica JDK 8/jre/lib/ext/zipfs.jar;D:/install-dir/Liberica JDK 8/jre/lib/jce.jar;D:/install-dir/Liberica JDK 8/jre/lib/jfxswt.jar;D:/install-dir/Liberica JDK 8/jre/lib/jsse.jar;D:/install-dir/Liberica JDK 8/jre/lib/management-agent.jar;D:/install-dir/Liberica JDK 8/jre/lib/resources.jar;D:/install-dir/Liberica JDK 8/jre/lib/rt.jar;D:/coding/minidb/target/test-classes;D:/coding/minidb/target/classes;C:/Users/11714/.m2/repository/org/antlr/antlr4-runtime/4.7.2/antlr4-runtime-4.7.2.jar;C:/Users/11714/.m2/repository/junit/junit/4.12/junit-4.12.jar;C:/Users/11714/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar;C:/Users/11714/.m2/repository/com/google/protobuf/protobuf-java/3.6.1/protobuf-java-3.6.1.jar;C:/Users/11714/.m2/repository/com/esotericsoftware/kryo/4.0.1/kryo-4.0.1.jar;C:/Users/11714/.m2/repository/com/esotericsoftware/reflectasm/1.11.3/reflectasm-1.11.3.jar;C:/Users/11714/.m2/repository/org/ow2/asm/asm/5.0.4/asm-5.0.4.jar;C:/Users/11714/.m2/repository/com/esotericsoftware/minlog/1.3.0/minlog-1.3.0.jar;C:/Users/11714/.m2/repository/org/objenesis/objenesis/2.5.1/objenesis-2.5.1.jar;C:/Users/11714/.m2/repository/org/projectlombok/lombok/1.18.4/lombok-1.18.4.jar" com.bit.tree.TreeTest'

"""
由minidb产生的相应文件，
目前用途为：运行minidb之前可将这些文件删除，避免单元测试运行结果不一致。
"""
minidb_files = [
    'C:\\Users\\11714\\tree13.minidb'
]