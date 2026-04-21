# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Thiết lập thư mục làm việc bên trong container 
WORKDIR /app

#Copy file quản lý thư viện trước 
COPY pom.xml .

# Tải các dependencies trước để tận dụng Docker cache
RUN mvn dependency:go-offline

# Copy toàn bộ mã nguồn Java vào container 
COPY src ./src

# Build ứng dụng thành file .jar, bỏ qua test để chạy nhanh hơn
RUN mvn clean package -DskipTests

# Run stage
# Sử dụng JRE (Java Runtime Environment) trên nền Alpine Linux.
# Alpine giúp giảm kích thước image từ >500MB xuống còn khoảng ~150MB.
FROM eclipse-temurin:17-jre-alpine

# Thiết lập thư mục làm việc 
WORKDIR /app

# Lấy file .jar đã biên dịch từ giai đoạn 'build' (ở trên) bỏ vào đây.
# Lưu ý: Lệnh copy này tổng quát (*.jar), nó sẽ lấy file jar kết quả trong thư mục target.
COPY --from=builder /app/target/*.jar app.jar

# Khai báo cổng 8080 (Cổng mặc định của Spring Boot)
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
# Tương đương lệnh: java -jar app.jar 
ENTRYPOINT ["java", "-jar", "app.jar"]
