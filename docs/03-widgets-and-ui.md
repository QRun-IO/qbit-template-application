# Widgets and UI

Application QBits include dashboard widgets and UI navigation via QAppSection.

## QAppSection (Required)

Every Application QBit must register a QAppSection:

```java
private QAppSection produceAppSection(MyAppConfig config, String qbitName)
{
   return new QAppSection()
      .withName(config.applyPrefix("myApp"))
      .withLabel("My Application")
      .withIcon(new QIcon().withName("dashboard"))
      .withTables(List.of(
         config.applyPrefix(Order.TABLE_NAME),
         config.applyPrefix(OrderLine.TABLE_NAME)
      ))
      .withProcesses(List.of(
         config.applyPrefix(ProcessOrder.NAME)
      ))
      .withWidgets(List.of(
         config.applyPrefix(OrderDashboard.NAME)
      ))
      .withSourceQBitName(qbitName);
}
```

## Dashboard Widgets

Create widgets with metadata producer and renderer:

```java
// MetaData Producer
public class MyWidgetMetaDataProducer implements MetaDataProducerInterface<QWidgetMetaData>
{
   @Override
   public QWidgetMetaData produce(QInstance qInstance)
   {
      return new QWidgetMetaData()
         .withName("myWidget")
         .withLabel("My Dashboard")
         .withType("chart")
         .withCodeReference(new QCodeReference(MyWidgetRenderer.class));
   }
}

// Renderer
public class MyWidgetRenderer extends AbstractWidgetRenderer
{
   @Override
   public RenderWidgetOutput render(RenderWidgetInput input)
   {
      Map<String, Object> data = new HashMap<>();
      data.put("title", "Dashboard");
      // Add chart data, counts, etc.
      return new RenderWidgetOutput(data);
   }
}
```
