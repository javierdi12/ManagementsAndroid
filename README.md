---

## 📖 Descripción

ManagementsAndroid es una aplicación móvil que permite:
- Listar, crear, editar y borrar estudiantes y cursos.  
- Sincronizar datos con un backend  
- Trabajar offline gracias a Room y OkHttp cache.  
- Mostrar datos en interfaces modernas con Jetpack Compose  

---


## 📑 Requisitos

- Android Studio
- Visual Studio Code
- SDK Android y JDK  

---

## 🚀 Instalación

1. **Clona el repositorio**  
   ```bash
   git clone https://github.com/javierdi12/ManagementsAndroid.git
----

## Instalación de Dependencias

En Android Studio selecciona File → Open y elige la carpeta raíz del proyecto.

Al abrir, haz clic en Sync Now y espera a que descargue:

Room 
Retrofit + OkHttp

Kotlin Coroutines 

Jetpack 

Material Components 

---

## Configuración de la API 
Editar
<string name="base_url">https://api.ejemplo.com/</string>

Sustitúyela por la URL de tu servidor.

----
## Mensajería Push con Firebase Cloud Messaging (Google Services)

Crea y configura tu proyecto en Firebase

Ve a https://console.firebase.google.com y crea un proyecto (por ejemplo “ManagementsAndroid”).

En el panel de tu proyecto, haz clic en “Añadir app” y elige Android.

Pon el nombre de tu paquete (com.moviles.managements)

Descarga el archivo google-services.json que te proporciona Firebase.

Tienes que agregarlo adentro de la carpeta App


## Ejecución de la App
Emulador

Abre AVD Manager en Android Studio y crea/inicia un dispositivo virtual (p. ej. Pixel 4 API 31).

Dispositivo físico

Activa Depuración USB en tu teléfono y conecta por USB.

Haz clic en Run ▶ y selecciona el emulador o dispositivo.

-----
