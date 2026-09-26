# Processes

Start with [ExampleProcessMetaDataProducer](../src/main/java/com/kingsrook/qbits/example/processes/ExampleProcessMetaDataProducer.java) and [ExampleProcessStep](../src/main/java/com/kingsrook/qbits/example/processes/ExampleProcessStep.java). The producer defines the process, its table, inputs, and backend step; the step transforms `PENDING` records to `PROCESSED` and returns them in the output.

In QQQ 4.0 and 4.1, import `MetaDataProducerInterface` from
`com.kingsrook.qqq.backend.core.model.metadata`. Define backend-step inputs
with `withInputData(new QFunctionInputMetaData().withField(...))`. The scaffold
uses `BackendStep.run(RunBackendStepInput, RunBackendStepOutput)` and adds each
updated record to the output. Keep that contract when renaming the step.

To make the process discoverable, register its metadata first, then pass the
registered process and tables as children to
`QAppMetaData.withSectionOfChildren(section, children)` before calling
`qInstance.addApp(...)`. This populates both the section references and the
app's child metadata required by validation. Use the actual registered names
if you introduce a prefix. Add tests for record changes and failure cases
when replacing the example business logic.
