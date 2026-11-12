# Editar las pantallas con Scene Builder

1. Abre el proyecto con IntelliJ IDEA usando Temurin 21.0.12.
2. Descarga e instala Gluon Scene Builder (versión compatible con JavaFX 21).
3. En IntelliJ: **Settings → Languages & Frameworks → JavaFX → Path to SceneBuilder**.
4. Localiza `src/main/resources/ni/edu/uam/practicasprofesionales/views` y abre el FXML mediante **Open in Scene Builder**.
5. `principal.fxml`: menú lateral, barra superior, área central (`fx:id=areaContenido`).
6. `inicio.fxml`: tarjetas de resumen y panel de instrucciones. IDs enlazados a `InicioController`.
7. `crud.fxml`: selector de entidades, tabla, filtro, formulario y botones; **no eliminar** `fx:id=tabla`, `fx:id=camposFormulario` o sus botones. Los campos específicos son generados dinámicamente en Java por `CrudController`; modifica `EntityCatalog` para agregar un nuevo campo.
8. `reportes.fxml`: selector de consultas, tabla y exportación CSV. Los textos y disposición pueden cambiarse en Scene Builder.
9. Cambia colores, bordes, fuentes y botones en `styles/app.css`; el CSS está incluido en `PracticasProfesionalesApplication`.
10. Guarda el FXML, ejecuta `mvn javafx:run` y verifica que los botones mantengan sus `onAction` y los `fx:id` coincidan con el controlador.

La ausencia de dependencias JavaFX en el classpath de Scene Builder puede mostrar advertencias visuales; ejecutando desde Maven se resuelven con las dependencias de `pom.xml`.
