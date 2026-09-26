/*******************************************************************************
 ** Example process step demonstrating business logic implementation.
 **
 ** This shows:
 ** - Standard process step patterns
 ** - Input/output handling
 ** - Logging conventions
 *******************************************************************************/
package com.kingsrook.qbits.example.processes;


import java.util.List;
import com.kingsrook.qqq.backend.core.actions.processes.BackendStep;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


public class ExampleProcessStep implements BackendStep
{
   private static final QLogger LOG = QLogger.getLogger(ExampleProcessStep.class);



   /*******************************************************************************
    ** Run the process step.
    *******************************************************************************/
   @Override
   public void run(RunBackendStepInput input, RunBackendStepOutput output) throws QException
   {
      List<QRecord> records = input.getRecords();
      LOG.info("Processing records", logPair("count", records.size()));

      for(QRecord record : records)
      {
         ///////////////////////////////////////////////////////////////////////
         // Example business logic - update status                            //
         ///////////////////////////////////////////////////////////////////////
         String currentStatus = record.getValueString("status");
         if("PENDING".equals(currentStatus))
         {
            record.setValue("status", "PROCESSED");
         }

         output.addRecord(record);
      }

      LOG.info("Processing complete", logPair("processed", records.size()));
   }



}
