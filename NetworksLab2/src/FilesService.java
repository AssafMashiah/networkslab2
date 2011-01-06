import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class FilesService implements IFileService
{
	private static FilesService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();

	private String m_RootDir;
	
	private FilesService() 
	{
	}

	public static synchronized FilesService get_instance() {
		if (m_Instance == null) {
			m_Instance = new FilesService();
		}

		return m_Instance;
	}
	
	public void SetRootDir(String dir)
	{
		m_RootDir = dir;
	}
	
	/**
	 * Calls a function in our commands service
	 * @param functionName The function name to call
	 * @param params The params for this function
	 * @return The output of the function processing
	 * @throws HttpServiceException If there was an internal processing error
	 */
	public String callFunction(Functions functionName, String[] params) throws HttpServiceException 
	{
		StringBuilder retVal;
		
		try
		{
			retVal = new StringBuilder();
			
			switch (functionName) {
			case get_file_sharing_page:
				retVal.append(getFileSharingPage(params[1], Integer.parseInt(params[0])));
				break;
			case download_file:
				retVal.append(downloadFile(params[0]));
				break;
			case get_shared_files:
				retVal.append(getSharedFiles());
				break;
			default:
				// this is a 404 error, it should not happen (right now no code
				// because it cannot happen at this place as we get enum as input)
			}
		}
		catch (Exception e)
		{
			throw new HttpServiceException("Internal error in commands service", e);
		}
		
		return retVal.toString();
	}
	
	private String getFileSharingPage(String targetIP, int targetPort) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		// get the file list on the remote machine
		String fileList;
		try
		{
			FilesServiceProxy proxy = new FilesServiceProxy(targetIP, targetPort);
			fileList = proxy.GetSharedFiles();
		}
		catch (Exception e)
		{
			// proxy operation failed, remove friend
			tracer.TraceToConsole(String.format("Failed to get file sharing page for %s:%d", targetIP, targetPort));
			FriendService.get_instance().RemoveFriend(targetIP, targetPort);
			throw new IOException("Proxy failed!");
		}
		
		// process files
		String[] files = fileList.split(":");
		
		// do we even have any files?
		if (files.length == 0)
		{
			sb.append("Friend is not sharing any files :(");
		}
		else
		{
			sb.append("<table border=1>");
			for (String s : files)
			{
				sb.append("<tr><td>");
				sb.append(s);
				sb.append("</td>");
				sb.append("<td><input type=button value=\"Download File\" onClick=\"javascript:DoDownload(");
				sb.append(s);
				sb.append(")\"></td></tr>");
			}
			sb.append("</table>");
		}
		
		HTMLTemplate t = new HTMLTemplate("file_download.html");
		t.AddValueToTemplate("FILES_TABLE", sb.toString());
		t.AddValueToTemplate("IP", targetIP);
		t.AddValueToTemplate("PORT", Integer.toString(targetPort));
		return t.CompileTemplate();
	}
	
	private String downloadFile(String fileName) throws IOException
	{
		//read file
		byte[] bytes = Lab2Utils.ReadFile(m_RootDir + fileName);
		
		// base64 it
		String basedFile = Base64.encodeToString(bytes, false);
		
		return basedFile;
	}
	
	private String getSharedFiles()
	{
		File folder = new File(m_RootDir);
	    // This filter only returns directories
	    FileFilter fileFilter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.isFile();
	        }
	    };
	    
	    File[] listOfFiles = folder.listFiles(fileFilter);
	    
	    StringBuilder sb = new StringBuilder();
	    
	    if (listOfFiles.length > 0)
	    {
	    	sb.append(listOfFiles[0].getName());
		    for (int i = 1; i < listOfFiles.length; i++) 
		    {
		    	sb.append(":");
		    	sb.append(listOfFiles[i].getName());
		    }
	    }
	    
	    return sb.toString();
	}
}
