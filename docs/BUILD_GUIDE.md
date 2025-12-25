# Proje Derleme Kılavuzu

## 🛠️ Projeyi Derleme

### Yöntem 1: Tüm Projeyi Derleme (Önerilen)

Proje kök dizininde:

```bash
mvn clean package
```

Bu komut:
- Tüm modülleri temizler (`clean`)
- Tüm servisleri derler
- JAR dosyalarını `target/` klasörlerine oluşturur
- Testleri çalıştırır (eğer test varsa)

### Yöntem 2: Testleri Atlayarak Derleme

```bash
mvn clean package -DskipTests
```

### Yöntem 3: Sadece Belirli Bir Servisi Derleme

```bash
# User Service
cd user-service
mvn clean package

# Product Service
cd product-service
mvn clean package

# Stock Service
cd stock-service
mvn clean package

# Order Service
cd order-service
mvn clean package

# API Gateway
cd api-gateway
mvn clean package
```

## 📦 Derleme Sonrası

Derleme başarılı olduğunda, her servisin `target/` klasöründe JAR dosyası oluşur:

```
user-service/target/user-service-1.0.0.jar
product-service/target/product-service-1.0.0.jar
stock-service/target/stock-service-1.0.0.jar
order-service/target/order-service-1.0.0.jar
api-gateway/target/api-gateway-1.0.0.jar
```

## 🐳 Docker Image'larını Oluşturma

Derleme sonrası Docker image'larını oluşturmak için:

```bash
# Tüm image'ları oluştur
docker-compose build

# Veya build ve başlat
docker-compose up -d --build
```

## ⚙️ Maven Komutları

### Temizleme

```bash
# Tüm modülleri temizle
mvn clean

# Sadece belirli modülü temizle
cd user-service
mvn clean
```

### Test Çalıştırma

```bash
# Tüm testleri çalıştır
mvn test

# Belirli servisin testlerini çalıştır
cd user-service
mvn test
```

### Dependency'leri Yükleme

```bash
# Tüm dependency'leri indir
mvn dependency:resolve
```

### Proje Bilgilerini Görüntüleme

```bash
# Proje yapısını görüntüle
mvn dependency:tree

# Proje bilgilerini görüntüle
mvn help:effective-pom
```

## 🔍 Derleme Sorunları

### Problem: "mvn: command not found"

**Çözüm:**
- Maven'in yüklü olduğundan emin olun
- PATH'e Maven ekleyin
- Veya IDE'nin kendi Maven'ini kullanın

### Problem: Java versiyon hatası

**Çözüm:**
```bash
# Java versiyonunu kontrol et
java -version

# Java 17 olmalı
# JAVA_HOME'u ayarla
export JAVA_HOME=/path/to/java17
```

### Problem: Port zaten kullanılıyor

**Çözüm:**
```bash
# Windows'ta port'u kullanan process'i bul
netstat -ano | findstr :9011

# Linux/Mac'te
lsof -i :9011
```

### Problem: Dependency indirme hatası

**Çözüm:**
```bash
# Maven cache'i temizle
mvn dependency:purge-local-repository

# Yeniden indir
mvn clean install -U
```

## 📝 Derleme Adımları Özeti

1. **Java 17 yüklü olmalı**
2. **Maven 3.6+ yüklü olmalı**
3. **Proje kök dizininde:**
   ```bash
   mvn clean package
   ```
4. **Docker image'larını oluştur:**
   ```bash
   docker-compose build
   ```
5. **Servisleri başlat:**
   ```bash
   docker-compose up -d
   ```

## 🎯 IDE'de Derleme

### IntelliJ IDEA

1. Sağ tık → `Maven` → `Reload Project`
2. `Maven` tool window'dan `Lifecycle` → `clean` → `package`

### Eclipse

1. Projeye sağ tık → `Run As` → `Maven clean`
2. `Run As` → `Maven package`

### VS Code

1. Terminal'de: `mvn clean package`
2. Veya Command Palette → `Java: Clean Java Language Server Workspace`

## ✅ Derleme Başarı Kontrolü

Derleme başarılı olduğunda göreceğiniz mesaj:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  XX.XXX s
[INFO] Finished at: YYYY-MM-DD HH:mm:ss
[INFO] ------------------------------------------------------------------------
```

## 🚀 Hızlı Başlangıç (Tüm Adımlar)

```bash
# 1. Projeyi derle
mvn clean package

# 2. Docker image'larını oluştur
docker-compose build

# 3. Servisleri başlat
docker-compose up -d

# 4. Durumu kontrol et
docker-compose ps

# 5. Logları izle
docker-compose logs -f
```

---

**Not:** İlk derleme daha uzun sürebilir çünkü tüm dependency'ler indirilir. Sonraki derlemeler daha hızlı olacaktır.

