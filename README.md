# Yahya's Checker

**Yahya's Checker** (`org.yahya3886.rootchecker`), MIUIX (HyperOS tarzı) arayüzle hazırlanmış basit bir Android root kontrol uygulamasıdır.

## Özellikler
- `su` yollarını ve `which su` çıktısını kontrol eder
- `su -c id` ve `su -v` ile root durumunu/sürümünü doğrular
- Sonucu ve ayrıntıları MIUIX bileşenleriyle gösterir (durum kartı, kaydırılabilir liste, üst çubuk)

## MIUIX ve KernelSU
Arayüz, [Miuix Compose](https://github.com/miuix-kotlin-multiplatform/miuix) (`top.yukonga.miuix.kmp`) ile oluşturulmuştur. "Tam MIUIX" görünüm ve düzen için [KernelSU (tiann/KernelSU)](https://github.com/tiann/KernelSU) yöneticisinin MIUIX arayüz uygulamasından (`manager`) tema/düzen ve bileşen kullanımına dair bazı desenler ve kod parçaları uyarlanmıştır.

- KernelSU: https://github.com/tiann/KernelSU (GPL-3.0)

## İndirme
APK dosyaları repo içinde tutulmaz; derlenmiş APK’lar [Releases](https://github.com/yahya3886/yahyas-checker/releases) bölümünden (prerelease dahil) indirilebilir.

## Derleme
- Android SDK (platform/build-tools 34+), JDK 17, Gradle 8.7+
- `./gradlew assembleDebug` (veya ortamınıza uygun Gradle ile)

```bash
./gradlew assembleDebug
# çıktı: app/build/outputs/apk/debug/app-debug.apk
```

## Lisans
Bu proje MIT lisansı ile sunulmaz; KernelSU’dan uyarlanan bölümler için KernelSU’nun lisansına (GPL-3.0) dikkat edin.
