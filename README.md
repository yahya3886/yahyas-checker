# Yahya's Checker

**Yahya's Checker** (`org.yahya3886.rootchecker`), Android cihazlarda root (su) erişimini kontrol etmek için geliştirilmiş hafif bir uygulamadır. Arayüz **Material 3 (Expressive)** bileşenleri, dinamik renk (Android 12+) ve sistem teması ile tasarlanmıştır.

## Özellikler
- Ana ekranda hızlı root kontrolü (otomatik kontrol + yenile)
- `su` yollarını ve `which su` çıktısını tarama
- `su -c id` ve `su -v` ile root durumu/sürüm ayrıntıları
- **Ayarlar** ekranı ve **Hakkında** bölümü (uygulama bilgisi, sürüm, lisans/atıf)

## Tasarım
- Material 3 / Material 3 Expressive bileşenleri (`LargeTopAppBar`, tonlu kartlar, dinamik renk şeması)
- Daha önce MIUIX arayüzüyle geliştirilen uygulama, bu sürümde Material 3 Expressive tasarıma geçirilmiştir.

## İndirme
APK dosyaları repo içinde tutulmaz; derlenmiş APK’lar [Releases](https://github.com/yahya3886/yahyas-checker/releases) bölümünden (prerelease dahil) indirilebilir.

## Derleme
- Android SDK (platform/build-tools 34+), JDK 17, Gradle 8.7+
- Örnek: `./gradlew assembleDebug`

```bash
./gradlew assembleDebug
# çıktı: app/build/outputs/apk/debug/app-debug.apk
```

## Lisans ve atıf
- Bu proje **GNU General Public License v3.0 (GPL-3.0)** ile lisanslanmıştır. Bkz. [LICENSE](LICENSE).
- Arayüz geliştirilirken [KernelSU (tiann/KernelSU)](https://github.com/tiann/KernelSU) (GPL-3.0) yönetici uygulamasındaki bazı tasarım desenlerinden ilham alınmıştır.
