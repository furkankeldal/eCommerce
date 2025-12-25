# Fallback Test Script
# Order Service'i kapatıp User Service'ten sipariş getirmeyi test eder

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Fallback Test Senaryosu" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Order Service'i kapat
Write-Host "1. Order Service kapatılıyor..." -ForegroundColor Yellow
kubectl scale deployment/order-service --replicas=0 -n ecommerce

Write-Host "Order Service kapatıldı!" -ForegroundColor Green
Write-Host ""

# 2. Pod durumunu kontrol et
Write-Host "2. Pod durumları:" -ForegroundColor Yellow
kubectl get pods -n ecommerce | Select-String "order-service|user-service"
Write-Host ""

# 3. User Service'in çalıştığını kontrol et
Write-Host "3. User Service durumu:" -ForegroundColor Yellow
$userServicePods = kubectl get pods -n ecommerce -l app=user-service -o jsonpath='{.items[*].status.phase}'
if ($userServicePods -match "Running") {
    Write-Host "User Service çalışıyor!" -ForegroundColor Green
} else {
    Write-Host "User Service çalışmıyor!" -ForegroundColor Red
}
Write-Host ""

# 4. Test isteği örneği
Write-Host "4. Test için istek örneği:" -ForegroundColor Yellow
Write-Host "   GET http://localhost:8090/api/users/1/orders" -ForegroundColor Gray
Write-Host ""
Write-Host "   Beklenen sonuç: [] (boş liste - fallback devreye girmeli)" -ForegroundColor Gray
Write-Host ""

# 5. Logları kontrol et
Write-Host "5. User Service loglarını kontrol etmek için:" -ForegroundColor Yellow
Write-Host "   kubectl logs -n ecommerce -l app=user-service --tail=50 | Select-String -Pattern 'Fallback|fallback|Order Service'" -ForegroundColor Gray
Write-Host ""

# 6. Order Service'i tekrar başlatmak için
Write-Host "6. Order Service'i tekrar başlatmak için:" -ForegroundColor Yellow
Write-Host "   kubectl scale deployment/order-service --replicas=2 -n ecommerce" -ForegroundColor Gray
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test hazır! İsteği atabilirsiniz." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

