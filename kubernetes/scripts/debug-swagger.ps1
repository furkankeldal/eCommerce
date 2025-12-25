# Debug script for Swagger UI issues
# Usage: powershell -ExecutionPolicy Bypass -File .\debug-swagger.ps1

Write-Host "========================================" -ForegroundColor Green
Write-Host "Swagger UI Debug Script" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# 1. Check port-forwards
Write-Host "1. Port-forward Kontrolu:" -ForegroundColor Yellow
$ports = @(9011, 9012, 9013, 9014, 8090)
$missingPorts = @()
foreach ($port in $ports) {
    $pf = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($pf) {
        Write-Host "   [OK] Port $port aktif" -ForegroundColor Green
    } else {
        Write-Host "   [X] Port $port YOK!" -ForegroundColor Red
        $missingPorts += $port
    }
}

if ($missingPorts.Count -gt 0) {
    Write-Host ""
    Write-Host "Eksik port-forward'lar bulundu!" -ForegroundColor Red
    Write-Host "Port-forward baslatiliyor..." -ForegroundColor Yellow
    
    $userPod = (kubectl get pods -n ecommerce -l app=user-service -o jsonpath='{.items[0].metadata.name}').Trim()
    $productPod = (kubectl get pods -n ecommerce -l app=product-service -o jsonpath='{.items[0].metadata.name}').Trim()
    $stockPod = (kubectl get pods -n ecommerce -l app=stock-service -o jsonpath='{.items[0].metadata.name}').Trim()
    $orderPod = (kubectl get pods -n ecommerce -l app=order-service -o jsonpath='{.items[0].metadata.name}').Trim()
    $gatewayPod = (kubectl get pods -n ecommerce -l app=api-gateway -o jsonpath='{.items[0].metadata.name}').Trim()
    
    # Function to start port-forward as a background job
    function Start-PF-Job {
        param($JobName, $PodName, $Port)
        $existingJob = Get-Job -Name "pf-debug-$JobName" -ErrorAction SilentlyContinue
        if ($existingJob) { return }
        
        Start-Job -Name "pf-debug-$JobName" -ScriptBlock {
            param($PodName, $Port)
            kubectl port-forward -n ecommerce pod/$PodName "${Port}:${Port}"
        } -ArgumentList $PodName, $Port
    }

    if ($missingPorts -contains 9011) {
        Start-PF-Job -JobName "user" -PodName $userPod -Port 9011
    }
    if ($missingPorts -contains 9012) {
        Start-PF-Job -JobName "product" -PodName $productPod -Port 9012
    }
    if ($missingPorts -contains 9013) {
        Start-PF-Job -JobName "stock" -PodName $stockPod -Port 9013
    }
    if ($missingPorts -contains 9014) {
        Start-PF-Job -JobName "order" -PodName $orderPod -Port 9014
    }
    if ($missingPorts -contains 8090) {
        Start-PF-Job -JobName "gateway" -PodName $gatewayPod -Port 8090
    }
    
    Start-Sleep -Seconds 3
    Write-Host "Port-forward'lar arka planda baslatildi!" -ForegroundColor Green
}

Write-Host ""
Write-Host "2. API Gateway Erisim Testi:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8090/" -UseBasicParsing -TimeoutSec 5
    Write-Host "   [OK] API Gateway erisilebilir (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "   [X] API Gateway erisilemiyor: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "3. Swagger UI Erisim Testi:" -ForegroundColor Yellow
$swaggerUrls = @(
    @{Name="User Service"; Url="http://localhost:9011/swagger-ui/index.html"},
    @{Name="Product Service"; Url="http://localhost:9012/swagger-ui/index.html"}
)
foreach ($item in $swaggerUrls) {
    try {
        $response = Invoke-WebRequest -Uri $item.Url -UseBasicParsing -TimeoutSec 5
        Write-Host "   [OK] $($item.Name) erisilebilir" -ForegroundColor Green
    } catch {
        Write-Host "   [X] $($item.Name) erisilemiyor" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "4. API Endpoint Testi:" -ForegroundColor Yellow
$apiEndpoints = @(
    @{Name="User Service"; Url="http://localhost:8090/api/users"},
    @{Name="Product Service"; Url="http://localhost:8090/api/products"}
)
foreach ($endpoint in $apiEndpoints) {
    try {
        $response = Invoke-WebRequest -Uri $endpoint.Url -UseBasicParsing -TimeoutSec 5
        Write-Host "   [OK] $($endpoint.Name) erisilebilir (Status: $($response.StatusCode))" -ForegroundColor Green
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq 404) {
            Write-Host "   [!] $($endpoint.Name) - 404 (Normal, veri yok)" -ForegroundColor Yellow
        } else {
            Write-Host "   [X] $($endpoint.Name) - Hata: $_" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Swagger UI Kullanim Talimatlari" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Swagger UI Adresleri:" -ForegroundColor Yellow
Write-Host "  - User Service:    http://localhost:9011/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host "  - Product Service: http://localhost:9012/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host "  - Stock Service:   http://localhost:9013/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host "  - Order Service:   http://localhost:9014/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "Swagger UI'da Server URL'ini ayarlayin:" -ForegroundColor Yellow
Write-Host "  - User Service:    http://localhost:8090/api/users" -ForegroundColor Cyan
Write-Host "  - Product Service: http://localhost:8090/api/products" -ForegroundColor Cyan
Write-Host "  - Stock Service:   http://localhost:8090/api/stocks" -ForegroundColor Cyan
Write-Host "  - Order Service:   http://localhost:8090/api/orders" -ForegroundColor Cyan
Write-Host ""

