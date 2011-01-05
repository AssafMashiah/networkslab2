import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HTMLTemplate 
{

	private HashMap<String, String> m_TemplateData;
	private String m_html;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	public HTMLTemplate(String fileName) throws IOException
	{
		m_TemplateData = new HashMap<String, String>();
		setHTMLFile(fileName);
		
		tracer.TraceToConsole("New Template Created");
	}

	private void setHTMLFile(String fileName) throws IOException
	{
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        
        tracer.TraceToConsole("Reading Template file");
        
        StringBuffer stringBuffer = new StringBuffer();
        for (String s = reader.readLine(); s != null; s = reader.readLine()) 
        {
        	stringBuffer.append(s);
            stringBuffer.append('\n');
        }
        
        m_html = stringBuffer.toString();
	}
	
	public void AddValueToTemplate(String property, String value)
	{
		m_TemplateData.put(property, value);
	}
	
	public String CompileTemplate()
	{
		tracer.TraceToConsole("Compiling template");
		
		String compiled = GetHtmlTemplate();
		
		for (Map.Entry<String, String> entry : m_TemplateData.entrySet())
		{
			String prop = String.format("[###%s]", entry.getKey());
			compiled = compiled.replace(prop, entry.getValue());
		}
		
		tracer.TraceToConsole("Compile completed");
		
		// we want to return the complied template
		return compiled;
	}
	
	public String GetHtmlTemplate()
	{
		return m_html;
	}
	
	
	
}
