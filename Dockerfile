FROM zlyxzq/centos7:v1
VOLUME /tmp
WORKDIR /
COPY target/heimdall-app.jar /root/app/heimdall-app.jar

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV JAVA_HOME /usr/local/java
ENV PATH $PATH:$JAVA_HOME/bin
ENV CLASSPATH .:$JAVA_HOME/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV JAVA_OPTS "-Xmx512M -Xms512M"
ENV SPRING_OPTS "--spring.profiles.active=local"
ENTRYPOINT ["sh","-c","java -Dfile.encoding=UTF-8 ${JAVA_OPTS} -jar /root/app/heimdall-app.jar ${SPRING_OPTS}"]
