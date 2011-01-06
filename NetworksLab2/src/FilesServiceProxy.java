import java.io.IOException;
import java.net.UnknownHostException;

public class FilesServiceProxy extends ProxyBase implements IFileService 
{

	public FilesServiceProxy(String targetIP, int targetPort) 
	{
		super(targetIP, targetPort);
	}
	
	public String GetSharedFiles() throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/files_service/get_shared_files");
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
		
		return new String(response.GetContent());
	}
	
	/**
	 * This gets the file from the server and serves it to disk
	 * @param fileName - The name of the file
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws HttpHeaderParsingException
	 * @throws HttpResponseParsingException
	 * @throws HttpProxyException
	 */
	public void DownloadFile(String fileName) throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		// Setting the correct ASCII
		String EncodedfileName = Lab2Utils.URLEncodeString(fileName);
		
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/files_service/download_file", EncodedfileName);
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
		
		// Decoding the file from Base64
		byte[] decodedFile = Base64Coder.decode(new String(response.GetContent()));
		
		// Data on where to save the file
		String fileNameForSaving = String.format("%s%s", FilesService.get_instance().GetRootDir(), fileName);
		
		// The writing of the file
		Lab2Utils.WriteFile(decodedFile, fileNameForSaving);
	}
}
