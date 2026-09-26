# Widgets and UI

The application producer registers a `QAppMetaData` containing a `QAppSection`. The section references metadata names already registered in the instance; it is not registered directly on `QInstance`.

```java
QAppSection section = new QAppSection()
   .withName("exampleSection")
   .withLabel("Example Application")
   .withIcon(new QIcon().withName("dashboard"));

qInstance.addApp(new QAppMetaData()
   .withName("exampleApp")
   .withLabel("Example Application")
   .withSectionOfChildren(section, List.of(
      qInstance.getTable(ExampleEntity.TABLE_NAME),
      qInstance.getTable(ExampleChildEntity.TABLE_NAME),
      qInstance.getProcess(ExampleProcessMetaDataProducer.NAME))));
```

Use [ExampleAppQBitProducer](../src/main/java/com/kingsrook/qbits/example/ExampleAppQBitProducer.java) as the starting point. Keep these references consistent when enabling/disabling tables or introducing prefixes.

The widget has two parts: [its metadata producer](../src/main/java/com/kingsrook/qbits/example/widgets/ExampleDashboardWidgetMetaDataProducer.java) declares a statistics widget and its renderer, and [its renderer](../src/main/java/com/kingsrook/qbits/example/widgets/ExampleDashboardWidgetRenderer.java) returns a `RenderWidgetOutput` containing `StatisticsData`. Replace the example count with a query or calculation. Choose a widget type that matches the returned data model.

Widgets are registered on `QInstance` separately. `QAppSection` has no
`withWidgets` method in QQQ 4.0 or 4.1, so a widget cannot be added as a
section navigation child. `QAppMetaData.withWidgets(List.of(widgetName))`
associates a registered widget with the app, but rendering and widget
navigation depend on the host UI; the scaffold does not provide a widget
navigation route.

For QQQ 4.0, `AbstractWidgetRenderer` is in `com.kingsrook.qqq.backend.core.actions.dashboard.widgets`; `RenderWidgetInput` and `RenderWidgetOutput` are in `com.kingsrook.qqq.backend.core.model.actions.widgets`.
