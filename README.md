Mín SDK: 21.

100% basado en Kotlin + Diseñado con el kit de herramientas de interfaz de usuario Jetpack Compose.

Corrutinas: Un patrón de diseño de concurrencia que se puede usar en Android para simplificar código que se ejecuta de forma asíncrona.

Flow: Un flujo de datos asíncrono que emite valores secuencialmente y se completa normalmente o con una excepción.

Arquitectura MVVM: Una arquitectura moderna, mantenible y recomendada por Google para aplicaciones.

Componentes de Arquitectura Android: Conjunto de bibliotecas que ayudan a diseñar aplicaciones robustas, testeables y mantenibles.

ViewModel: Almacena datos relacionados con la interfaz de usuario que no se destruyen ante cambios en la UI.

Repositorio: Ubicado en la capa de datos, contiene la lógica de negocio y los datos de la aplicación.

💉 Inyección de Dependencias

Hilt: Implementación sencilla y con menos código repetitivo que Dagger2.

🌐 Redes

Retrofit: Un cliente HTTP seguro para Android y Java.

OkHttp: Un cliente HTTP que realiza solicitudes de red de manera eficiente.

🖼️ Carga de Imágenes

Coil: Una biblioteca para carga de imágenes en Android, respaldada por Corrutinas de Kotlin.

🗐 Paginación

Paging 3: La biblioteca de Paginación ayuda a cargar y mostrar páginas de datos desde un conjunto más grande, ya sea en almacenamiento local o a través de la red.

🏗️ Estructura de Paquetes
text
rickandmorty/      # Paquete raíz  
|  
├─ data/           # Capa de datos  
│  ├─ database/    # Almacenamiento local  
│  ├─ dto/         # Objetos de transferencia de datos para respuestas remotas  
│  ├─ network/     # Servicio de API  
│  ├─ repository/  # Repositorio  
|  
├─ di/             # Módulos de inyección de dependencias  
|  
├─ domain/         # Capa de dominio  
│  ├─ mapper/      # Mapeadores para respuestas de red  
│  ├─ model/       # Modelos de UI  
|  
├─ navigation/     # Navegación en Compose  
|  
├─ ui/             # Capa de presentación  
│  ├─ detail/      # Detalles de personajes  
│  ├─ home/        # Pantalla principal (Personajes y Ubicaciones)  
|  ├─ splash/      # Pantalla de inicio para SDK nivel < 31  
|  ├─ theme/       # Temas de la aplicación  
|  ├─ util/        # Utilidades de UI  
|  
├─ util/           # Utilidades comunes  