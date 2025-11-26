# Script para generar APKs de PetCareConnect
# Autor: GitHub Copilot
# Fecha: 2025-11-26

Write-Host "`n===================================" -ForegroundColor Cyan
Write-Host "  GENERADOR DE APKs - PetCareConnect" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan

# Limpiar el proyecto
Write-Host "`n[1/3] Limpiando proyecto..." -ForegroundColor Yellow
.\gradlew clean

# Generar APK de Debug
Write-Host "`n[2/3] Generando APK de Debug..." -ForegroundColor Yellow
.\gradlew assembleDebug

# Generar APK de Release
Write-Host "`n[3/3] Generando APK de Release..." -ForegroundColor Yellow
.\gradlew assembleRelease

# Mostrar resultados
Write-Host "`n===================================" -ForegroundColor Green
Write-Host "  APKs GENERADAS EXITOSAMENTE" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green

if (Test-Path "app\build\outputs\apk\debug\app-debug.apk") {
    $debugSize = (Get-Item "app\build\outputs\apk\debug\app-debug.apk").Length / 1MB
    Write-Host "`nAPK DEBUG:" -ForegroundColor Cyan
    Write-Host "  Ubicacion: app\build\outputs\apk\debug\app-debug.apk"
    Write-Host "  Tamano: $([math]::Round($debugSize, 2)) MB"
}

if (Test-Path "app\build\outputs\apk\release\app-release.apk") {
    $releaseSize = (Get-Item "app\build\outputs\apk\release\app-release.apk").Length / 1MB
    Write-Host "`nAPK RELEASE:" -ForegroundColor Green
    Write-Host "  Ubicacion: app\build\outputs\apk\release\app-release.apk"
    Write-Host "  Tamano: $([math]::Round($releaseSize, 2)) MB"
}

Write-Host "`n===================================" -ForegroundColor Cyan
Write-Host "Presiona cualquier tecla para abrir la carpeta de APKs..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
explorer app\build\outputs\apk

