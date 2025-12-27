# Processes

Application QBits include processes for business logic execution.

## Process Structure

```java
public class MyProcessMetaDataProducer implements MetaDataProducerInterface<QProcessMetaData>
{
   public static final String NAME = "myProcess";

   @Override
   public QProcessMetaData produce(QInstance qInstance)
   {
      return new QProcessMetaData()
         .withName(NAME)
         .withLabel("My Process")
         .withTableName(MyEntity.TABLE_NAME)
         .withStepList(List.of(
            new QBackendStepMetaData()
               .withName("execute")
               .withCode(new QCodeReference(MyProcessStep.class))
         ));
   }
}
```

## Process Steps

Implement `AbstractTransformStep` for record processing:

```java
public class MyProcessStep extends AbstractTransformStep
{
   @Override
   public void runOnePage(RunBackendStepInput input, RunBackendStepOutput output)
   {
      for(QRecord record : input.getRecords())
      {
         // Business logic here
         output.addRecord(record);
      }
   }
}
```

## Including in QAppSection

Add processes to your app's navigation:

```java
return new QAppSection()
   .withName("myApp")
   .withTables(List.of(...))
   .withProcesses(List.of(
      config.applyPrefix(MyProcess.NAME)
   ));
```
