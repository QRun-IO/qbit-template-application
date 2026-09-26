/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2022.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kingsrook.qbits.example;

import com.kingsrook.qqq.backend.core.actions.processes.BackendStep;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
import java.util.List;
import com.kingsrook.qqq.backend.core.model.metadata.QBackendMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qbits.example.model.ExampleChildEntity;
import com.kingsrook.qbits.example.model.ExampleEntity;
import com.kingsrook.qbits.example.processes.ExampleProcessStep;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExampleAppQBitProducerTest
{
   @Test
   void producesBothTablesAndTheirPossibleValueSources() throws Exception
   {
      QInstance instance = produce();
      assertNotNull(instance.getTable(ExampleEntity.TABLE_NAME));
      assertNotNull(instance.getTable(ExampleChildEntity.TABLE_NAME));
      assertNotNull(instance.getPossibleValueSource(ExampleEntity.TABLE_NAME));
      assertNotNull(instance.getPossibleValueSource(ExampleChildEntity.TABLE_NAME));
      assertEquals(7, QRecordEntity.getFieldList(ExampleEntity.class).size());
      assertEquals(8, QRecordEntity.getFieldList(ExampleChildEntity.class).size());
   }

   @Test
   void exampleProcessStepMatchesRegisteredBackendStepContract()
   {
      assertTrue(BackendStep.class.isAssignableFrom(ExampleProcessStep.class));
   }

   @Test
   void exampleProcessTransformsPendingRecord() throws Exception
   {
      QRecord record = new QRecord().withValue("status", "PENDING");
      RunBackendStepOutput output = new RunBackendStepOutput();
      new ExampleProcessStep().run(new RunBackendStepInput().withRecords(List.of(record)), output);
      assertEquals("PROCESSED", output.getRecords().getFirst().getValueString("status"));
   }

   @Test
   void producedAppOwnsNavigableTablesAndProcess() throws Exception
   {
      QInstance instance = produce();
      var app = instance.getApp("exampleApp");
      assertNotNull(app);
      assertNotNull(app.getChildren());
      assertTrue(app.getChildren().contains(instance.getTable(ExampleEntity.TABLE_NAME)));
      assertTrue(app.getChildren().contains(instance.getTable(ExampleChildEntity.TABLE_NAME)));
      assertTrue(app.getChildren().contains(instance.getProcess("exampleProcess")));
      assertTrue(app.getSections().stream().anyMatch(section ->
         section.getTables().contains(ExampleEntity.TABLE_NAME)
            && section.getProcesses().contains("exampleProcess")));
   }

   private QInstance produce() throws Exception
   {
      QInstance instance = new QInstance();
      instance.addBackend(new QBackendMetaData().withName("rdbms"));
      new ExampleAppQBitProducer()
         .withConfig(new ExampleAppQBitConfig().withBackendName("rdbms"))
         .produce(instance, "regression");
      return instance;
   }
}
