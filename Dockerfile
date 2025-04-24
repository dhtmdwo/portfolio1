# 베이스 이미지 설정 (OpenJDK 17 기준)
FROM openjdk:17-jdk-slim

# JAR 파일을 컨테이너에 복사
COPY build/libs/be12-fin-5verdose-WMTHIS-BE-0.0.1-SNAPSHOT.jar app.jar

# 앱 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]