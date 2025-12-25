# Tüm servisleri yeniden build edip deploy et

Write-Host "========================================" -ForegroundColor Green
Write-Host "Tüm Servisleri Yeniden Build ve Deploy" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$rootDir = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$services = @("user-service", "product-service", "stock-service", "order-service", "api-gateway")

Write-Host "Build edilecek servisler:" -ForegroundColor Yellow
foreach ($service in $services) {
    Write-Host "  - $service" -ForegroundColor Gray
}
Write-Host ""

$failedServices = @()

foreach ($service in $services) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Building: $service" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    
    $serviceDir = Join-Path $rootDir $service
    
    if (-not (Test-Path $serviceDir)) {
        Write-Host "Dizin bulunamadı: $serviceDir" -ForegroundColor Red
        $failedServices += $service
        continue
    }
    
    # 1. Maven build
    Write-Host "`n1. Maven build başlatılıyor..." -ForegroundColor Yellow
    Push-Location $serviceDir
    mvn clean package -DskipTests
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build başarısız: $service" -ForegroundColor Red
        $failedServices += $service
        Pop-Location
        continue
    }
    
    Write-Host "Build başarılı: $service" -ForegroundColor Green
    
    # 2. Docker image oluştur
    Write-Host "`n2. Docker image oluşturuluyor..." -ForegroundColor Yellow
    docker build -t "$service`:latest" .
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Docker build başarısız: $service" -ForegroundColor Red
        $failedServices += $service
        Pop-Location
        continue
    }
    
    Write-Host "Docker image oluşturuldu: $service" -ForegroundColor Green
    Pop-Location
    
    Write-Host "`n$service tamamlandı!" -ForegroundColor Green
    Write-Host ""
}

# 3. Kubernetes pod'larını restart et
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Kubernetes Pod'ları Restart Ediliyor" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$deployments = @("user-service", "product-service", "stock-service", "order-service", "api-gateway")

foreach ($deployment in $deployments) {
    Write-Host "Restart ediliyor: $deployment" -ForegroundColor Yellow
    kubectl rollout restart deployment/$deployment -n ecommerce
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Restart başarılı: $deployment" -ForegroundColor Green
    } else {
        Write-Host "Restart başarısız: $deployment" -ForegroundColor Red
        $failedServices += $deployment
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Özet" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

if ($failedServices.Count -eq 0) {
    Write-Host "Tüm servisler başarıyla build edildi ve deploy edildi!" -ForegroundColor Green
} else {
    Write-Host "Başarısız servisler:" -ForegroundColor Red
    foreach ($service in $failedServices) {
        Write-Host "  - $service" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Pod durumunu kontrol etmek için:" -ForegroundColor Yellow
Write-Host "  kubectl get pods -n ecommerce" -ForegroundColor Gray
Write-Host ""
Write-Host "Pod'ların hazır olmasını beklemek için:" -ForegroundColor Yellow
Write-Host "  kubectl get pods -n ecommerce -w" -ForegroundColor Gray
Write-Host ""

