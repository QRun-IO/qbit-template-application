/*******************************************************************************
 ** Renderer for the example dashboard widget.
 **
 ** This class produces the data displayed in the widget.
 *******************************************************************************/
package com.kingsrook.qbits.example.widgets;


import java.util.HashMap;
import java.util.Map;
import com.kingsrook.qqq.backend.core.actions.dashboard.AbstractWidgetRenderer;
import com.kingsrook.qqq.backend.core.actions.dashboard.RenderWidgetInput;
import com.kingsrook.qqq.backend.core.actions.dashboard.RenderWidgetOutput;
import com.kingsrook.qqq.backend.core.exceptions.QException;


public class ExampleDashboardWidgetRenderer extends AbstractWidgetRenderer
{
   /*******************************************************************************
    ** Render the widget data.
    *******************************************************************************/
   @Override
   public RenderWidgetOutput render(RenderWidgetInput input) throws QException
   {
      Map<String, Object> data = new HashMap<>();
      data.put("title", "Example Dashboard");
      data.put("description", "This is an example widget for the Application QBit template.");

      /////////////////////////////////////////////////////////////////////////
      // Add chart data, counts, or other dashboard information here         //
      /////////////////////////////////////////////////////////////////////////
      data.put("totalCount", 0);
      data.put("activeCount", 0);

      return new RenderWidgetOutput(data);
   }
}
